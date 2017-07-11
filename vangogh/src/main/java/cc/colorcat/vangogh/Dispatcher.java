package cc.colorcat.vangogh;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
class Dispatcher {
    private Handler handler = new Handler(Looper.getMainLooper());

    private ExecutorService executor;
    private Queue<BitmapHunter> waitingHunters = new LinkedList<>();
    private Queue<RealCall> waitingCalls = new ConcurrentLinkedQueue<>();
    private Set<RealCall> executingCalls = new CopyOnWriteArraySet<>();

    private VanGogh vanGogh;

    Dispatcher(VanGogh vanGogh, ExecutorService executor) {
        this.vanGogh = vanGogh;
        this.executor = executor;
    }

    boolean enqueue(BitmapHunter hunter) {
        if (!waitingHunters.contains(hunter) && waitingHunters.add(hunter)) {
            hunter.start();
            RealCall call = new RealCall(vanGogh, hunter.task());
            if (!waitingCalls.contains(call) && waitingCalls.add(call)) {
                promoteTask();
            }
            return true;
        }
        return false;
    }

    private synchronized void promoteTask() {
        if (executingCalls.size() >= vanGogh.maxRunning()) return;
        for (RealCall call = waitingCalls.poll(); call != null; call = waitingCalls.poll()) {
            if (executingCalls.add(call)) {
                executor.submit(new AsyncCall(call));
                if (executingCalls.size() >= vanGogh.maxRunning()) return;
            }
        }
    }

    private void onSuccess(RealCall call, Result result) {
        onFinish(call, result, null);
    }

    private synchronized void onFailure(RealCall call, Exception e) {
        if (call.task().getAndIncrementCount() < vanGogh.retryCount()) {
            if (!waitingCalls.contains(call)) {
                waitingCalls.add(call);
            }
        } else {
            onFinish(call, null, e);
        }
    }

    private void onFinish(final RealCall call, final Result result, final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                final String key = call.task().getKey();
                Iterator<BitmapHunter> iterator = waitingHunters.iterator();
                while (iterator.hasNext()) {
                    BitmapHunter hunter = iterator.next();
                    if (key.equals(hunter.task().getKey())) {
                        if (result != null) {
                            hunter.hunted(result);
                        } else {
                            hunter.failed(e);
                        }
                        hunter.finish();
                        iterator.remove();
                    }
                }
            }
        });
    }

    private class AsyncCall implements Runnable {
        private RealCall call;

        private AsyncCall(RealCall call) {
            this.call = call;
        }

        @Override
        public void run() {
            Result result = null;
            Exception cause = null;
            try {
                result = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
                cause = e;
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                cause = new UnsupportedOperationException("unsupported scheme, uri: " + call.task().getUri());
            } finally {
                executingCalls.remove(call);
                if (result != null) {
                    onSuccess(call, result);
                } else if (cause != null) {
                    onFailure(call, cause);
                }
                promoteTask();
            }
        }
    }
}

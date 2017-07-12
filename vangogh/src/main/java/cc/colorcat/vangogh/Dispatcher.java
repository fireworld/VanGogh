package cc.colorcat.vangogh;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Iterator;
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
    private Queue<Task> waitingTasks = new ConcurrentLinkedQueue<>();
    private Queue<RealCall> waitingCalls = new ConcurrentLinkedQueue<>();
    private Set<RealCall> executingCalls = new CopyOnWriteArraySet<>();

    private VanGogh vanGogh;

    Dispatcher(VanGogh vanGogh, ExecutorService executor) {
        this.vanGogh = vanGogh;
        this.executor = executor;
    }

    boolean enqueue(Task task) {
        Utils.checkMain();
        if (!waitingTasks.contains(task) && waitingTasks.offer(task)) {
            task.onPreExecute();
            RealCall call = new RealCall(vanGogh, task);
            if (!waitingCalls.contains(call) && waitingCalls.offer(call)) {
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
        if (call.task().getAndIncrementExecutedCount() < vanGogh.retryCount()) {
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
                final String key = call.task().stableKey();
                Iterator<Task> iterator = waitingTasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (key.equals(task.stableKey())) {
                        task.onPostResult(result, e);
                        if (result != null || task.getExecutedCount() >= vanGogh.retryCount()) {
                            iterator.remove();
                        }
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
                LogUtils.e(e);
                cause = e;
            } catch (IndexOutOfBoundsException e) {
                cause = new UnsupportedOperationException("unsupported uri: " + call.task().uri());
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

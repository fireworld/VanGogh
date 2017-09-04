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
    private volatile boolean pause = false;

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

    void pause() {
        pause = true;
    }

    void resume() {
        pause = false;
        promoteTask();
    }

    private void promoteTask() {
        RealCall call;
        while (!pause && executingCalls.size() < vanGogh.maxRunning() && (call = waitingCalls.poll()) != null) {
            if (executingCalls.add(call)) {
                executor.submit(new AsyncCall(call));
            }
        }
        LogUtils.i("Dispatcher", "waiting tasks = " + waitingTasks.size()
                + "\n waiting calls = " + waitingCalls.size()
                + "\n executing calls = " + executingCalls.size());
    }

    private void completeCall(final RealCall call, final Result result, final Exception cause) {
        if ((result != null) == (cause != null)) {
            throw new IllegalStateException("dispatcher reporting error.");
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                String stableKey = call.task().stableKey();
                Iterator<Task> iterator = waitingTasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (stableKey.equals(task.stableKey())) {
                        task.onPostResult(result, cause);
                        iterator.remove();
                        LogUtils.d("Dispatcher", "waiting tasks = " + waitingTasks.size()
                                + "\n waiting calls = " + waitingCalls.size()
                                + "\n executing calls = " + executingCalls.size());
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
                LogUtils.e(e);
                cause = new UnsupportedOperationException("unsupported uri: " + call.task().uri());
            } finally {
                executingCalls.remove(call);
                if (result != null || call.getAndIncrement() >= vanGogh.retryCount()) {
                    completeCall(call, result, cause);
                } else if (!waitingCalls.contains(call)) {
                    waitingCalls.offer(call);
                }
                promoteTask();
            }
        }
    }
}

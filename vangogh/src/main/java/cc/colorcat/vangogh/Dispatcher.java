package cc.colorcat.vangogh;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
class Dispatcher {
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ExecutorService executor;
    //    private Queue<Task> waitingTasks = new ConcurrentLinkedQueue<>();
    private final Deque<Task> waitingTasks = new LinkedList<>();
    //    private Queue<RealCall> waitingCalls = new ConcurrentLinkedQueue<>();
    private final Deque<RealCall> waitingCalls = new LinkedList<>();
    //    private Set<RealCall> executingCalls = new CopyOnWriteArraySet<>();
    private final Set<RealCall> executingCalls = new HashSet<>();

    private final VanGogh vanGogh;
    private volatile boolean pause = false;

    Dispatcher(VanGogh vanGogh, ExecutorService executor) {
        this.vanGogh = vanGogh;
        this.executor = executor;
    }

    boolean enqueue(Task task) {
        Utils.checkMain();
        if (!waitingTasks.contains(task) && waitingTasks.offer(task)) {
            task.onPreExecute();
            RealCall call = new RealCall(vanGogh, task);
//            if (!waitingCalls.contains(call) && waitingCalls.offer(call)) {
//                promoteTask();
//            }
            synchronized (waitingCalls) {
                if (!waitingCalls.contains(call) && waitingCalls.offer(call)) {
                    promoteTask();
                }
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
//        promoteTask();
        synchronized (waitingCalls) {
            promoteTask();
        }
    }

    void clear() {
        Utils.checkMain();
        synchronized (waitingCalls) {
            waitingCalls.clear();
            waitingTasks.clear();
//            RealCall call;
//            while ((call = waitingCalls.poll()) != null) {
//                Iterator<Task> iterator = waitingTasks.iterator();
//                while (iterator.hasNext()) {
//                    if (call.task().stableKey().equals(iterator.next().stableKey())) {
//                        iterator.remove();
//                    }
//                }
//            }
        }
    }

    private void promoteTask() {
        RealCall call;
        while (!pause && executingCalls.size() < vanGogh.maxRunning() && (call = waitingCalls.pollLast()) != null) {
            if (executingCalls.add(call)) {
                executor.submit(new AsyncCall(call));
            }
        }
//        LogUtils.i("Dispatcher", "waiting tasks = " + waitingTasks.size()
//                + "\n waiting calls = " + waitingCalls.size()
//                + "\n executing calls = " + executingCalls.size());
    }

    private void completeCall(final RealCall call, final Result result, final Exception cause) {
        if ((result != null) == (cause != null)) {
            throw new IllegalStateException("dispatcher reporting error.");
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                String stableKey = call.task().stableKey();
//                Iterator<Task> iterator = waitingTasks.iterator();
                Iterator<Task> iterator = waitingTasks.descendingIterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (stableKey.equals(task.stableKey())) {
                        task.onPostResult(result, cause);
                        iterator.remove();
//                        LogUtils.d("Dispatcher", "waiting tasks = " + waitingTasks.size()
//                                + "\n waiting calls = " + waitingCalls.size()
//                                + "\n executing calls = " + executingCalls.size());
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
//                executingCalls.remove(call);
//                if (result != null || call.getAndIncrement() >= vanGogh.retryCount()) {
//                    completeCall(call, result, cause);
//                } else if (!waitingCalls.contains(call)) {
//                    waitingCalls.offer(call);
//                }
//                promoteTask();
                synchronized (waitingCalls) {
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
}

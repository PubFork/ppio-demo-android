package io.pp.net_disk_demo.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CancelFixedThreadPool extends ThreadPoolExecutor {

    private static final String TAG = "CancelFixedThreadPool";

    private final int MAX_WAIT_COUNT = 1;

    public CancelFixedThreadPool(int nThreads) {
        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        /*
         * The implementation of LinkedBlockingQueue is thread-safe
         * */
    }

    @Override
    public void execute(Runnable command) {
        if (getQueue().size() == MAX_WAIT_COUNT) {
            getQueue().poll();
        }

        super.execute(command);
    }
}
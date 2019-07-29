package com.example.mdb;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class ExecutorProvider {

    private static ExecutorProvider executorProvider = null;
    public Executor singleExecutor;

    private ExecutorProvider() {
        singleExecutor = Executors.newSingleThreadExecutor();
    }

    public static ExecutorProvider getExecutorProvider() {
        if (executorProvider == null) {
            executorProvider = new ExecutorProvider();
        }
        return executorProvider;
    }


}

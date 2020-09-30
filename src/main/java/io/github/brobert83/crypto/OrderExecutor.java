package io.github.brobert83.crypto;

import lombok.AllArgsConstructor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@AllArgsConstructor
public class OrderExecutor {

    private final ExecutorService executorService;

    public <T> T execute(Callable<T> command) {

        Future<T> task = executorService.submit(command);

        try {
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread execution interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void execute(Runnable command) {

        Future<?> task = executorService.submit(command);

        try {
            task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread execution interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}

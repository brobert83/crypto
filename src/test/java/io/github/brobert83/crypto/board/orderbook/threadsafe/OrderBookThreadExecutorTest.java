package io.github.brobert83.crypto.board.orderbook.threadsafe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookThreadExecutorTest {

    OrderBookThreadExecutor orderBookThreadExecutor;

    @Mock ExecutorService executorService;
    @Mock Callable<Long> callable;
    @Mock Runnable runnable;
    @Mock Future<Long> taskCallable;
    @SuppressWarnings("rawtypes") @Mock Future taskRunnable;

    @Before
    public void setUp() {
         orderBookThreadExecutor = new OrderBookThreadExecutor(executorService);
    }

    @Test
    public void execute_callable() throws Exception {

        //given
        when(executorService.submit(callable)).thenReturn(taskCallable);
        when(taskCallable.get()).thenReturn(111L);

        //when
        long result = orderBookThreadExecutor.execute(callable);

        //then
        verify(executorService).submit(callable);
        assertThat(result).isEqualTo(111L);
    }

    @Test
    public void execute_runnable() throws Exception {

        //given
        //noinspection unchecked
        when(executorService.submit(runnable)).thenReturn(taskRunnable);

        //when
        orderBookThreadExecutor.execute(runnable);

        //then
        verify(executorService).submit(runnable);
        verify(taskRunnable).get();
    }

}
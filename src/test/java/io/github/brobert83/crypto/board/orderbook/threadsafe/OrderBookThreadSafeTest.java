package io.github.brobert83.crypto.board.orderbook.threadsafe;

import io.github.brobert83.crypto.board.model.Level;
import io.github.brobert83.crypto.board.model.Order;
import io.github.brobert83.crypto.board.model.Side;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.unsafe.OrderBookSingleThread;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.TreeSet;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookThreadSafeTest {

    @Mock Symbol symbol;

    @Mock OrderBookThreadExecutor buyExecutor;
    @Mock OrderBookThreadExecutor sellExecutor;

    @Mock OrderBookSingleThread orderBookSingleThread;

    OrderBookThreadSafe orderBookThreadSafe;

    @Mock Order order;
    @Mock TreeSet<Level> sellLevels;
    @Mock TreeSet<Level> buyLevels;

    @Captor ArgumentCaptor<Callable<Order>> callableCaptor;
    @Captor ArgumentCaptor<Runnable> runnableCaptor;

    @Before
    public void setUp() {
        orderBookThreadSafe = new OrderBookThreadSafe(symbol, buyExecutor, sellExecutor, orderBookSingleThread);
    }

    @Test
    public void addOrder_SELL() throws Exception {

        //given
        Order expectedOrder = mock(Order.class);
        when(order.getSide()).thenReturn(Side.SELL);

        //noinspection unchecked
        doReturn(expectedOrder).when(sellExecutor).execute(any(Callable.class));

        //when
        Order actualOrder = orderBookThreadSafe.addOrder(order);

        //then
        verify(sellExecutor).execute(callableCaptor.capture());
        assertThat(actualOrder).isSameAs(expectedOrder);

        {
            //given
            Callable<Order> callable = callableCaptor.getValue();

            //when
            callable.call();

            //then
            verify(orderBookSingleThread).addOrder(order);
        }

    }

    @Test
    public void addOrder_BUY() throws Exception {

        //given
        Order expectedOrder = mock(Order.class);
        when(order.getSide()).thenReturn(Side.BUY);

        //noinspection unchecked
        doReturn(expectedOrder).when(buyExecutor).execute(any(Callable.class));

        //when
        Order actualOrder = orderBookThreadSafe.addOrder(order);

        //then
        verify(buyExecutor).execute(callableCaptor.capture());
        assertThat(actualOrder).isSameAs(expectedOrder);

        {
            //given
            Callable<Order> callable = callableCaptor.getValue();

            //when
            callable.call();

            //then
            verify(orderBookSingleThread).addOrder(order);
        }
    }

    @Test
    public void removeOrder_SELL(){

        //given
        when(order.getSide()).thenReturn(Side.SELL);

        //when
        orderBookThreadSafe.removeOrder(order);

        //then
        verify(sellExecutor).execute(runnableCaptor.capture());

        {
            //given
            Runnable runnable = runnableCaptor.getValue();

            //when
            runnable.run();

            //then
            verify(orderBookSingleThread).removeOrder(order);
        }
    }

    @Test
    public void removeOrder_BUY(){

        //given
        when(order.getSide()).thenReturn(Side.BUY);

        //when
        orderBookThreadSafe.removeOrder(order);

        //then
        verify(buyExecutor).execute(runnableCaptor.capture());

        {
            //given
            Runnable runnable = runnableCaptor.getValue();

            //when
            runnable.run();

            //then
            verify(orderBookSingleThread).removeOrder(order);
        }
    }

    @Test
    public void getSellLevels(){

        //given
        when(orderBookSingleThread.getSellLevels()).thenReturn(sellLevels);

        //when
        TreeSet<Level> actualSellLevels = orderBookThreadSafe.getSellLevels();

        //then
        assertThat(actualSellLevels).isSameAs(sellLevels);
    }

    @Test
    public void getBuyLevels(){

        //given
        when(orderBookSingleThread.getBuyLevels()).thenReturn(buyLevels);

        //when
        TreeSet<Level> actualBuyLevels = orderBookThreadSafe.getBuyLevels();

        //then
        assertThat(actualBuyLevels).isSameAs(buyLevels);
    }

}
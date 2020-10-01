package io.github.brobert83.crypto.board.orderbook.unsafe;

import io.github.brobert83.crypto.board.model.Level;
import io.github.brobert83.crypto.board.model.Order;
import io.github.brobert83.crypto.board.model.Side;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrdersIndex;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookSingleThreadTest {

    @Mock Symbol symbol;
    @Mock OrdersIndex ordersIndex;

    OrderBookSingleThread orderBookSingleThread;

    @Mock Order order;

    @Captor ArgumentCaptor<Long> longCaptor;

    @Before
    public void setUp() {
        orderBookSingleThread = new OrderBookSingleThread(symbol, ordersIndex);
    }

    @Test
    public void addOrder_getLevels() {

        //given
        List<Order> orders = Arrays.asList(
                Order.builder().symbol(symbol).side(Side.SELL).quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build(),
                Order.builder().symbol(symbol).side(Side.SELL).quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build(),
                Order.builder().symbol(symbol).side(Side.SELL).quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                Order.builder().symbol(symbol).side(Side.SELL).quantity(new BigDecimal("3.5")).price(new BigDecimal("13.6")).build(),

                Order.builder().symbol(symbol).side(Side.BUY).quantity(new BigDecimal("10.5")).price(new BigDecimal("14.1")).build(),
                Order.builder().symbol(symbol).side(Side.BUY).quantity(new BigDecimal("12.5")).price(new BigDecimal("14.1")).build(),
                Order.builder().symbol(symbol).side(Side.BUY).quantity(new BigDecimal("111.5")).price(new BigDecimal("15.8")).build()
        );

        //when
        orders.forEach(orderBookSingleThread::addOrder);
        TreeSet<Level> sellLevels = orderBookSingleThread.getSellLevels();
        TreeSet<Level> buyLevels = orderBookSingleThread.getBuyLevels();

        //then
        assertThat(sellLevels)
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("353.6")).price(new BigDecimal("13.6")).build(),
                        Level.builder().quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                        Level.builder().quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build()
                );

        assertThat(buyLevels)
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("111.5")).price(new BigDecimal("15.8")).build(),
                        Level.builder().quantity(new BigDecimal("23.0")).price(new BigDecimal("14.1")).build()
                );
    }

    @Test
    public void removeOrder() {

        //given
        List<Order> orders = Arrays.asList(
                Order.builder().symbol(symbol).side(Side.SELL).quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build(),
                Order.builder().symbol(symbol).side(Side.SELL).quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build(),
                Order.builder().symbol(symbol).side(Side.SELL).quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),

                Order.builder().symbol(symbol).side(Side.BUY).quantity(new BigDecimal("10.5")).price(new BigDecimal("14.1")).build()
        );

        orders.forEach(orderBookSingleThread::addOrder);

        Order order1 = Order.builder().symbol(symbol).side(Side.SELL).quantity(new BigDecimal("3.5")).price(new BigDecimal("13.6")).build();
        Order order2 = Order.builder().symbol(symbol).side(Side.BUY).quantity(new BigDecimal("12.5")).price(new BigDecimal("14.1")).build();
        Order order3 = Order.builder().symbol(symbol).side(Side.BUY).quantity(new BigDecimal("111.5")).price(new BigDecimal("15.8")).build();

        long orderId1_toRemove = orderBookSingleThread.addOrder(order1).getId();
        long orderId2_toRemove = orderBookSingleThread.addOrder(order2).getId();
        long orderId3_toRemove = orderBookSingleThread.addOrder(order3).getId();

        //when
        orderBookSingleThread.removeOrder(order1);
        orderBookSingleThread.removeOrder(order2);
        orderBookSingleThread.removeOrder(order3);

        TreeSet<Level> sellLevels = orderBookSingleThread.getSellLevels();
        TreeSet<Level> buyLevels = orderBookSingleThread.getBuyLevels();

        //then
        assertThat(sellLevels)
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build(),
                        Level.builder().quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                        Level.builder().quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build()
                );

        assertThat(buyLevels)
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("10.5")).price(new BigDecimal("14.1")).build()
                );

        verify(ordersIndex, atLeast(3)).remove(longCaptor.capture());
        assertThat(longCaptor.getAllValues()).containsExactlyInAnyOrder(orderId1_toRemove, orderId2_toRemove, orderId3_toRemove);
    }

}
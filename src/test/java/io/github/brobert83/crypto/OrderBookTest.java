package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.Level;
import io.github.brobert83.crypto.model.Order;
import io.github.brobert83.crypto.model.Side;
import io.github.brobert83.crypto.model.Symbol;
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
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookTest {

    OrderBook orderBook;
    OrderBook orderBookSpy;

    @Mock Symbol symbol;
    @Mock Order order;
    @Mock OrderExecutor sellExecutor;
    @Mock OrderExecutor buyExecutor;

    @Captor ArgumentCaptor<Callable<Long>> callableCaptor;
    @Captor ArgumentCaptor<Runnable> runnableCaptor;

    @Before
    public void setUp() {
        orderBook = OrderBook.builder().sellExecutor(sellExecutor).buyExecutor(buyExecutor).symbol(symbol).build();
        orderBookSpy = spy(orderBook);
    }

    @Test
    public void addOrderUnsafe_uuid() {

        //given
        when(order.getPrice()).thenReturn(BigDecimal.ZERO);
        when(order.getQuantity()).thenReturn(BigDecimal.ZERO);
        when(order.getId()).thenReturn(1234L);

        //when
        long orderId = orderBook.addOrderUnsafe(order);

        //then
        verify(order).setId(anyLong());
        assertThat(orderId).isEqualTo(1234L);
        assertThat(orderBook.getOrders()).containsValue(order);
    }

    @Test
    public void addOrder_SELL() throws Exception {

        //given
        when(order.getPrice()).thenReturn(BigDecimal.ONE);
        when(order.getQuantity()).thenReturn(BigDecimal.ONE);
        when(order.getSide()).thenReturn(Side.SELL);

        //noinspection unchecked
        doReturn(123L).when(sellExecutor).execute(any(Callable.class));

        //when
        long orderId = orderBookSpy.addOrder(order);

        //then
        verify(sellExecutor).execute(callableCaptor.capture());
        assertThat(orderId).isEqualTo(123L);
        {
            //given
            Callable<Long> callable = callableCaptor.getValue();

            //when
            callable.call();

            //then
            verify(orderBookSpy).addOrderUnsafe(order);
        }

    }

    @Test
    public void addOrder_BUY() throws Exception {

        //given
        when(order.getPrice()).thenReturn(BigDecimal.ONE);
        when(order.getQuantity()).thenReturn(BigDecimal.ONE);
        when(order.getSide()).thenReturn(Side.BUY);

        //noinspection unchecked
        doReturn(123L).when(buyExecutor).execute(any(Callable.class));

        //when
        long orderId = orderBookSpy.addOrder(order);

        //then
        assertThat(orderId).isEqualTo(123L);
        verify(buyExecutor).execute(callableCaptor.capture());
        {
            //given
            Callable<Long> callable = callableCaptor.getValue();

            //when
            callable.call();

            //then
            verify(orderBookSpy).addOrderUnsafe(order);
        }
    }

    @Test
    public void add_Order_nulls() {

        //noinspection ConstantConditions
        assertThatThrownBy(() -> orderBook.addOrder(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("order is marked non-null but is null");

        assertThatThrownBy(() -> orderBook.addOrder(Order.builder().quantity(BigDecimal.ZERO).build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order price is null");

        assertThatThrownBy(() -> orderBook.addOrder(Order.builder().price(BigDecimal.ZERO).build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order quantity is null");
    }

    @Test
    public void addOrderUnsafe_getLevels() {

        //given
        List<Order> orders = Arrays.asList(
                Order.builder().side(Side.SELL).quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build(),
                Order.builder().side(Side.SELL).quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build(),
                Order.builder().side(Side.SELL).quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                Order.builder().side(Side.SELL).quantity(new BigDecimal("3.5")).price(new BigDecimal("13.6")).build(),

                Order.builder().side(Side.BUY).quantity(new BigDecimal("10.5")).price(new BigDecimal("14.1")).build(),
                Order.builder().side(Side.BUY).quantity(new BigDecimal("12.5")).price(new BigDecimal("14.1")).build(),
                Order.builder().side(Side.BUY).quantity(new BigDecimal("111.5")).price(new BigDecimal("15.8")).build()
        );

        //when
        orders.forEach(orderBook::addOrderUnsafe);
        TreeSet<Level> sellLevels = orderBook.getSellLevels();
        TreeSet<Level> buyLevels = orderBook.getBuyLevels();

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
    public void removeOrder_SELL(){

        //given
        orderBook.getOrders().put(1L,order);
        when(order.getPrice()).thenReturn(BigDecimal.ONE);
        when(order.getQuantity()).thenReturn(BigDecimal.ONE);
        when(order.getSide()).thenReturn(Side.SELL);

        //when
        orderBookSpy.removeOrder(1L);

        //then
        verify(sellExecutor).execute(runnableCaptor.capture());

        {
            //given
            Runnable runnable = runnableCaptor.getValue();

            //when
            runnable.run();

            //then
            verify(orderBookSpy).removeOrderUnsafe(order);
        }
    }

    @Test
    public void removeOrder_BUY(){

        //given
        orderBook.getOrders().put(1L,order);
        when(order.getPrice()).thenReturn(BigDecimal.ONE);
        when(order.getQuantity()).thenReturn(BigDecimal.ONE);
        when(order.getSide()).thenReturn(Side.BUY);

        //when
        orderBookSpy.removeOrder(1L);

        //then
        verify(buyExecutor).execute(runnableCaptor.capture());

        {
            //given
            Runnable runnable = runnableCaptor.getValue();

            //when
            runnable.run();

            //then
            verify(orderBookSpy).removeOrderUnsafe(order);
        }
    }

    @Test
    public void removeOrderUnsafe() {

        //given
        List<Order> orders = Arrays.asList(
                Order.builder().side(Side.SELL).quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build(),
                Order.builder().side(Side.SELL).quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build(),
                Order.builder().side(Side.SELL).quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),

                Order.builder().side(Side.BUY).quantity(new BigDecimal("10.5")).price(new BigDecimal("14.1")).build()
        );

        orders.forEach(orderBook::addOrderUnsafe);

        Order order1 = Order.builder().side(Side.SELL).quantity(new BigDecimal("3.5")).price(new BigDecimal("13.6")).build();
        Order order2 = Order.builder().side(Side.BUY).quantity(new BigDecimal("12.5")).price(new BigDecimal("14.1")).build();
        Order order3 = Order.builder().side(Side.BUY).quantity(new BigDecimal("111.5")).price(new BigDecimal("15.8")).build();

        long orderId1_toRemove = orderBook.addOrderUnsafe(order1);
        long orderId2_toRemove = orderBook.addOrderUnsafe(order2);
        long orderId3_toRemove = orderBook.addOrderUnsafe(order3);

        //when
        orderBook.removeOrderUnsafe(order1);
        orderBook.removeOrderUnsafe(order2);
        orderBook.removeOrderUnsafe(order3);

        TreeSet<Level> sellLevels = orderBook.getSellLevels();
        TreeSet<Level> buyLevels = orderBook.getBuyLevels();

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

        assertThat(orderBook.getOrders()).doesNotContainKeys(orderId1_toRemove, orderId2_toRemove, orderId3_toRemove);
    }

}
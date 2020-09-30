package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.Level;
import io.github.brobert83.crypto.model.Order;
import io.github.brobert83.crypto.model.Side;
import io.github.brobert83.crypto.model.Symbol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookTest {

    OrderBook orderBook;

    @Mock Symbol symbol;
    @Mock Order order;

    @Before
    public void setUp() {
       orderBook = OrderBook.builder().symbol(symbol).build();
    }

    @Test
    public void addOrder_uuid(){

        //given
        when(order.getPrice()).thenReturn(BigDecimal.ZERO);
        when(order.getQuantity()).thenReturn(BigDecimal.ZERO);
        when(order.getId()).thenReturn(1234L);

        //when
        long orderId = orderBook.addOrder(order);

        //then
        verify(order).setId(anyLong());
        assertThat(orderId).isEqualTo(1234L);
    }

    @Test
    public void add_Order_nulls(){

        //noinspection ConstantConditions
        assertThatThrownBy(() ->  orderBook.addOrder(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("order is marked non-null but is null");

        assertThatThrownBy(() ->  orderBook.addOrder(Order.builder().quantity(BigDecimal.ZERO).build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order price is null");

        assertThatThrownBy(() ->  orderBook.addOrder(Order.builder().price(BigDecimal.ZERO).build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order quantity is null");
    }

    @Test
    public void addOrder_getLevels() {

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
        orders.forEach(orderBook::addOrder);
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

}
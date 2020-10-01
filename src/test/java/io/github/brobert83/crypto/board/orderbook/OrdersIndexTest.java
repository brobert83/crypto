package io.github.brobert83.crypto.board.orderbook;

import io.github.brobert83.crypto.board.model.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrdersIndexTest {

    OrdersIndex ordersIndex = new OrdersIndex();

    @Mock Order order;

    @Test
    public void add_get() {

        //given
        when(order.getId()).thenReturn(123L);

        //when
        ordersIndex.add(order);

        //then
        assertThat(ordersIndex.getOrder(123L)).isSameAs(order);
    }

    @Test
    public void remove() {

        //given
        when(order.getId()).thenReturn(123L);
        ordersIndex.add(order);

        //when
        ordersIndex.remove(123L);

        //then
        assertThat(ordersIndex.getOrder(123L)).isNull();
    }

}
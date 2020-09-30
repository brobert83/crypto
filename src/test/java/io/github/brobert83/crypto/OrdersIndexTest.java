package io.github.brobert83.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class OrdersIndexTest {

    OrdersIndex ordersIndex = new OrdersIndex();

    @Mock OrderBook orderBook;

    @Test
    public void add_getOrderBook() {

        //given
        ordersIndex.add(12L, orderBook);

        //when
        OrderBook actualOrderBook = ordersIndex.getOrderBook(12L);

        //then
        assertThat(actualOrderBook).isEqualTo(orderBook);
    }

}
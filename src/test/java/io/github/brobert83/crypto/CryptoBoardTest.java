package io.github.brobert83.crypto;


import io.github.brobert83.crypto.model.Order;
import io.github.brobert83.crypto.model.Symbol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CryptoBoardTest {

    CryptoBoard cryptoBoard;

    @Mock OrderBooks orderBooks;
    @Mock OrderBook orderBook;
    @Mock Symbol symbol;

    @Before
    public void setUp() {
        cryptoBoard = new CryptoBoard(orderBooks);
    }

    @Test
    public void placeOrder(){

        //given
        Order order = Mockito.mock(Order.class);
        when(order.getSymbol()).thenReturn(symbol);
        when(orderBooks.getOrderBookForSymbol(symbol)).thenReturn(orderBook);
        when(orderBook.addOrder(order)).thenReturn(1234L);

        //when
        long orderId = cryptoBoard.placeOrder(order);

        //then
        assertThat(orderId).isEqualTo(1234L);
        verify(orderBook).addOrder(order);
    }

}
package io.github.brobert83.crypto.board;

import io.github.brobert83.crypto.board.exception.OrderNotFoundException;
import io.github.brobert83.crypto.board.model.BoardSummary;
import io.github.brobert83.crypto.board.model.Order;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrderBook;
import io.github.brobert83.crypto.board.orderbook.OrdersIndex;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CryptoBoardTest {

    @Mock OrdersIndex ordersIndex;
    @Mock OrderBooksIndex orderBooksIndex;
    @Mock Function<Symbol, OrderBook> newOrderBookSupplier;

    CryptoBoard cryptoBoard;

    @Mock Symbol symbol;
    @Mock Order order;
    @Mock OrderBook orderBook;
    @Mock BoardSummary boardSummary;

    long orderId = 234L;

    @Before
    public void setUp() {
        cryptoBoard = new CryptoBoard(ordersIndex, orderBooksIndex, newOrderBookSupplier);
    }

    @Test
    public void addOrder() {

        //given
        Order expectedOrder = mock(Order.class);

        when(order.getSymbol()).thenReturn(symbol);
        when(orderBooksIndex.getOrderBookForSymbol(symbol, newOrderBookSupplier)).thenReturn(orderBook);
        when(orderBook.addOrder(order)).thenReturn(expectedOrder);

        //when
        Order createdOrder = cryptoBoard.addOrder(this.order);

        //then
        assertThat(createdOrder).isSameAs(expectedOrder);
    }

    @Test
    public void removeOrder() {

        //given
        when(ordersIndex.getOrder(orderId)).thenReturn(order);
        when(order.getSymbol()).thenReturn(symbol);
        when(orderBooksIndex.getOrderBookForSymbol(symbol)).thenReturn(orderBook);

        //when
        cryptoBoard.removeOrder(orderId);

        //then
        verify(orderBook).removeOrder(order);
    }

    @Test
    public void removeOrder_orderNotFound() {

        assertThatThrownBy(() -> cryptoBoard.removeOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Cannot delete order, order with id '" + orderId + "' not found");
    }

    @Test
    public void getBoardSummary() {

        //given
        when(orderBooksIndex.getBoardSummary(10)).thenReturn(boardSummary);

        //when
        BoardSummary actualBoardSummary = cryptoBoard.getBoardSummary(10);

        //then
        assertThat(actualBoardSummary).isSameAs(boardSummary);
    }

}

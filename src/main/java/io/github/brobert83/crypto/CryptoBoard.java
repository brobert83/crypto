package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.BoardSummary;
import io.github.brobert83.crypto.model.Order;

public class CryptoBoard {

    private final OrderBooks orderBooks;

    public CryptoBoard(OrderBooks orderBooks) {
        this.orderBooks = orderBooks;
    }

    public long placeOrder(Order order) {

        OrderBook orderBook = orderBooks.getOrderBookForSymbol(order.getSymbol());

        return orderBook.addOrder(order);
    }

    public BoardSummary getSummary() {
        return null;
    }

}

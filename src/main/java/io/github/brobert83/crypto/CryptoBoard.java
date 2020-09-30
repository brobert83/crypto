package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.BoardSummary;
import io.github.brobert83.crypto.model.Order;

public class CryptoBoard {

    private final OrderBooks orderBooks;
    private final OrdersIndex ordersIndex;

    public CryptoBoard(OrderBooks orderBooks, OrdersIndex ordersIndex) {
        this.orderBooks = orderBooks;
        this.ordersIndex = ordersIndex;
    }

    public long placeOrder(Order order) {

        OrderBook orderBook = orderBooks.getOrderBookForSymbol(order.getSymbol());

        long orderId = orderBook.addOrder(order);

        ordersIndex.add(orderId, orderBook);

        return orderId;
    }

    public BoardSummary getBoardSummary() {
        return orderBooks.getBoardSummary();
    }

    public void removeOrder(long orderId) {
        OrderBook orderBook = ordersIndex.getOrderBook(orderId);
        orderBook.removeOrder(orderId);
    }

}

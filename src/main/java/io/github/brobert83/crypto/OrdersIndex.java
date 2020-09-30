package io.github.brobert83.crypto;

import java.util.concurrent.ConcurrentHashMap;

public class OrdersIndex {

    private final ConcurrentHashMap<Long, OrderBook> index = new ConcurrentHashMap<>();

    public void add(long orderId, OrderBook orderBook) {
        index.put(orderId, orderBook);
    }

    public OrderBook getOrderBook(long orderId) {
        return index.get(orderId);
    }

}

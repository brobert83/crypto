package io.github.brobert83.crypto;

import java.util.HashMap;
import java.util.Map;

public class OrdersIndex {

    private final Map<Long, OrderBook> index = new HashMap<>();

    public void add(long orderId, OrderBook orderBook) {
        index.put(orderId, orderBook);
    }

    public OrderBook getOrderBook(long orderId) {
        return index.get(orderId);
    }

}

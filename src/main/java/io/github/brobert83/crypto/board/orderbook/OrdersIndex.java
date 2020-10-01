package io.github.brobert83.crypto.board.orderbook;

import io.github.brobert83.crypto.board.model.Order;
import lombok.NonNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This structure is used to do a order lookup by id necessary for the order remove operation.
 * <br>
 * Operations on this index are not synchronized, because in case of a remove, the orderId will not be available until the 'add' operation has completed,
 * so it is not possible for a remove operation to be scheduled without the 'add' having completed.
 */
public class OrdersIndex {

    // the initial capacity will prevent rehashing until the load factor is reached (75%)
    private final ConcurrentHashMap<Long, Order> index = new ConcurrentHashMap<>(100000);

    public void add(@NonNull Order order) {
        index.put(order.getId(), order);
    }

    public Order getOrder(long orderId) {
        return index.get(orderId);
    }

    public void remove(long orderId){
        index.remove(orderId);
    }

}

package io.github.brobert83.crypto.board;

import io.github.brobert83.crypto.board.exception.OrderNotFoundException;
import io.github.brobert83.crypto.board.model.BoardSummary;
import io.github.brobert83.crypto.board.model.Order;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrderBook;
import io.github.brobert83.crypto.board.orderbook.OrdersIndex;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
public class CryptoBoard {

    @NonNull private OrdersIndex ordersIndex;
    @NonNull private OrderBooksIndex orderBooksIndex;
    @NonNull private Function<Symbol,OrderBook> newOrderBookSupplier;

    /**
     * Creates a order
     *
     * The value used for the symbol is case-insensitive and it does not have to be the same instance
     *
     * @param order, the data object representing a new order
     * @return a data object representing the created order, with the id filled in
     */
    public Order addOrder(@NonNull Order order) {

        OrderBook orderBook = orderBooksIndex.getOrderBookForSymbol(order.getSymbol(), newOrderBookSupplier);

        return orderBook.addOrder(order);
    }

    /**
     * Removes a order
     *
     * @param orderId, the id of the order to be removed
     */
    public void removeOrder(long orderId) {

        Order order = Optional.ofNullable(ordersIndex.getOrder(orderId))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Cannot delete order, order with id '%s' not found", orderId)));

        OrderBook orderBook = orderBooksIndex.getOrderBookForSymbol(order.getSymbol());

        orderBook.removeOrder(order);
    }

    /**
     * Returns the summary of all the orders for all the symbols present in the system.
     * @param count, how many of the top levels should the summary contain
     */
    public BoardSummary getBoardSummary(int count) {
        return orderBooksIndex.getBoardSummary(count);
    }

}

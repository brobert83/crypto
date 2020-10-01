package io.github.brobert83.crypto.board.orderbook.threadsafe;

import io.github.brobert83.crypto.board.model.Level;
import io.github.brobert83.crypto.board.model.Order;
import io.github.brobert83.crypto.board.model.Side;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrderBook;
import io.github.brobert83.crypto.board.orderbook.unsafe.OrderBookSingleThread;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.TreeSet;

import static io.github.brobert83.crypto.board.model.Side.SELL;

/**
 * This class will identify which executor to be used against the requested operation and in turn invoke the single-threaded version of OrderBook to perform the operation.
 * <br>
 * It is achieving the same effect as a synchronized call would.
 */
@AllArgsConstructor
public class OrderBookThreadSafe implements OrderBook {

    @NonNull @Getter private final Symbol symbol;

    @NonNull @Getter private final OrderBookThreadExecutor buyExecutor;
    @NonNull @Getter private final OrderBookThreadExecutor sellExecutor;

    @NonNull private final OrderBookSingleThread orderBookSingleThread;

    private OrderBookThreadExecutor getExecutor(Side side) {
        return side == SELL ? sellExecutor : buyExecutor;
    }

    @Override
    public Order addOrder(@NonNull Order order) {

        OrderBookThreadExecutor sideExecutor = getExecutor(order.getSide());

        return sideExecutor.execute(() -> orderBookSingleThread.addOrder(order));
    }

    @Override
    public void removeOrder(@NonNull Order order) {

        OrderBookThreadExecutor sideExecutor = getExecutor(order.getSide());

        sideExecutor.execute(() -> orderBookSingleThread.removeOrder(order));
    }

    @Override
    public TreeSet<Level> getSellLevels() {
        return orderBookSingleThread.getSellLevels();
    }

    @Override
    public TreeSet<Level> getBuyLevels() {
        return orderBookSingleThread.getBuyLevels();
    }

}

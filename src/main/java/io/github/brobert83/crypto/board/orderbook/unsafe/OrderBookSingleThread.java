package io.github.brobert83.crypto.board.orderbook.unsafe;

import io.github.brobert83.crypto.board.model.Level;
import io.github.brobert83.crypto.board.model.Order;
import io.github.brobert83.crypto.board.model.Side;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrderBook;
import io.github.brobert83.crypto.board.orderbook.OrdersIndex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.brobert83.crypto.board.model.Side.SELL;

/**
 * This is the data class where the order price levels are kept and updated.
 * The `addOrder` and `removeOrder` methods are not meant to be called threaded.
 */
@AllArgsConstructor
public class OrderBookSingleThread implements OrderBook {

    @Getter private final Symbol symbol;

    private static Comparator<Level> sellLevelsComparator = Comparator.comparing(Level::getPrice);
    private static Comparator<Level> buyLevelsComparator = Comparator.comparing(Level::getPrice).reversed();

    @Getter private final TreeSet<Level> sellLevels = new TreeSet<>(sellLevelsComparator);
    @Getter private final TreeSet<Level> buyLevels = new TreeSet<>(buyLevelsComparator);

    private final OrdersIndex ordersIndex;

    private final Function<Side, TreeSet<Level>> getLevels = (side) -> side == SELL ? sellLevels : buyLevels;

    private final Supplier<Long> newUUID = () -> UUID.randomUUID().getMostSignificantBits();

    public Order addOrder(@NonNull Order order) {

        BigDecimal orderPrice = order.getPrice();
        BigDecimal orderQuantity = order.getQuantity();
        Side orderSide = order.getSide();

        TreeSet<Level> sideLevels = getLevels.apply(orderSide);

        Level levelSearch = Level.builder().price(order.getPrice()).quantity(orderQuantity).build();
        Level levelMatch = sideLevels.floor(levelSearch);

        if (levelMatch != null && levelMatch.getPrice().equals(orderPrice)) {
            levelMatch.incrementOrderQuantity(orderQuantity);
        } else {
            sideLevels.add(levelSearch);
        }

        long orderId = newUUID.get();

        order.setId(orderId);

        ordersIndex.add(order);

        return order;
    }

    public void removeOrder(@NonNull Order order) {

        TreeSet<Level> sideLevels = getLevels.apply(order.getSide());

        BigDecimal orderPrice = order.getPrice();
        BigDecimal orderQuantity = order.getQuantity();

        Level levelSearch = Level.builder().quantity(BigDecimal.ZERO).price(orderPrice).build();
        Level levelMatch = sideLevels.floor(levelSearch);

        if (levelMatch != null && levelMatch.getPrice().equals(orderPrice)) {
            if (levelMatch.getQuantity().compareTo(orderQuantity) > 0) {
                levelMatch.incrementOrderQuantity(orderQuantity.negate());
            } else {
                sideLevels.remove(levelMatch);
            }
        }

        ordersIndex.remove(order.getId());
    }

}

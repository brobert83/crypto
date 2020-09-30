package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.Level;
import io.github.brobert83.crypto.model.Order;
import io.github.brobert83.crypto.model.Side;
import io.github.brobert83.crypto.model.Symbol;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.*;

import static io.github.brobert83.crypto.model.Side.SELL;

@Builder
public class OrderBook {

    private Symbol symbol;

    private static Comparator<Level> sellLevelsComparator = Comparator.comparing(Level::getPrice);
    private static Comparator<Level> buyLevelsComparator = Comparator.comparing(Level::getPrice).reversed();

    @Getter private final TreeSet<Level> sellLevels = new TreeSet<>(sellLevelsComparator);
    @Getter private final TreeSet<Level> buyLevels = new TreeSet<>(buyLevelsComparator);

    @Getter private final Map<Long, Order> orders = new HashMap<>();

    private TreeSet<Level> getSideLevels(Side side) {
        return side == SELL ? sellLevels : buyLevels;
    }

    public long addOrder(@NonNull Order order) {

        Optional.ofNullable(order.getPrice()).orElseThrow(() -> new RuntimeException("Order price is null"));
        Optional.ofNullable(order.getQuantity()).orElseThrow(() -> new RuntimeException("Order quantity is null"));

        BigDecimal price = order.getPrice();
        BigDecimal quantity = order.getQuantity();

        TreeSet<Level> sideLevels = getSideLevels(order.getSide());

        Level levelSearch = Level.builder().price(order.getPrice()).quantity(quantity).build();
        Level levelMatch = sideLevels.floor(levelSearch);

        if (levelMatch != null && levelMatch.getPrice().equals(price)) {
            levelMatch.incrementOrderQuantity(quantity);
        } else {
            sideLevels.add(levelSearch);
        }

        long orderId = UUID.randomUUID().getMostSignificantBits();

        order.setId(orderId);
        orders.put(orderId, order);

        return order.getId();
    }

    public void removeOrder(long orderId) {

        Order order = Optional.ofNullable(orders.get(orderId))
                .orElseThrow(() -> new RuntimeException("Order with id '%s' not found"));

        TreeSet<Level> sideLevels = getSideLevels(order.getSide());

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

        orders.remove(orderId);
    }

}

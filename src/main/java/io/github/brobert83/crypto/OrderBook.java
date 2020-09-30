package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.Level;
import io.github.brobert83.crypto.model.Order;
import io.github.brobert83.crypto.model.Side;
import io.github.brobert83.crypto.model.Symbol;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;

import static io.github.brobert83.crypto.model.Side.SELL;

@Builder
public class OrderBook {

    private Symbol symbol;

    private static Comparator<Level> sellLevelsComparator = Comparator.comparing(Level::getPrice);
    private static Comparator<Level> buyLevelsComparator = Comparator.comparing(Level::getPrice).reversed();

    @Getter private final TreeSet<Level> sellLevels = new TreeSet<>(sellLevelsComparator);
    @Getter private final TreeSet<Level> buyLevels = new TreeSet<>(buyLevelsComparator);

    private TreeSet<Level> getSideLevels(Side side) {
        return side == SELL ? sellLevels : buyLevels;
    }

    public long addOrder(@NonNull Order order) {

        Optional.ofNullable(order.getPrice()).orElseThrow(()-> new RuntimeException("Order price is null"));
        Optional.ofNullable(order.getQuantity()).orElseThrow(()-> new RuntimeException("Order quantity is null"));

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

        order.setId(UUID.randomUUID().getMostSignificantBits());

        return order.getId();
    }

}

package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.Order;
import io.github.brobert83.crypto.model.Symbol;
import lombok.Builder;

@Builder
public class OrderBook {

    private Symbol symbol;

    public long addOrder(Order order) {
        return 0;
    }

}

package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.Symbol;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class OrderBooks {

    private final Map<Symbol, OrderBook> orderBooks = new HashMap<>();

    public OrderBook getOrderBookForSymbol(@NonNull Symbol symbol) {
        return orderBooks.computeIfAbsent(symbol, s -> OrderBook.builder().symbol(s).build());
    }

}

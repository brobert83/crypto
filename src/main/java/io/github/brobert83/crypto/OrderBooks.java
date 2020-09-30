package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.BoardSummary;
import io.github.brobert83.crypto.model.Symbol;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderBooks {

    private final Map<Symbol, OrderBook> orderBooks = new HashMap<>();

    public OrderBook getOrderBookForSymbol(@NonNull Symbol symbol) {
        return orderBooks.computeIfAbsent(symbol, s -> OrderBook.builder().symbol(s).build());
    }

    public BoardSummary getBoardSummary() {

        BoardSummary boardSummary = BoardSummary.builder().build();

        orderBooks.forEach((symbol, orderBook) -> {
            boardSummary.getSellOrders().computeIfAbsent(symbol, s -> new ArrayList<>(orderBook.getSellLevels()));
            boardSummary.getBuyOrders().computeIfAbsent(symbol, s -> new ArrayList<>(orderBook.getBuyLevels()));
        });

        return boardSummary;
    }

}

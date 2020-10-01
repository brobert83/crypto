package io.github.brobert83.crypto.board;

import io.github.brobert83.crypto.board.model.BoardSummary;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrderBook;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class OrderBooksIndex {

    private final ConcurrentHashMap<Symbol, OrderBook> orderBooks = new ConcurrentHashMap<>(500);

    public synchronized OrderBook getOrderBookForSymbol(
            @NonNull Symbol symbol,
            @NonNull Function<Symbol,OrderBook> newOrderBookSupplier) {

        return orderBooks.computeIfAbsent(symbol, newOrderBookSupplier);
    }

    public synchronized OrderBook getOrderBookForSymbol(@NonNull Symbol symbol) {
        return orderBooks.get(symbol);
    }

    public BoardSummary getBoardSummary() {

        BoardSummary boardSummary = BoardSummary.builder().build();

        orderBooks.forEach((symbol, orderBook) -> {
            boardSummary.getSellLevels().computeIfAbsent(symbol, s -> new ArrayList<>(orderBook.getSellLevels()));
            boardSummary.getBuyLevels().computeIfAbsent(symbol, s -> new ArrayList<>(orderBook.getBuyLevels()));
        });

        return boardSummary;
    }
}

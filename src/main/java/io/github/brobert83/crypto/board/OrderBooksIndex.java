package io.github.brobert83.crypto.board;

import io.github.brobert83.crypto.board.model.BoardSummary;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrderBook;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class OrderBooksIndex {

    private final ConcurrentHashMap<Symbol, OrderBook> orderBooks = new ConcurrentHashMap<>(500);

    public synchronized OrderBook getOrderBookForSymbol(
            @NonNull Symbol symbol,
            @NonNull Function<Symbol, OrderBook> newOrderBookSupplier) {

        return orderBooks.computeIfAbsent(symbol, newOrderBookSupplier);
    }

    public synchronized OrderBook getOrderBookForSymbol(@NonNull Symbol symbol) {
        return orderBooks.get(symbol);
    }

    public BoardSummary getBoardSummary(int count) {

        BoardSummary boardSummary = BoardSummary.builder().build();

        orderBooks.forEach((symbol, orderBook) -> {
            boardSummary.getSellLevels().computeIfAbsent(symbol, s -> getFirstElements(orderBook.getSellLevels(), count));
            boardSummary.getBuyLevels().computeIfAbsent(symbol, s -> getFirstElements(orderBook.getBuyLevels(), count));
        });

        return boardSummary;
    }

    <T> List<T> getFirstElements(Iterable<T> sourceCollection, int count){

        List<T> firstElements = new ArrayList<>();

        Iterator<T> iterator = sourceCollection.iterator();

        for (int i = 0; i < count && iterator.hasNext(); i++) {
            firstElements.add(iterator.next());
        }
        return firstElements;
    }

}

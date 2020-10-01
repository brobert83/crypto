package io.github.brobert83.crypto;

import io.github.brobert83.crypto.board.CryptoBoard;
import io.github.brobert83.crypto.board.OrderBooksIndex;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrdersIndex;
import io.github.brobert83.crypto.board.orderbook.threadsafe.OrderBookThreadExecutor;
import io.github.brobert83.crypto.board.orderbook.threadsafe.OrderBookThreadSafe;
import io.github.brobert83.crypto.board.orderbook.unsafe.OrderBookSingleThread;

import java.util.concurrent.Executors;

public class CryptoBoardApi {

    private static OrderBookSingleThread newOrderBookSingleThread(Symbol symbol, OrdersIndex ordersIndex) {
        return new OrderBookSingleThread(symbol, ordersIndex);
    }

    private static OrderBookThreadSafe newOrderBookThreadSafe(Symbol symbol, OrderBookSingleThread orderBookSingleThread) {
        return new OrderBookThreadSafe(
                symbol,
                new OrderBookThreadExecutor(Executors.newSingleThreadExecutor()),
                new OrderBookThreadExecutor(Executors.newSingleThreadExecutor()),
                orderBookSingleThread
        );
    }

    /**
     * Creates a instance of CryptoBoard that is not thread-safe
     */
    public static CryptoBoard newCryptoBoard() {

        OrdersIndex ordersIndex = new OrdersIndex();
        OrderBooksIndex orderBooksIndex = new OrderBooksIndex();

        return new CryptoBoard(
                ordersIndex,
                orderBooksIndex,
                symbol -> newOrderBookSingleThread(symbol, ordersIndex)
        );
    }

    /**
     * Creates a instance of CryptoBoard that is thread-safe
     */
    public static CryptoBoard newCryptoBoardThreaded() {

        OrdersIndex ordersIndex = new OrdersIndex();
        OrderBooksIndex orderBooksIndex = new OrderBooksIndex();

        return new CryptoBoard(
                ordersIndex,
                orderBooksIndex,
                symbol -> newOrderBookThreadSafe(
                        symbol,
                        newOrderBookSingleThread(symbol, ordersIndex)
                )
        );
    }

}

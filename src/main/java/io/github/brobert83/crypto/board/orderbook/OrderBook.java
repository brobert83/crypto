package io.github.brobert83.crypto.board.orderbook;

import io.github.brobert83.crypto.board.model.Level;
import io.github.brobert83.crypto.board.model.Order;

import java.util.TreeSet;

public interface OrderBook {

    Order addOrder(Order order);

    void removeOrder(Order order);

    TreeSet<Level> getSellLevels();

    TreeSet<Level> getBuyLevels();

}

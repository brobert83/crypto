package io.github.brobert83.crypto;

import io.github.brobert83.crypto.board.CryptoBoard;
import io.github.brobert83.crypto.board.model.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptoBoardThreadedIntegrationTest {

    CryptoBoard cryptoBoard = CryptoBoardApi.newCryptoBoard();

    /**
     * This test will build a board in single threaded mode than a second board in multi-threaded mode using the same orders and compare the summaries
     */
    @Test
    public void summaryThreadSafe() throws InterruptedException {

        //given
        Symbol ethereum = new Symbol("Ethereum");
        Symbol litecoin = new Symbol("Litecoin");

        Random random = new Random();

        Supplier<Side> randomSide = () -> random.nextInt(2) % 2 == 0 ? Side.SELL : Side.BUY;
        Supplier<Symbol> randomSymbol = () -> random.nextInt(2) % 2 == 0 ? ethereum : litecoin;

        List<BigDecimal> prices = IntStream.rangeClosed(1, 10).mapToObj(i -> BigDecimal.valueOf(Math.random()).add(BigDecimal.ONE)).collect(Collectors.toList());

        Supplier<BigDecimal> randomPrice = () -> prices.get(random.nextInt(10));

        CryptoBoard cryptoBoardThreaded = CryptoBoardApi.newCryptoBoardThreaded();

        Function<Integer, Order> orderBuilder = index -> Order.builder()
                .side(randomSide.get())
                .symbol(randomSymbol.get())
                .quantity(BigDecimal.valueOf(Math.random()).add(BigDecimal.ONE))
                .price(randomPrice.get())
                .build();

        int orderCount = 1000;
        // these are the orders used to build both boards
        List<Order> orders = IntStream.rangeClosed(1, orderCount)
                .mapToObj(orderBuilder::apply)
                .collect(Collectors.toList());

        List<Runnable> commands = orders.stream()
                .peek(order -> cryptoBoard.addOrder(order))
                .map(order -> (Runnable) () -> cryptoBoardThreaded.addOrder(order))
                .collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(15);

        commands.parallelStream().forEach(executorService::submit);
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);

        BoardSummary boardSummary = cryptoBoard.getBoardSummary(orderCount);
        BoardSummary threadedBoard_Summary = cryptoBoardThreaded.getBoardSummary(orderCount);

        threadedBoard_Summary.getBuyLevels()
                .forEach((symbol, levels) ->
                        assertThat(levels)
                                .describedAs("buy orders for " + symbol)
                                .containsExactly(boardSummary.getBuyLevels().get(symbol).toArray(new Level[0])));

        threadedBoard_Summary.getSellLevels()
                .forEach((symbol, levels) ->
                        assertThat(levels)
                                .describedAs("sell orders for " + symbol)
                                .containsExactly(boardSummary.getSellLevels().get(symbol).toArray(new Level[0])));
    }

}

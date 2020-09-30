package io.github.brobert83.crypto.integration;

import io.github.brobert83.crypto.CryptoBoard;
import io.github.brobert83.crypto.OrderBooks;
import io.github.brobert83.crypto.OrdersIndex;
import io.github.brobert83.crypto.model.*;
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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class CryptoBoardIntegrationTest {

    CryptoBoard cryptoBoard = new CryptoBoard(new OrderBooks(), new OrdersIndex());

    @Test
    public void summary() {

        //given
        Symbol ethereum = new Symbol("Ethereum");
        Symbol litecoin = new Symbol("Litecoin");

        asList(
                //sell ethereum
                Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build(),
                Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build(),
                Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),


                //buy ethereum
                Order.builder().side(Side.BUY).symbol(ethereum).quantity(new BigDecimal("10.5")).price(new BigDecimal("14.1")).build(),
                Order.builder().side(Side.BUY).symbol(ethereum).quantity(new BigDecimal("12.5")).price(new BigDecimal("14.1")).build(),
                Order.builder().side(Side.BUY).symbol(ethereum).quantity(new BigDecimal("111.5")).price(new BigDecimal("15.8")).build(),

                //sell litecoin
                Order.builder().side(Side.SELL).symbol(litecoin).quantity(new BigDecimal("45.56245")).price(new BigDecimal("111.6")).build(),
                Order.builder().side(Side.SELL).symbol(litecoin).quantity(new BigDecimal("567.2234")).price(new BigDecimal("123.567")).build(),
                Order.builder().side(Side.SELL).symbol(litecoin).quantity(new BigDecimal("567.1")).price(new BigDecimal("123.567")).build(),

                //buy litecoin
                Order.builder().side(Side.BUY).symbol(litecoin).quantity(new BigDecimal("55.67")).price(new BigDecimal("44.1")).build(),
                Order.builder().side(Side.BUY).symbol(litecoin).quantity(new BigDecimal("10.22")).price(new BigDecimal("44.7")).build()


        ).forEach(cryptoBoard::placeOrder);

        long orderId_toBeRemoved = cryptoBoard.placeOrder(Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("3.5")).price(new BigDecimal("13.6")).build());

        //when
        BoardSummary boardSummary = cryptoBoard.getBoardSummary();

        //then
        assertThat(boardSummary).isNotNull();

        assertThat(boardSummary.getSellOrders()).isNotNull();
        assertThat(boardSummary.getBuyOrders()).isNotNull();

        assertThat(boardSummary.getSellOrders())
                .isNotNull()
                .containsKeys(new Symbol("ethereum"), new Symbol("LITECOIN"));

        assertThat(boardSummary.getSellOrders().get(new Symbol("ETHEREUM")))
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("353.6")).price(new BigDecimal("13.6")).build(),
                        Level.builder().quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                        Level.builder().quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build()
                );

        assertThat(boardSummary.getBuyOrders().get(new Symbol("Ethereum")))
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("111.5")).price(new BigDecimal("15.8")).build(),
                        Level.builder().quantity(new BigDecimal("23.0")).price(new BigDecimal("14.1")).build()
                );

        assertThat(boardSummary.getSellOrders().get(new Symbol("litecoin")))
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("45.56245")).price(new BigDecimal("111.6")).build(),
                        Level.builder().quantity(new BigDecimal("1134.3234")).price(new BigDecimal("123.567")).build()
                );

        assertThat(boardSummary.getBuyOrders().get(new Symbol("Litecoin")))
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("10.22")).price(new BigDecimal("44.7")).build(),
                        Level.builder().quantity(new BigDecimal("55.67")).price(new BigDecimal("44.1")).build()
                );

        { // REMOVE ORDER TEST

            //given
            cryptoBoard.removeOrder(orderId_toBeRemoved);

            //when
            boardSummary = cryptoBoard.getBoardSummary();

            //then
            assertThat(boardSummary.getSellOrders().get(new Symbol("ETHEREUM")))
                    .isNotNull()
                    .containsExactly(
                            Level.builder().quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build(),
                            Level.builder().quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                            Level.builder().quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build()
                    );

        }
    }

    @Test
    public void summaryThreadSafe() throws InterruptedException {

        //given
        Symbol ethereum = new Symbol("Ethereum");
        Symbol litecoin = new Symbol("Litecoin");

        Random random = new Random();

        Supplier<Side> randomSide = () -> random.nextInt(2) % 2 == 0 ? Side.SELL : Side.BUY;
        Supplier<Symbol> randomSymbol = () -> random.nextInt(2) % 2 == 0 ? ethereum : litecoin;

        CryptoBoard threadedCallBoard = new CryptoBoard(new OrderBooks(), new OrdersIndex());

        Function<Integer, Order> orderBuilder = index -> Order.builder()
                .side(randomSide.get())
                .symbol(randomSymbol.get())
                .quantity(BigDecimal.valueOf(Math.random()).add(BigDecimal.ONE))
                .price(BigDecimal.valueOf(Math.random()).add(BigDecimal.ONE))
                .build();

        List<Runnable> commands = IntStream.rangeClosed(1, 1000)
                .peek(index -> cryptoBoard.placeOrder(orderBuilder.apply(index)))
                .mapToObj(index -> (Runnable) () -> threadedCallBoard.placeOrder(orderBuilder.apply(index)))
                .collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(15);

        commands.parallelStream().forEach(executorService::submit);
        executorService.shutdown();
        executorService.awaitTermination(0, TimeUnit.SECONDS);

        BoardSummary boardSummary = cryptoBoard.getBoardSummary();
        BoardSummary threadedBoard_Summary = threadedCallBoard.getBoardSummary();

        assertThat(threadedBoard_Summary).isEqualToComparingFieldByField(boardSummary);
    }

}

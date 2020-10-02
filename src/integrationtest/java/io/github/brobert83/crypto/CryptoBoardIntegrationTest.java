package io.github.brobert83.crypto;

import io.github.brobert83.crypto.board.CryptoBoard;
import io.github.brobert83.crypto.board.model.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class CryptoBoardIntegrationTest {

    CryptoBoard cryptoBoard = CryptoBoardApi.newCryptoBoard();

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
        ).forEach(cryptoBoard::addOrder);

        long orderId_toBeRemoved = cryptoBoard.addOrder(Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("3.5")).price(new BigDecimal("13.6")).build()).getId();

        //when
        BoardSummary boardSummary = cryptoBoard.getBoardSummary(10);

        //then
        assertThat(boardSummary).isNotNull();

        assertThat(boardSummary.getSellLevels()).isNotNull();
        assertThat(boardSummary.getBuyLevels()).isNotNull();

        assertThat(boardSummary.getSellLevels())
                .isNotNull()
                .containsKeys(new Symbol("ethereum"), new Symbol("LITECOIN"));

        assertThat(boardSummary.getSellLevels().get(new Symbol("ETHEREUM")))
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("353.6")).price(new BigDecimal("13.6")).build(),
                        Level.builder().quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                        Level.builder().quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build()
                );

        assertThat(boardSummary.getBuyLevels().get(new Symbol("Ethereum")))
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("111.5")).price(new BigDecimal("15.8")).build(),
                        Level.builder().quantity(new BigDecimal("23.0")).price(new BigDecimal("14.1")).build()
                );

        assertThat(boardSummary.getSellLevels().get(new Symbol("litecoin")))
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("45.56245")).price(new BigDecimal("111.6")).build(),
                        Level.builder().quantity(new BigDecimal("1134.3234")).price(new BigDecimal("123.567")).build()
                );

        assertThat(boardSummary.getBuyLevels().get(new Symbol("Litecoin")))
                .isNotNull()
                .containsExactly(
                        Level.builder().quantity(new BigDecimal("10.22")).price(new BigDecimal("44.7")).build(),
                        Level.builder().quantity(new BigDecimal("55.67")).price(new BigDecimal("44.1")).build()
                );

        { // REMOVE ORDER TEST

            //given
            cryptoBoard.removeOrder(orderId_toBeRemoved);

            //when
            boardSummary = cryptoBoard.getBoardSummary(10);

            //then
            assertThat(boardSummary.getSellLevels().get(new Symbol("ETHEREUM")))
                    .isNotNull()
                    .containsExactly(
                            Level.builder().quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build(),
                            Level.builder().quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                            Level.builder().quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build()
                    );

        }
    }

    interface OrderGenerator {
        Order newOrder(Side side, Integer seed, Symbol symbol);
    }

    OrderGenerator orderGenerator = (side, seed, symbol) -> Order.builder().side(side).symbol(symbol).quantity(new BigDecimal("" + seed)).price(new BigDecimal("" + (seed * 1.12))).build();

    @Test
    public void summary_top10() {

        //given
        Symbol ethereum = new Symbol("Ethereum");
        Symbol litecoin = new Symbol("Litecoin");
        Symbol bitcoin = new Symbol("Bitcoin");

        IntStream.rangeClosed(1, 15).mapToObj(index -> orderGenerator.newOrder(Side.SELL, index, ethereum)).forEach(cryptoBoard::addOrder);
        IntStream.rangeClosed(1, 15).mapToObj(index -> orderGenerator.newOrder(Side.BUY, index, ethereum)).forEach(cryptoBoard::addOrder);

        IntStream.rangeClosed(1, 15).mapToObj(index -> orderGenerator.newOrder(Side.SELL, index, litecoin)).forEach(cryptoBoard::addOrder);
        IntStream.rangeClosed(1, 15).mapToObj(index -> orderGenerator.newOrder(Side.BUY, index, litecoin)).forEach(cryptoBoard::addOrder);

        IntStream.rangeClosed(1, 7).mapToObj(index -> orderGenerator.newOrder(Side.SELL, index, bitcoin)).forEach(cryptoBoard::addOrder);
        IntStream.rangeClosed(1, 10).mapToObj(index -> orderGenerator.newOrder(Side.BUY, index, bitcoin)).forEach(cryptoBoard::addOrder);

        //when
        BoardSummary boardSummary = cryptoBoard.getBoardSummary(10);

        //then
        assertThat(boardSummary.getSellLevels().get(ethereum)).hasSize(10);
        assertThat(boardSummary.getBuyLevels().get(ethereum)).hasSize(10);
        assertThat(boardSummary.getSellLevels().get(litecoin)).hasSize(10);
        assertThat(boardSummary.getBuyLevels().get(litecoin)).hasSize(10);
        assertThat(boardSummary.getSellLevels().get(bitcoin)).hasSize(7);
        assertThat(boardSummary.getBuyLevels().get(bitcoin)).hasSize(10);
    }

}

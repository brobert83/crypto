package io.github.brobert83.crypto.integration;

import io.github.brobert83.crypto.CryptoBoard;
import io.github.brobert83.crypto.model.*;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptoBoardIntegrationTest {

    CryptoBoard cryptoBoard = new CryptoBoard();

    @Test
    public void summary(){

        //given
        Symbol ethereum = new Symbol("Ethereum");

        Order order1 = Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("350.1")).price(new BigDecimal("13.6")).build();
        Order order2 = Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build();
        Order order3 = Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build();
        Order order4 = Order.builder().side(Side.SELL).symbol(ethereum).quantity(new BigDecimal("3.5")).price(new BigDecimal("13.6")).build();

        cryptoBoard.placeOrder(order1);
        cryptoBoard.placeOrder(order2);
        cryptoBoard.placeOrder(order3);
        cryptoBoard.placeOrder(order4);

        //when
        BoardSummary boardSummary = cryptoBoard.getSummary();

        //then
        assertThat(boardSummary).isNotNull();

        assertThat(boardSummary.getSellOrders())
                .isNotNull()
                .containsKeys(new Symbol("ethereum"));

        assertThat(boardSummary.getSellOrders().get(new Symbol("ETHEREUM")))
                .isNotNull()
                .containsExactly(
                        OrderSummary.builder().quantity(new BigDecimal("353.6")).price(new BigDecimal("13.6")).build(),
                        OrderSummary.builder().quantity(new BigDecimal("441.8")).price(new BigDecimal("13.9")).build(),
                        OrderSummary.builder().quantity(new BigDecimal("50.5")).price(new BigDecimal("14")).build()
                );
    }

}

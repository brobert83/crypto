package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.BoardSummary;
import io.github.brobert83.crypto.model.Level;
import io.github.brobert83.crypto.model.Symbol;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderBooksTest {

    OrderBooks orderBooks = new OrderBooks();

    @Test
    public void getOrderBookForSymbol() {

        //given
        Symbol symbol = new Symbol("bitcoin");

        //when
        OrderBook orderBook = orderBooks.getOrderBookForSymbol(symbol);

        //then
        assertThat(orderBook).isNotNull();
        assertThat(orderBooks.getOrderBookForSymbol(symbol)).isEqualTo(orderBook);
        assertThat(orderBook.getBuyExecutor()).isNotNull();
        assertThat(orderBook.getSellExecutor()).isNotNull();
        assertThat(orderBook.getSellExecutor()).isNotSameAs(orderBook.getBuyExecutor());
    }

    @Test
    public void getOrderBookForSymbol_null() {

        assertThatThrownBy(() -> orderBooks.getOrderBookForSymbol(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("symbol is marked non-null but is null");
    }

    @Test
    public void getBoardSummary() {

        //given
        OrderBook orderBook1 = orderBooks.getOrderBookForSymbol(new Symbol("lite"));
        Level lite_sellLevel1 = Level.builder().price(new BigDecimal("123.11")).quantity(new BigDecimal("32.1")).build();
        Level lite_sellLevel2 = Level.builder().price(new BigDecimal("88.11")).quantity(new BigDecimal("32.1231")).build();
        Level lite_buyLevel1 = Level.builder().price(new BigDecimal("66.11")).quantity(new BigDecimal("78.1")).build();
        Level lite_buyLevel2 = Level.builder().price(new BigDecimal("11.11")).quantity(new BigDecimal("87.1231")).build();

        orderBook1.getSellLevels().add(lite_sellLevel1);
        orderBook1.getSellLevels().add(lite_sellLevel2);

        orderBook1.getBuyLevels().add(lite_buyLevel1);
        orderBook1.getBuyLevels().add(lite_buyLevel2);

        OrderBook orderBook2 = orderBooks.getOrderBookForSymbol(new Symbol("heavy"));
        Level heavy_sellLevel1 = Level.builder().price(new BigDecimal("9123.11")).quantity(new BigDecimal("32.19")).build();
        Level heavy_sellLevel2 = Level.builder().price(new BigDecimal("988.11")).quantity(new BigDecimal("32.12319")).build();
        Level heavy_buyLevel1 = Level.builder().price(new BigDecimal("966.11")).quantity(new BigDecimal("78.19")).build();
        Level heavy_buyLevel2 = Level.builder().price(new BigDecimal("911.11")).quantity(new BigDecimal("87.12319")).build();

        orderBook2.getSellLevels().add(heavy_sellLevel1);
        orderBook2.getSellLevels().add(heavy_sellLevel2);

        orderBook2.getBuyLevels().add(heavy_buyLevel1);
        orderBook2.getBuyLevels().add(heavy_buyLevel2);

        //when
        BoardSummary boardSummary = orderBooks.getBoardSummary();

        //then
        assertThat(boardSummary.getSellOrders().get(new Symbol("lite")))
                .containsExactly(lite_sellLevel2, lite_sellLevel1);

        assertThat(boardSummary.getBuyOrders().get(new Symbol("lite")))
                .containsExactly(lite_buyLevel1, lite_buyLevel2);

        assertThat(boardSummary.getSellOrders().get(new Symbol("heavy")))
                .containsExactly(heavy_sellLevel2, heavy_sellLevel1);

        assertThat(boardSummary.getBuyOrders().get(new Symbol("heavy")))
                .containsExactly(heavy_buyLevel1, heavy_buyLevel2);
    }

}
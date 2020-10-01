package io.github.brobert83.crypto.board;

import io.github.brobert83.crypto.board.model.BoardSummary;
import io.github.brobert83.crypto.board.model.Level;
import io.github.brobert83.crypto.board.model.Symbol;
import io.github.brobert83.crypto.board.orderbook.OrderBook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderBooksIndexTest {

    @Mock Function<Symbol, OrderBook> newOrderBookSupplier;

    OrderBooksIndex orderBooksIndex = new OrderBooksIndex();

    @Mock Symbol symbol;
    @Mock OrderBook orderBook;

    @Test
    public void getOrderBookForSymbol_withSupplier() {

        //given
        when(newOrderBookSupplier.apply(symbol)).thenReturn(orderBook);

        //when
        OrderBook actualOrderBook = orderBooksIndex.getOrderBookForSymbol(symbol, newOrderBookSupplier);

        //then
        assertThat(actualOrderBook).isSameAs(orderBook);
        verify(newOrderBookSupplier).apply(symbol);
    }

    @Test
    public void getOrderBookForSymbol() {

        //given
        when(newOrderBookSupplier.apply(symbol)).thenReturn(orderBook);
        orderBooksIndex.getOrderBookForSymbol(symbol, newOrderBookSupplier);

        //when
        OrderBook actualOrderBook = orderBooksIndex.getOrderBookForSymbol(symbol);

        //then
        assertThat(actualOrderBook).isSameAs(orderBook);
    }

    @Test
    public void getBoardSummary() {

        //given
        Symbol symbol1 = new Symbol("lite");

        OrderBook orderBook1 = mock(OrderBook.class);

        orderBooksIndex.getOrderBookForSymbol(symbol1, s -> orderBook1);

        Level lite_sellLevel1 = Level.builder().price(new BigDecimal("123.11")).quantity(new BigDecimal("32.1")).build();
        Level lite_sellLevel2 = Level.builder().price(new BigDecimal("88.11")).quantity(new BigDecimal("32.1231")).build();
        Level lite_buyLevel1 = Level.builder().price(new BigDecimal("66.11")).quantity(new BigDecimal("78.1")).build();
        Level lite_buyLevel2 = Level.builder().price(new BigDecimal("11.11")).quantity(new BigDecimal("87.1231")).build();

        when(orderBook1.getSellLevels())
                .thenReturn(new TreeSet<Level>(Comparator.comparing(Level::getPrice)) {{
                    add(lite_sellLevel1);
                    add(lite_sellLevel2);
                }});

        when(orderBook1.getBuyLevels())
                .thenReturn(new TreeSet<Level>(Comparator.comparing(Level::getPrice).reversed()) {{
                    add(lite_buyLevel1);
                    add(lite_buyLevel2);
                }});

        Symbol symbol2 = new Symbol("heavy");

        OrderBook orderBook2 = mock(OrderBook.class);

        orderBooksIndex.getOrderBookForSymbol(symbol2, s -> orderBook2);

        Level heavy_sellLevel1 = Level.builder().price(new BigDecimal("9123.11")).quantity(new BigDecimal("32.19")).build();
        Level heavy_sellLevel2 = Level.builder().price(new BigDecimal("988.11")).quantity(new BigDecimal("32.12319")).build();
        Level heavy_buyLevel1 = Level.builder().price(new BigDecimal("966.11")).quantity(new BigDecimal("78.19")).build();
        Level heavy_buyLevel2 = Level.builder().price(new BigDecimal("911.11")).quantity(new BigDecimal("87.12319")).build();

        when(orderBook2.getSellLevels())
                .thenReturn(new TreeSet<Level>(Comparator.comparing(Level::getPrice)) {{
                    add(heavy_sellLevel1);
                    add(heavy_sellLevel2);
                }});

        when(orderBook2.getBuyLevels())
                .thenReturn(new TreeSet<Level>(Comparator.comparing(Level::getPrice).reversed()) {{
                    add(heavy_buyLevel1);
                    add(heavy_buyLevel2);
                }});

        //when
        BoardSummary boardSummary = orderBooksIndex.getBoardSummary();

        //then
        assertThat(boardSummary.getSellLevels().get(new Symbol("lite")))
                .containsExactly(lite_sellLevel2, lite_sellLevel1);

        assertThat(boardSummary.getBuyLevels().get(new Symbol("lite")))
                .containsExactly(lite_buyLevel1, lite_buyLevel2);

        assertThat(boardSummary.getSellLevels().get(new Symbol("heavy")))
                .containsExactly(heavy_sellLevel2, heavy_sellLevel1);

        assertThat(boardSummary.getBuyLevels().get(new Symbol("heavy")))
                .containsExactly(heavy_buyLevel1, heavy_buyLevel2);
    }

}
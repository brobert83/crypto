package io.github.brobert83.crypto;

import io.github.brobert83.crypto.model.Symbol;
import org.junit.Test;

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
    }

    @Test
    public void getOrderBookForSymbol_null(){

        assertThatThrownBy(() ->  orderBooks.getOrderBookForSymbol(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("symbol is marked non-null but is null");
    }

}
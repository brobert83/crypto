package io.github.brobert83.crypto.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LevelTest {

    Level level = Level.builder().quantity(new BigDecimal("12.34")).price(BigDecimal.ONE).build();

    @Test
    public void incrementQuantity() {

        //when
        level.incrementOrderQuantity(new BigDecimal("34.22"));

        //then
        assertThat(level.getQuantity()).isEqualTo(new BigDecimal("46.56"));
    }

    @Test
    public void nulls() {

        assertThatThrownBy(() ->  Level.builder().price(BigDecimal.ZERO).build())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("quantity is marked non-null but is null");

        assertThatThrownBy(() ->  Level.builder().quantity(BigDecimal.ZERO).build())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("price is marked non-null but is null");
    }

}
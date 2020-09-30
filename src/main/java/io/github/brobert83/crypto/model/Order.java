package io.github.brobert83.crypto.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class Order {

    private final long id = UUID.randomUUID().getMostSignificantBits();

    private long userId;
    private Side side;
    private BigDecimal quantity;
    private Symbol symbol;
    private BigDecimal price;

}

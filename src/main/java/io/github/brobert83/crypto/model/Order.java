package io.github.brobert83.crypto.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Order {

    private long id;
    private long userId;
    private Side side;
    private BigDecimal quantity;
    private Symbol symbol;
    private BigDecimal price;

}

package io.github.brobert83.crypto.board.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class Order {

    private long id;
    @NonNull private long userId;
    @NonNull private Side side;
    @NonNull private BigDecimal quantity;
    @NonNull private Symbol symbol;
    @NonNull private BigDecimal price;

}

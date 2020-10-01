package io.github.brobert83.crypto.board.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
@Data
public class Level {
    @NonNull private BigDecimal quantity;
    @NonNull private BigDecimal price;

    public void incrementOrderQuantity(BigDecimal quantity) {
        this.quantity = this.quantity.add(quantity);
    }

}

package io.github.brobert83.crypto.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderSummary {
    private BigDecimal quantity;
    private BigDecimal price;
}

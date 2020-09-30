package io.github.brobert83.crypto.model;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public class BoardSummary {

    @Getter private final Map<Symbol, List<Level>> sellOrders = new HashMap<>();
    @Getter private final Map<Symbol, List<Level>> buyOrders = new HashMap<>();

}

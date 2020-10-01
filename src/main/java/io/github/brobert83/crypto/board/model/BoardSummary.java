package io.github.brobert83.crypto.board.model;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public class BoardSummary {

    @Getter private final Map<Symbol, List<Level>> sellLevels = new HashMap<>();
    @Getter private final Map<Symbol, List<Level>> buyLevels = new HashMap<>();

}

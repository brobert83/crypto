package io.github.brobert83.crypto.board.model;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Symbol {

    @NonNull private String name;

    public Symbol(String name) {
        this.name = name.toLowerCase();
    }
}

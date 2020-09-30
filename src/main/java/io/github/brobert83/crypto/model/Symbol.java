package io.github.brobert83.crypto.model;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class Symbol {

    @NonNull private String name;

    public Symbol(String name) {
        this.name = name.toLowerCase();
    }
}

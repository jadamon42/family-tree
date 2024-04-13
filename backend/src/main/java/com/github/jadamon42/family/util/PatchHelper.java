package com.github.jadamon42.family.util;

import java.util.Optional;
import java.util.function.BiConsumer;

public class PatchHelper {
    public static <B, T> void patch(B builder, BiConsumer<B, T> setter, Optional<T> newValue, T existingValue) {
        if (newValue != null) {
            setter.accept(builder, newValue.orElse(null));
        } else {
            setter.accept(builder, existingValue);
        }
    }

    public static <B, T> void set(B builder, BiConsumer<B, T> setter, Optional<T> newValue) {
        if (newValue != null) {
            setter.accept(builder, newValue.orElse(null));
        }
    }
}

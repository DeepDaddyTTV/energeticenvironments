package com.deepdaddyttv.energeticenvironments.common.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import net.minecraft.util.StringRepresentable;

public final class StringRepresentableCodec {
    private StringRepresentableCodec() {}

    public static <T extends Enum<T> & StringRepresentable> Codec<T> of(final T[] values) {
        return Codec.STRING.comapFlatMap(
                key -> Arrays.stream(values)
                        .filter(value -> value.getSerializedName().equals(key))
                        .findFirst()
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown value: " + key)),
                StringRepresentable::getSerializedName
        );
    }
}

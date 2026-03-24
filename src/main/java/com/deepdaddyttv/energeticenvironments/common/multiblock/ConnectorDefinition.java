package com.deepdaddyttv.energeticenvironments.common.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ConnectorDefinition(char symbol) {
    private static final Codec<Character> CHARACTER_CODEC = Codec.STRING.comapFlatMap(
            value -> value.length() == 1 ? DataResult.success(value.charAt(0)) : DataResult.error(() -> "Connector symbol must be one character"),
            String::valueOf
    );

    public static final Codec<ConnectorDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CHARACTER_CODEC.fieldOf("symbol").forGetter(ConnectorDefinition::symbol)
    ).apply(instance, ConnectorDefinition::new));
}

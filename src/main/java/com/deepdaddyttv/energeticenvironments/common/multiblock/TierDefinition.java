package com.deepdaddyttv.energeticenvironments.common.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TierDefinition(
        int tier,
        String displayName,
        int energyCapacity,
        int baseGeneration,
        int maxOutput
) {
    public static final Codec<TierDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, 99).fieldOf("tier").forGetter(TierDefinition::tier),
            Codec.STRING.fieldOf("display_name").forGetter(TierDefinition::displayName),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("energy_capacity").forGetter(TierDefinition::energyCapacity),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("base_generation").forGetter(TierDefinition::baseGeneration),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("max_output").forGetter(TierDefinition::maxOutput)
    ).apply(instance, TierDefinition::new));
}

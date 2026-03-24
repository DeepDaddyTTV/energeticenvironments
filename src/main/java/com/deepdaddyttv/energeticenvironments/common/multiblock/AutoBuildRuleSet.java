package com.deepdaddyttv.energeticenvironments.common.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record AutoBuildRuleSet(boolean allowReplaceableBlocks, int blocksPerOperation) {
    public static final Codec<AutoBuildRuleSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("allow_replaceable_blocks", true).forGetter(AutoBuildRuleSet::allowReplaceableBlocks),
            Codec.intRange(1, 512).optionalFieldOf("blocks_per_operation", 256).forGetter(AutoBuildRuleSet::blocksPerOperation)
    ).apply(instance, AutoBuildRuleSet::new));
}

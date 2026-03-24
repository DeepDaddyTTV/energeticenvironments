package com.deepdaddyttv.energeticenvironments.common.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public record MultiblockDefinition(
        ResourceLocation family,
        TierDefinition tier,
        ResourceLocation controllerBlock,
        List<ConnectorDefinition> connectors,
        AutoBuildRuleSet autoBuild,
        List<EnvironmentModifierDefinition> environment,
        Map<Character, BlockRequirement> palette,
        List<List<String>> layers
) {
    private static final Codec<Character> PALETTE_KEY_CODEC = Codec.STRING.comapFlatMap(
            value -> value.length() == 1 ? DataResult.success(value.charAt(0)) : DataResult.error(() -> "Palette keys must be one character"),
            String::valueOf
    );

    public static final Codec<MultiblockDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("family").forGetter(MultiblockDefinition::family),
            TierDefinition.CODEC.fieldOf("tier").forGetter(MultiblockDefinition::tier),
            ResourceLocation.CODEC.fieldOf("controller_block").forGetter(MultiblockDefinition::controllerBlock),
            Codec.list(ConnectorDefinition.CODEC).optionalFieldOf("connectors", List.of()).forGetter(MultiblockDefinition::connectors),
            AutoBuildRuleSet.CODEC.optionalFieldOf("auto_build", new AutoBuildRuleSet(true, 256)).forGetter(MultiblockDefinition::autoBuild),
            Codec.list(EnvironmentModifierDefinition.CODEC).optionalFieldOf("environment", List.of()).forGetter(MultiblockDefinition::environment),
            Codec.unboundedMap(PALETTE_KEY_CODEC, BlockRequirement.CODEC).fieldOf("palette").forGetter(MultiblockDefinition::palette),
            Codec.list(Codec.list(Codec.STRING)).fieldOf("layers").forGetter(MultiblockDefinition::layers)
    ).apply(instance, MultiblockDefinition::new));
}

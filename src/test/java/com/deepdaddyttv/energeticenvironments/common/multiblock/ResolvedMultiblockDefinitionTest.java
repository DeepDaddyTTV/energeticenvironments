package com.deepdaddyttv.energeticenvironments.common.multiblock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

final class ResolvedMultiblockDefinitionTest {
    @Test
    void resolvesCountsAndConnectorPositions() {
        final MultiblockDefinition definition = new MultiblockDefinition(
                ResourceLocation.parse("energeticenvironments:wind_generator"),
                new TierDefinition(1, "Tier 1 Wind Generator", 20_000, 24, 64),
                ResourceLocation.parse("energeticenvironments:wind_generator_controller"),
                List.of(new ConnectorDefinition('O')),
                new AutoBuildRuleSet(true, 32),
                List.of(),
                Map.of(
                        'C', new BlockRequirement(MaterialGroup.CONTROLLER, Optional.of(ResourceLocation.parse("energeticenvironments:wind_generator_controller")), Optional.empty()),
                        'O', new BlockRequirement(MaterialGroup.CONNECTOR, Optional.of(ResourceLocation.parse("energeticenvironments:wind_generator_connector")), Optional.empty()),
                        'T', new BlockRequirement(MaterialGroup.TOWER, Optional.of(ResourceLocation.parse("minecraft:stone")), Optional.empty()),
                        'B', new BlockRequirement(MaterialGroup.BLADE, Optional.of(ResourceLocation.parse("minecraft:quartz_block")), Optional.empty()),
                        'H', new BlockRequirement(MaterialGroup.HUB, Optional.of(ResourceLocation.parse("minecraft:gold_block")), Optional.empty())
                ),
                List.of(
                        List.of("...", ".C.", ".O."),
                        List.of("...", ".T.", "..."),
                        List.of(".B.", "BHB", ".B.")
                )
        );

        final ResolvedMultiblockDefinition resolved = ResolvedMultiblockDefinition.resolve(ResourceLocation.parse("energeticenvironments:test"), definition);
        assertEquals(7, resolved.totalPlacements());
        assertEquals(1, resolved.requiredByGroup().get(MaterialGroup.TOWER));
        assertEquals(4, resolved.requiredByGroup().get(MaterialGroup.BLADE));
        assertEquals(1, resolved.requiredByGroup().get(MaterialGroup.HUB));
        assertEquals(1, resolved.requiredByGroup().get(MaterialGroup.CONNECTOR));
        assertTrue(resolved.connectorPositions().contains(new BlockPos(0, 0, 1)));
        assertEquals(new BlockPos(0, 3, 0), resolved.probePosition());
    }

    @Test
    void rejectsDefinitionsWithoutSingleController() {
        final MultiblockDefinition definition = new MultiblockDefinition(
                ResourceLocation.parse("energeticenvironments:wind_generator"),
                new TierDefinition(1, "Broken", 20_000, 24, 64),
                ResourceLocation.parse("energeticenvironments:wind_generator_controller"),
                List.of(),
                new AutoBuildRuleSet(true, 32),
                List.of(),
                Map.of(
                        'T', new BlockRequirement(MaterialGroup.TOWER, Optional.of(ResourceLocation.parse("minecraft:stone")), Optional.empty())
                ),
                List.of(List.of("T"))
        );

        assertThrows(IllegalArgumentException.class, () -> ResolvedMultiblockDefinition.resolve(ResourceLocation.parse("energeticenvironments:broken"), definition));
    }
}

package com.deepdaddyttv.energeticenvironments.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public final class WindGenerationCalculator {
    private WindGenerationCalculator() {}

    public static int calculateGeneration(final Level level, final BlockPos controllerPos, final Direction facing, final ResolvedMultiblockDefinition definition) {
        final BlockPos probe = StructureTransform.toWorld(controllerPos, definition.probePosition(), facing);
        double multiplier = 1.0D;
        for (final EnvironmentModifierDefinition modifier : definition.definition().environment()) {
            multiplier *= modifier.evaluate(level, probe);
        }

        return Math.max(0, (int) Math.round(definition.definition().tier().baseGeneration() * multiplier));
    }
}

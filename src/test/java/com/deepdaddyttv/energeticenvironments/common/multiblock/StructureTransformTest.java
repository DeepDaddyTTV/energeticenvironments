package com.deepdaddyttv.energeticenvironments.common.multiblock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

final class StructureTransformTest {
    @Test
    void rotatesRelativePositionsAroundController() {
        final BlockPos origin = new BlockPos(1, 2, -3);
        assertEquals(new BlockPos(1, 2, -3), StructureTransform.rotate(origin, Direction.NORTH));
        assertEquals(new BlockPos(3, 2, 1), StructureTransform.rotate(origin, Direction.EAST));
        assertEquals(new BlockPos(-1, 2, 3), StructureTransform.rotate(origin, Direction.SOUTH));
        assertEquals(new BlockPos(-3, 2, -1), StructureTransform.rotate(origin, Direction.WEST));
    }
}

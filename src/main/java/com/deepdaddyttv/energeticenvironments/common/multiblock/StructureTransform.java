package com.deepdaddyttv.energeticenvironments.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class StructureTransform {
    private StructureTransform() {}

    public static BlockPos rotate(final BlockPos relativePos, final Direction facing) {
        return switch (facing) {
            case SOUTH -> new BlockPos(-relativePos.getX(), relativePos.getY(), -relativePos.getZ());
            case EAST -> new BlockPos(-relativePos.getZ(), relativePos.getY(), relativePos.getX());
            case WEST -> new BlockPos(relativePos.getZ(), relativePos.getY(), -relativePos.getX());
            default -> relativePos;
        };
    }

    public static BlockPos toWorld(final BlockPos origin, final BlockPos relativePos, final Direction facing) {
        return origin.offset(rotate(relativePos, facing));
    }
}

package com.deepdaddyttv.energeticenvironments.common.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class EnergyTransferHelper {
    private EnergyTransferHelper() {}

    public static int pushEnergyToNeighbors(final Level level, final BlockPos sourcePos, final IEnergyStorage source, final int maxTransfer) {
        int moved = 0;
        int remaining = maxTransfer;

        for (final Direction direction : Direction.values()) {
            if (remaining <= 0) {
                break;
            }

            final BlockPos targetPos = sourcePos.relative(direction);
            final IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, direction.getOpposite());
            if (target == null || !target.canReceive()) {
                continue;
            }

            final int accepted = target.receiveEnergy(remaining, true);
            if (accepted <= 0) {
                continue;
            }

            final int extracted = source.extractEnergy(accepted, false);
            if (extracted <= 0) {
                continue;
            }

            final int inserted = target.receiveEnergy(extracted, false);
            if (inserted <= 0) {
                source.receiveEnergy(extracted, false);
                continue;
            }

            if (inserted < extracted) {
                source.receiveEnergy(extracted - inserted, false);
            }

            remaining -= inserted;
            moved += inserted;
        }

        return moved;
    }
}

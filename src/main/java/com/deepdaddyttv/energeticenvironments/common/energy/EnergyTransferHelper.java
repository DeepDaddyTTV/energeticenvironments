package com.deepdaddyttv.energeticenvironments.common.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class EnergyTransferHelper {
    private EnergyTransferHelper() {}

    public static int pushEnergyToNeighbors(final Level level, final BlockPos sourcePos, final EnergyHandler source, final int maxTransfer) {
        int moved = 0;
        int remaining = maxTransfer;

        for (final Direction direction : Direction.values()) {
            if (remaining <= 0) {
                break;
            }

            final BlockPos targetPos = sourcePos.relative(direction);
            final EnergyHandler target = level.getCapability(Capabilities.Energy.BLOCK, targetPos, direction.getOpposite());
            if (target == null) {
                continue;
            }

            final int accepted = simulateInsert(target, remaining);
            if (accepted <= 0) {
                continue;
            }

            try (Transaction transaction = Transaction.openRoot()) {
                final int extracted = source.extract(accepted, transaction);
                final int inserted = target.insert(extracted, transaction);
                if (extracted > 0 && extracted == inserted) {
                    transaction.commit();
                    remaining -= inserted;
                    moved += inserted;
                }
            }
        }

        return moved;
    }

    private static int simulateInsert(final EnergyHandler target, final int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            return target.insert(amount, transaction);
        }
    }
}

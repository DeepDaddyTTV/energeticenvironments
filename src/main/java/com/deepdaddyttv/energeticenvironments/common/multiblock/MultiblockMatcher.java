package com.deepdaddyttv.energeticenvironments.common.multiblock;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class MultiblockMatcher {
    private MultiblockMatcher() {}

    public static ScanResult scan(final Level level, final BlockPos controllerPos, final Direction facing, final ResolvedMultiblockDefinition definition, final Container inventory) {
        final Map<MaterialGroup, Integer> placedByGroup = new EnumMap<>(MaterialGroup.class);
        final Map<MaterialGroup, Integer> availableByGroup = new EnumMap<>(MaterialGroup.class);
        final List<ResolvedMultiblockDefinition.ResolvedStructureCell> missing = new ArrayList<>();
        int obstructionCount = 0;

        for (final MaterialGroup group : MaterialGroup.values()) {
            placedByGroup.put(group, 0);
            availableByGroup.put(group, 0);
        }

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            final ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            for (final ResolvedMultiblockDefinition.ResolvedStructureCell cell : definition.cells()) {
                if (cell.isController()) {
                    continue;
                }
                if (cell.requirement().matches(stack)) {
                    availableByGroup.merge(cell.requirement().group(), stack.getCount(), Integer::sum);
                    break;
                }
            }
        }

        int placedCount = 0;
        for (final ResolvedMultiblockDefinition.ResolvedStructureCell cell : definition.cells()) {
            final BlockPos worldPos = StructureTransform.toWorld(controllerPos, cell.relativePos(), facing);
            final BlockState state = level.getBlockState(worldPos);

            if (cell.requirement().matches(state)) {
                placedByGroup.merge(cell.requirement().group(), 1, Integer::sum);
                if (!cell.isController()) {
                    placedCount++;
                }
                continue;
            }

            if (cell.isController()) {
                obstructionCount++;
                continue;
            }

            if (state.canBeReplaced()) {
                missing.add(cell);
            } else {
                obstructionCount++;
            }
        }

        final int completionPercent = definition.totalPlacements() == 0 ? 100 : (placedCount * 100) / definition.totalPlacements();
        boolean buildReady = obstructionCount == 0;
        for (final Map.Entry<MaterialGroup, Integer> entry : definition.requiredByGroup().entrySet()) {
            final int placed = placedByGroup.getOrDefault(entry.getKey(), 0);
            final int available = availableByGroup.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue() - placed) {
                buildReady = false;
                break;
            }
        }

        return new ScanResult(placedByGroup, availableByGroup, missing, obstructionCount, completionPercent, buildReady);
    }

    public static boolean matches(final Level level, final BlockPos controllerPos, final Direction facing, final ResolvedMultiblockDefinition definition) {
        for (final ResolvedMultiblockDefinition.ResolvedStructureCell cell : definition.cells()) {
            final BlockPos worldPos = StructureTransform.toWorld(controllerPos, cell.relativePos(), facing);
            if (!cell.requirement().matches(level.getBlockState(worldPos))) {
                return false;
            }
        }
        return true;
    }

    public record ScanResult(
            Map<MaterialGroup, Integer> placedByGroup,
            Map<MaterialGroup, Integer> availableByGroup,
            List<ResolvedMultiblockDefinition.ResolvedStructureCell> missingCells,
            int obstructionCount,
            int completionPercent,
            boolean buildReady
    ) {}
}

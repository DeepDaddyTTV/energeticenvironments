package com.deepdaddyttv.energeticenvironments.common.multiblock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public record ResolvedMultiblockDefinition(
        ResourceLocation id,
        MultiblockDefinition definition,
        List<ResolvedStructureCell> cells,
        List<BlockPos> connectorPositions,
        Map<MaterialGroup, Integer> requiredByGroup,
        int totalPlacements,
        BlockPos probePosition
) {
    public static ResolvedMultiblockDefinition resolve(final ResourceLocation id, final MultiblockDefinition definition) {
        if (definition.layers().isEmpty()) {
            throw new IllegalArgumentException("Multiblock " + id + " has no layers");
        }

        final int depth = definition.layers().getFirst().size();
        if (depth == 0) {
            throw new IllegalArgumentException("Multiblock " + id + " has empty layers");
        }

        final int width = definition.layers().getFirst().getFirst().length();
        if (width == 0) {
            throw new IllegalArgumentException("Multiblock " + id + " has zero-width rows");
        }

        final List<ResolvedStructureCell> cells = new ArrayList<>();
        final List<BlockPos> connectors = new ArrayList<>();
        final Map<MaterialGroup, Integer> requiredByGroup = new EnumMap<>(MaterialGroup.class);

        BlockPos controllerOrigin = null;
        int highestY = 0;

        for (int y = 0; y < definition.layers().size(); y++) {
            final List<String> layer = definition.layers().get(y);
            if (layer.size() != depth) {
                throw new IllegalArgumentException("Multiblock " + id + " has inconsistent layer depth");
            }
            for (int z = 0; z < layer.size(); z++) {
                final String row = layer.get(z);
                if (row.length() != width) {
                    throw new IllegalArgumentException("Multiblock " + id + " has inconsistent row width");
                }
                for (int x = 0; x < row.length(); x++) {
                    final char symbol = row.charAt(x);
                    if (symbol == '.' || symbol == ' ') {
                        continue;
                    }
                    final BlockRequirement requirement = definition.palette().get(symbol);
                    if (requirement == null) {
                        throw new IllegalArgumentException("Multiblock " + id + " references missing palette symbol '" + symbol + "'");
                    }
                    if (!requirement.isValidDefinition()) {
                        throw new IllegalArgumentException("Multiblock " + id + " has invalid palette entry for '" + symbol + "'");
                    }
                    final BlockPos relative = new BlockPos(x, y, z);
                    cells.add(new ResolvedStructureCell(relative, symbol, requirement));
                    requiredByGroup.merge(requirement.group(), 1, Integer::sum);
                    highestY = Math.max(highestY, y);
                    if (requirement.group() == MaterialGroup.CONTROLLER) {
                        if (controllerOrigin != null) {
                            throw new IllegalArgumentException("Multiblock " + id + " defines multiple controller positions");
                        }
                        controllerOrigin = relative;
                    }
                }
            }
        }

        if (controllerOrigin == null) {
            throw new IllegalArgumentException("Multiblock " + id + " does not define a controller");
        }

        final List<ResolvedStructureCell> normalized = new ArrayList<>(cells.size());
        for (final ResolvedStructureCell cell : cells) {
            final BlockPos translated = cell.relativePos().subtract(controllerOrigin);
            final ResolvedStructureCell normalizedCell = new ResolvedStructureCell(translated, cell.symbol(), cell.requirement());
            normalized.add(normalizedCell);
            if (cell.requirement().group() == MaterialGroup.CONNECTOR) {
                connectors.add(translated);
            }
        }
        normalized.sort(Comparator.comparingInt((ResolvedStructureCell cell) -> cell.relativePos().getY())
                .thenComparingInt(cell -> cell.relativePos().getZ())
                .thenComparingInt(cell -> cell.relativePos().getX()));

        requiredByGroup.remove(MaterialGroup.CONTROLLER);
        final int totalPlacements = normalized.size() - 1;
        final BlockPos probePosition = new BlockPos(0, highestY - controllerOrigin.getY() + 1, 0);
        return new ResolvedMultiblockDefinition(id, definition, List.copyOf(normalized), List.copyOf(connectors), Map.copyOf(requiredByGroup), totalPlacements, probePosition);
    }

    public int tierNumber() {
        return definition.tier().tier();
    }

    public String displayName() {
        return definition.tier().displayName();
    }

    public Block controllerBlock() {
        return BuiltInRegistries.BLOCK.getValue(definition.controllerBlock());
    }

    public Map<MaterialGroup, Integer> createZeroedGroupMap() {
        final Map<MaterialGroup, Integer> result = new EnumMap<>(MaterialGroup.class);
        for (final MaterialGroup group : MaterialGroup.values()) {
            result.put(group, 0);
        }
        return result;
    }

    public record ResolvedStructureCell(BlockPos relativePos, char symbol, BlockRequirement requirement) {
        public boolean isController() {
            return requirement.group() == MaterialGroup.CONTROLLER;
        }
    }
}

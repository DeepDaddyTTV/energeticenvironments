package com.deepdaddyttv.energeticenvironments.gametest;

import com.deepdaddyttv.energeticenvironments.common.block.WindGeneratorControllerBlock;
import com.deepdaddyttv.energeticenvironments.common.blockentity.EnergyCellBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorConnectorBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorControllerBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.multiblock.MultiblockDefinitionManager;
import com.deepdaddyttv.energeticenvironments.common.multiblock.MultiblockMatcher;
import com.deepdaddyttv.energeticenvironments.common.multiblock.ResolvedMultiblockDefinition;
import com.deepdaddyttv.energeticenvironments.common.multiblock.StructureTransform;
import com.deepdaddyttv.energeticenvironments.registry.EEBlocks;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

final class EEGameTestSupport {
    static final BlockPos CONTROLLER_POS = new BlockPos(4, 2, 4);
    static final BlockPos CONNECTOR_POS = new BlockPos(4, 2, 5);
    static final BlockPos ENERGY_CELL_POS = new BlockPos(4, 2, 6);
    static final BlockPos TOWER_POS = new BlockPos(4, 3, 4);
    static final BlockPos HUB_POS = new BlockPos(4, 4, 4);
    static final BlockPos BLADE_NORTH_POS = new BlockPos(4, 4, 3);
    static final BlockPos BLADE_SOUTH_POS = new BlockPos(4, 4, 5);
    static final BlockPos BLADE_WEST_POS = new BlockPos(3, 4, 4);
    static final BlockPos BLADE_EAST_POS = new BlockPos(5, 4, 4);

    private EEGameTestSupport() {}

    static BlockState controllerState(final GameTestHelper helper) {
        return EEBlocks.WIND_GENERATOR_CONTROLLER.get().defaultBlockState().setValue(WindGeneratorControllerBlock.FACING, facingForRotation(helper.getTestRotation()));
    }

    static WindGeneratorControllerBlockEntity placeController(final GameTestHelper helper) {
        helper.setBlock(CONTROLLER_POS, controllerState(helper));
        return getWindController(helper);
    }

    static void placeCompletedTierOneStructure(final GameTestHelper helper) {
        helper.setBlock(CONTROLLER_POS, controllerState(helper));
        helper.setBlock(CONNECTOR_POS, EEBlocks.WIND_GENERATOR_CONNECTOR.get());
        helper.setBlock(TOWER_POS, Blocks.SMOOTH_STONE);
        helper.setBlock(HUB_POS, Blocks.GOLD_BLOCK);
        helper.setBlock(BLADE_NORTH_POS, Blocks.WHITE_CONCRETE);
        helper.setBlock(BLADE_SOUTH_POS, Blocks.WHITE_CONCRETE);
        helper.setBlock(BLADE_WEST_POS, Blocks.WHITE_CONCRETE);
        helper.setBlock(BLADE_EAST_POS, Blocks.WHITE_CONCRETE);
    }

    static void placeEnergyCell(final GameTestHelper helper) {
        helper.setBlock(ENERGY_CELL_POS, EEBlocks.ENERGY_CELL.get());
    }

    static void fillTierOneAutoBuildInventory(final WindGeneratorControllerBlockEntity controller) {
        controller.setItem(0, new ItemStack(EEBlocks.WIND_GENERATOR_CONNECTOR.get()));
        controller.setItem(1, new ItemStack(Blocks.SMOOTH_STONE));
        controller.setItem(2, new ItemStack(Blocks.GOLD_BLOCK));
        controller.setItem(3, new ItemStack(Blocks.WHITE_CONCRETE, 4));
    }

    static int countItems(final Container container) {
        int total = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            total += container.getItem(slot).getCount();
        }
        return total;
    }

    static IEnergyStorage getBlockEnergyStorage(final GameTestHelper helper, final BlockPos relativePos, final Direction side) {
        return helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, helper.absolutePos(relativePos), side);
    }

    static String describeTierOneScan(final GameTestHelper helper, final WindGeneratorControllerBlockEntity controller) {
        final ResolvedMultiblockDefinition definition = MultiblockDefinitionManager.definitionsForController(EEBlocks.WIND_GENERATOR_CONTROLLER.get()).stream()
                .filter(candidate -> candidate.tierNumber() == 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tier 1 wind generator definition is missing."));

        final MultiblockMatcher.ScanResult scan = MultiblockMatcher.scan(
                helper.getLevel(),
                controller.getBlockPos(),
                controller.getBlockState().getValue(WindGeneratorControllerBlock.FACING),
                definition,
                controller
        );

        final String missingCells = scan.missingCells().stream()
                .map(cell -> cell.relativePos().toShortString())
                .collect(Collectors.joining(", "));
        final String actualCells = definition.cells().stream()
                .filter(cell -> !cell.isController())
                .map(cell -> {
                    final BlockPos worldPos = StructureTransform.toWorld(
                            controller.getBlockPos(),
                            cell.relativePos(),
                            controller.getBlockState().getValue(WindGeneratorControllerBlock.FACING)
                    );
                    final String blockName = BuiltInRegistries.BLOCK.getKey(helper.getLevel().getBlockState(worldPos).getBlock()).toString();
                    return cell.relativePos().toShortString() + "=" + blockName;
                })
                .collect(Collectors.joining(", "));

        return "completion=" + scan.completionPercent()
                + ", obstructionCount=" + scan.obstructionCount()
                + ", buildReady=" + scan.buildReady()
                + ", missingCells=[" + missingCells + "]"
                + ", actualCells=[" + actualCells + "]";
    }

    static WindGeneratorControllerBlockEntity getWindController(final GameTestHelper helper) {
        final WindGeneratorControllerBlockEntity controller = helper.getBlockEntity(CONTROLLER_POS);
        if (controller == null) {
            throw new IllegalStateException("Missing wind generator controller block entity.");
        }
        return controller;
    }

    static WindGeneratorConnectorBlockEntity getConnector(final GameTestHelper helper) {
        final WindGeneratorConnectorBlockEntity connector = helper.getBlockEntity(CONNECTOR_POS);
        if (connector == null) {
            throw new IllegalStateException("Missing wind generator connector block entity.");
        }
        return connector;
    }

    static EnergyCellBlockEntity getEnergyCell(final GameTestHelper helper) {
        final EnergyCellBlockEntity energyCell = helper.getBlockEntity(ENERGY_CELL_POS);
        if (energyCell == null) {
            throw new IllegalStateException("Missing energy cell block entity.");
        }
        return energyCell;
    }

    private static Direction facingForRotation(final Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90 -> Direction.EAST;
            case CLOCKWISE_180 -> Direction.SOUTH;
            case COUNTERCLOCKWISE_90 -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }
}

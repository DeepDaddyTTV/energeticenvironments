package com.deepdaddyttv.energeticenvironments.registry;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.common.block.EnergyCellBlock;
import com.deepdaddyttv.energeticenvironments.common.block.WindGeneratorConnectorBlock;
import com.deepdaddyttv.energeticenvironments.common.block.WindGeneratorControllerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EEBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnergeticEnvironments.MOD_ID);

    public static final DeferredBlock<WindGeneratorControllerBlock> WIND_GENERATOR_CONTROLLER = BLOCKS.registerBlock("wind_generator_controller",
            WindGeneratorControllerBlock::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<WindGeneratorConnectorBlock> WIND_GENERATOR_CONNECTOR = BLOCKS.registerBlock("wind_generator_connector",
            WindGeneratorConnectorBlock::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(4.0F, 6.0F).sound(SoundType.COPPER).requiresCorrectToolForDrops());
    public static final DeferredBlock<EnergyCellBlock> ENERGY_CELL = BLOCKS.registerBlock("energy_cell",
            EnergyCellBlock::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());

    private EEBlocks() {}

    public static void register(final IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

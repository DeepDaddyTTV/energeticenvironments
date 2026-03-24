package com.deepdaddyttv.energeticenvironments.registry;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.common.block.EnergyCellBlock;
import com.deepdaddyttv.energeticenvironments.common.block.WindGeneratorConnectorBlock;
import com.deepdaddyttv.energeticenvironments.common.block.WindGeneratorControllerBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EEBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnergeticEnvironments.MOD_ID);

    public static final DeferredBlock<WindGeneratorControllerBlock> WIND_GENERATOR_CONTROLLER = BLOCKS.register("wind_generator_controller",
            id -> new WindGeneratorControllerBlock(withBlockId(id, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops())));
    public static final DeferredBlock<WindGeneratorConnectorBlock> WIND_GENERATOR_CONNECTOR = BLOCKS.register("wind_generator_connector",
            id -> new WindGeneratorConnectorBlock(withBlockId(id, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(4.0F, 6.0F).sound(SoundType.COPPER).requiresCorrectToolForDrops())));
    public static final DeferredBlock<EnergyCellBlock> ENERGY_CELL = BLOCKS.register("energy_cell",
            id -> new EnergyCellBlock(withBlockId(id, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops())));

    private EEBlocks() {}

    private static BlockBehaviour.Properties withBlockId(final ResourceLocation id, final BlockBehaviour.Properties properties) {
        return properties.setId(ResourceKey.create(Registries.BLOCK, id));
    }

    public static void register(final IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

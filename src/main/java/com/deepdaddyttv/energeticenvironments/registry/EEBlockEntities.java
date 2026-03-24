package com.deepdaddyttv.energeticenvironments.registry;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.common.blockentity.EnergyCellBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorConnectorBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorControllerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EEBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, EnergeticEnvironments.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindGeneratorControllerBlockEntity>> WIND_GENERATOR_CONTROLLER =
            BLOCK_ENTITIES.register("wind_generator_controller", () -> BlockEntityType.Builder.of(WindGeneratorControllerBlockEntity::new, EEBlocks.WIND_GENERATOR_CONTROLLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindGeneratorConnectorBlockEntity>> WIND_GENERATOR_CONNECTOR =
            BLOCK_ENTITIES.register("wind_generator_connector", () -> BlockEntityType.Builder.of(WindGeneratorConnectorBlockEntity::new, EEBlocks.WIND_GENERATOR_CONNECTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyCellBlockEntity>> ENERGY_CELL =
            BLOCK_ENTITIES.register("energy_cell", () -> BlockEntityType.Builder.of(EnergyCellBlockEntity::new, EEBlocks.ENERGY_CELL.get()).build(null));

    private EEBlockEntities() {}

    public static void register(final net.neoforged.bus.api.IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, WIND_GENERATOR_CONNECTOR.get(), (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ENERGY_CELL.get(), (blockEntity, side) -> blockEntity.getEnergyHandler());
    }
}

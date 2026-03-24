package com.deepdaddyttv.energeticenvironments.registry;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.common.blockentity.EnergyCellBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorConnectorBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorControllerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.Set;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EEBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, EnergeticEnvironments.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindGeneratorControllerBlockEntity>> WIND_GENERATOR_CONTROLLER =
            BLOCK_ENTITIES.register("wind_generator_controller", () -> new BlockEntityType<WindGeneratorControllerBlockEntity>(WindGeneratorControllerBlockEntity::new, Set.of(EEBlocks.WIND_GENERATOR_CONTROLLER.get()), false));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindGeneratorConnectorBlockEntity>> WIND_GENERATOR_CONNECTOR =
            BLOCK_ENTITIES.register("wind_generator_connector", () -> new BlockEntityType<WindGeneratorConnectorBlockEntity>(WindGeneratorConnectorBlockEntity::new, Set.of(EEBlocks.WIND_GENERATOR_CONNECTOR.get()), false));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyCellBlockEntity>> ENERGY_CELL =
            BLOCK_ENTITIES.register("energy_cell", () -> new BlockEntityType<EnergyCellBlockEntity>(EnergyCellBlockEntity::new, Set.of(EEBlocks.ENERGY_CELL.get()), false));

    private EEBlockEntities() {}

    public static void register(final net.neoforged.bus.api.IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Energy.BLOCK, WIND_GENERATOR_CONNECTOR.get(), (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ENERGY_CELL.get(), (blockEntity, side) -> blockEntity.getEnergyHandler());
    }
}

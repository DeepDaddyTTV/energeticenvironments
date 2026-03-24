package com.deepdaddyttv.energeticenvironments.registry;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.common.item.WindSurveyToolItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EEItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnergeticEnvironments.MOD_ID);

    public static final DeferredItem<BlockItem> WIND_GENERATOR_CONTROLLER = ITEMS.registerSimpleBlockItem(EEBlocks.WIND_GENERATOR_CONTROLLER);
    public static final DeferredItem<BlockItem> WIND_GENERATOR_CONNECTOR = ITEMS.registerSimpleBlockItem(EEBlocks.WIND_GENERATOR_CONNECTOR);
    public static final DeferredItem<BlockItem> ENERGY_CELL = ITEMS.registerSimpleBlockItem(EEBlocks.ENERGY_CELL);
    public static final DeferredItem<WindSurveyToolItem> WIND_SURVEY_TOOL = ITEMS.register("wind_survey_tool",
            id -> new WindSurveyToolItem(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, id))));

    private EEItems() {}

    public static void register(final IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

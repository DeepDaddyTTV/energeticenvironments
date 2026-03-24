package com.deepdaddyttv.energeticenvironments.registry;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.common.menu.EnergyCellMenu;
import com.deepdaddyttv.energeticenvironments.common.menu.WindGeneratorControllerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EEMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, EnergeticEnvironments.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<WindGeneratorControllerMenu>> WIND_GENERATOR_CONTROLLER =
            MENUS.register("wind_generator_controller", () -> IMenuTypeExtension.create((windowId, playerInventory, data) ->
                    new WindGeneratorControllerMenu(windowId, playerInventory, data.readBlockPos())));
    public static final DeferredHolder<MenuType<?>, MenuType<EnergyCellMenu>> ENERGY_CELL =
            MENUS.register("energy_cell", () -> IMenuTypeExtension.create((windowId, playerInventory, data) ->
                    new EnergyCellMenu(windowId, playerInventory, data.readBlockPos())));

    private EEMenus() {}

    public static void register(final IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

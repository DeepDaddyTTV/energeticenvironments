package com.deepdaddyttv.energeticenvironments.client;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.client.screen.EnergyCellScreen;
import com.deepdaddyttv.energeticenvironments.client.screen.WindGeneratorControllerScreen;
import com.deepdaddyttv.energeticenvironments.registry.EEMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = EnergeticEnvironments.MOD_ID, value = Dist.CLIENT)
public final class EnergeticEnvironmentsClient {
    private EnergeticEnvironmentsClient() {}

    @SubscribeEvent
    public static void registerScreens(final RegisterMenuScreensEvent event) {
        event.register(EEMenus.WIND_GENERATOR_CONTROLLER.get(), WindGeneratorControllerScreen::new);
        event.register(EEMenus.ENERGY_CELL.get(), EnergyCellScreen::new);
    }
}

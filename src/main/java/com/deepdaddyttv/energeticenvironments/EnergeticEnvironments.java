package com.deepdaddyttv.energeticenvironments;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(EnergeticEnvironments.MOD_ID)
public final class EnergeticEnvironments {
    public static final String MOD_ID = "energeticenvironments";
    public static final String MOD_NAME = "Energetic Environments";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EnergeticEnvironments(final IEventBus modEventBus) {
        modEventBus.addListener(this::onCommonSetup);
        LOGGER.info("{} bootstrap initialized.", MOD_NAME);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("{} common setup complete.", MOD_NAME);
    }
}

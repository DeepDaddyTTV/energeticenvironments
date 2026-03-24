package com.deepdaddyttv.energeticenvironments;

import com.deepdaddyttv.energeticenvironments.common.multiblock.MultiblockDefinitionManager;
import com.deepdaddyttv.energeticenvironments.registry.EEBlockEntities;
import com.deepdaddyttv.energeticenvironments.registry.EEBlocks;
import com.deepdaddyttv.energeticenvironments.registry.EEItems;
import com.deepdaddyttv.energeticenvironments.registry.EEMenus;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(EnergeticEnvironments.MOD_ID)
public final class EnergeticEnvironments {
    public static final String MOD_ID = "energeticenvironments";
    public static final String MOD_NAME = "Energetic Environments";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EnergeticEnvironments(final IEventBus modEventBus) {
        EEBlocks.register(modEventBus);
        EEItems.register(modEventBus);
        EEBlockEntities.register(modEventBus);
        EEMenus.register(modEventBus);
        modEventBus.addListener(EEBlockEntities::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(MultiblockDefinitionManager::registerReloadListener);
        LOGGER.info("{} bootstrap initialized.", MOD_NAME);
    }
}

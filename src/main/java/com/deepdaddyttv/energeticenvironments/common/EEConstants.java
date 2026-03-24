package com.deepdaddyttv.energeticenvironments.common;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import net.minecraft.resources.ResourceLocation;

public final class EEConstants {
    public static final String DATA_MULTIBLOCK_DIRECTORY = "multiblocks";
    public static final ResourceLocation VANILLA_CHEST_GUI = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    private EEConstants() {}

    public static ResourceLocation id(final String path) {
        return ResourceLocation.fromNamespaceAndPath(EnergeticEnvironments.MOD_ID, path);
    }
}

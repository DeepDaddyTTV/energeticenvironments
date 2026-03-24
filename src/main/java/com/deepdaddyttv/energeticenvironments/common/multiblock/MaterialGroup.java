package com.deepdaddyttv.energeticenvironments.common.multiblock;

import net.minecraft.util.StringRepresentable;

public enum MaterialGroup implements StringRepresentable {
    CONTROLLER("controller"),
    CONNECTOR("connector"),
    TOWER("tower"),
    BLADE("blade"),
    HUB("hub");

    private final String serializedName;

    MaterialGroup(final String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    public String translationKey() {
        return "ui.energeticenvironments.material_group." + serializedName;
    }
}

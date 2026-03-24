package com.deepdaddyttv.energeticenvironments.client.screen;

import com.deepdaddyttv.energeticenvironments.common.EEConstants;
import com.deepdaddyttv.energeticenvironments.common.menu.WindGeneratorControllerMenu;
import com.deepdaddyttv.energeticenvironments.common.multiblock.MaterialGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public final class WindGeneratorControllerScreen extends AbstractContainerScreen<WindGeneratorControllerMenu> {
    private static final int SIDE_PANEL_X = 180;
    private static final int SIDE_PANEL_WIDTH = 64;

    public WindGeneratorControllerScreen(final WindGeneratorControllerMenu menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 248;
        this.imageHeight = 114 + WindGeneratorControllerMenu.CONTAINER_ROWS * 18;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("<"), button ->
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, WindGeneratorControllerMenu.BUTTON_PREVIOUS_TIER))
                .bounds(leftPos + SIDE_PANEL_X, topPos + 6, 20, 20)
                .build());
        addRenderableWidget(Button.builder(Component.literal(">"), button ->
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, WindGeneratorControllerMenu.BUTTON_NEXT_TIER))
                .bounds(leftPos + SIDE_PANEL_X + 22, topPos + 6, 20, 20)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("ui.energeticenvironments.validate"), button ->
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, WindGeneratorControllerMenu.BUTTON_VALIDATE))
                .bounds(leftPos + SIDE_PANEL_X, topPos + 30, SIDE_PANEL_WIDTH, 18)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("ui.energeticenvironments.autobuild"), button ->
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, WindGeneratorControllerMenu.BUTTON_AUTOBUILD))
                .bounds(leftPos + SIDE_PANEL_X, topPos + 50, SIDE_PANEL_WIDTH, 18)
                .build());
    }

    @Override
    protected void renderBg(final GuiGraphics guiGraphics, final float partialTick, final int mouseX, final int mouseY) {
        guiGraphics.fill(leftPos, topPos, leftPos + 176, topPos + imageHeight, 0xFF2B2A2E);
        guiGraphics.fill(leftPos + 1, topPos + 1, leftPos + 175, topPos + imageHeight - 1, 0xFF3A3940);
        for (int row = 0; row < WindGeneratorControllerMenu.CONTAINER_ROWS; row++) {
            for (int column = 0; column < 9; column++) {
                final int slotX = leftPos + 7 + column * 18;
                final int slotY = topPos + 17 + row * 18;
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF1A1B20);
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                final int slotX = leftPos + 7 + column * 18;
                final int slotY = topPos + 102 + row * 18;
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF1A1B20);
            }
        }
        for (int column = 0; column < 9; column++) {
            final int slotX = leftPos + 7 + column * 18;
            final int slotY = topPos + 160;
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF1A1B20);
        }
        guiGraphics.fill(leftPos + 176, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF1B1D24);
        guiGraphics.fill(leftPos + 177, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF2A2E39);
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);

        final int textX = SIDE_PANEL_X + 2;
        int y = 74;
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.selected_tier", menu.getSelectedTier()), textX, y, 0xFFFFFF, false);
        y += 10;
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.active_tier", menu.getActiveTier()), textX, y, 0xFFFFFF, false);
        y += 10;
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.formed", yesNo(menu.isFormed())), textX, y, 0xFFFFFF, false);
        y += 10;
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.build_ready", yesNo(menu.isBuildReady())), textX, y, 0xFFFFFF, false);
        y += 10;
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.integrity", menu.getCompletionPercent()), textX, y, 0xFFFFFF, false);
        y += 10;
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.obstructions", menu.getObstructionCount()), textX, y, 0xFFFFFF, false);
        y += 10;
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.generation", menu.getGeneration()), textX, y, 0xB6FFB3, false);
        y += 10;
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.energy", menu.getEnergyStored(), menu.getEnergyCapacity()), textX, y, 0x7FD4FF, false);
        y += 14;
        drawMaterialLine(guiGraphics, MaterialGroup.TOWER, textX, y);
        y += 10;
        drawMaterialLine(guiGraphics, MaterialGroup.BLADE, textX, y);
        y += 10;
        drawMaterialLine(guiGraphics, MaterialGroup.HUB, textX, y);
        y += 10;
        drawMaterialLine(guiGraphics, MaterialGroup.CONNECTOR, textX, y);
    }

    private void drawMaterialLine(final GuiGraphics guiGraphics, final MaterialGroup group, final int x, final int y) {
        guiGraphics.drawString(font,
                Component.translatable(group.translationKey()).append(": " + menu.getAvailable(group) + "/" + menu.getRequired(group)),
                x,
                y,
                menu.getAvailable(group) >= menu.getRequired(group) ? 0xA7F3A1 : 0xFFB36B,
                false);
    }

    private Component yesNo(final boolean value) {
        return Component.translatable(value ? "ui.energeticenvironments.yes" : "ui.energeticenvironments.no");
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}

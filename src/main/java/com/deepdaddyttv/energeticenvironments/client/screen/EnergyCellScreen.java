package com.deepdaddyttv.energeticenvironments.client.screen;

import com.deepdaddyttv.energeticenvironments.common.EEConstants;
import com.deepdaddyttv.energeticenvironments.common.menu.EnergyCellMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class EnergyCellScreen extends AbstractContainerScreen<EnergyCellMenu> {
    public EnergyCellScreen(final EnergyCellMenu menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 248;
        this.imageHeight = 114;
        this.inventoryLabelY = 20;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(final GuiGraphics guiGraphics, final float partialTick, final int mouseX, final int mouseY) {
        guiGraphics.fill(leftPos, topPos, leftPos + 176, topPos + imageHeight, 0xFF2B2A2E);
        guiGraphics.fill(leftPos + 1, topPos + 1, leftPos + 175, topPos + imageHeight - 1, 0xFF3A3940);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                final int slotX = leftPos + 7 + column * 18;
                final int slotY = topPos + 29 + row * 18;
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF1A1B20);
            }
        }
        for (int column = 0; column < 9; column++) {
            final int slotX = leftPos + 7 + column * 18;
            final int slotY = topPos + 87;
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF1A1B20);
        }
        guiGraphics.fill(leftPos + 176, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF1B1D24);
        guiGraphics.fill(leftPos + 177, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF2A2E39);
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.energy", menu.getEnergyStored(), menu.getEnergyCapacity()), 180, 20, 0x7FD4FF, false);
        guiGraphics.drawString(font, Component.translatable("ui.energeticenvironments.energy_fill", getFillPercent()), 180, 34, 0xFFFFFF, false);
        guiGraphics.fill(180, 50, 238, 60, 0xFF151820);
        final int barWidth = (int) Math.round(58.0D * (double) menu.getEnergyStored() / Math.max(1, menu.getEnergyCapacity()));
        guiGraphics.fill(180, 50, 180 + barWidth, 60, 0xFF55C1FF);
    }

    private int getFillPercent() {
        return menu.getEnergyCapacity() <= 0 ? 0 : (menu.getEnergyStored() * 100) / menu.getEnergyCapacity();
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}

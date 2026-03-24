package com.deepdaddyttv.energeticenvironments.common.menu;

import com.deepdaddyttv.energeticenvironments.common.blockentity.EnergyCellBlockEntity;
import com.deepdaddyttv.energeticenvironments.registry.EEBlocks;
import com.deepdaddyttv.energeticenvironments.registry.EEMenus;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class EnergyCellMenu extends AbstractContainerMenu {
    @Nullable
    private final EnergyCellBlockEntity blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public EnergyCellMenu(final int containerId, final Inventory playerInventory, final BlockPos pos) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, pos), pos);
    }

    public EnergyCellMenu(final int containerId, final Inventory playerInventory, final EnergyCellBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.getBlockPos());
    }

    private EnergyCellMenu(final int containerId, final Inventory playerInventory, @Nullable final EnergyCellBlockEntity blockEntity, final BlockPos pos) {
        super(EEMenus.ENERGY_CELL.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = blockEntity != null ? blockEntity.getMenuData() : new SimpleContainerData(EnergyCellBlockEntity.DATA_COUNT);
        this.access = blockEntity != null && blockEntity.getLevel() != null
                ? ContainerLevelAccess.create(blockEntity.getLevel(), pos)
                : ContainerLevelAccess.NULL;
        addDataSlots(data);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 30 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 88));
        }
    }

    @Nullable
    private static EnergyCellBlockEntity getBlockEntity(final Inventory playerInventory, final BlockPos pos) {
        if (playerInventory.player.level().getBlockEntity(pos) instanceof EnergyCellBlockEntity blockEntity) {
            return blockEntity;
        }
        return null;
    }

    @Override
    public boolean stillValid(final Player player) {
        return stillValid(access, player, EEBlocks.ENERGY_CELL.get());
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        return ItemStack.EMPTY;
    }

    public int getEnergyStored() {
        return data.get(0);
    }

    public int getEnergyCapacity() {
        return data.get(1);
    }
}

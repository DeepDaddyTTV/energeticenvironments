package com.deepdaddyttv.energeticenvironments.common.menu;

import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorControllerBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.multiblock.MaterialGroup;
import com.deepdaddyttv.energeticenvironments.registry.EEBlocks;
import com.deepdaddyttv.energeticenvironments.registry.EEMenus;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class WindGeneratorControllerMenu extends AbstractContainerMenu {
    public static final int CONTAINER_ROWS = 4;
    public static final int CONTAINER_SLOTS = WindGeneratorControllerBlockEntity.CONTAINER_SIZE;
    public static final int BUTTON_PREVIOUS_TIER = 0;
    public static final int BUTTON_NEXT_TIER = 1;
    public static final int BUTTON_VALIDATE = 2;
    public static final int BUTTON_AUTOBUILD = 3;

    @Nullable
    private final WindGeneratorControllerBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public WindGeneratorControllerMenu(final int containerId, final Inventory playerInventory, final BlockPos pos) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, pos), pos);
    }

    public WindGeneratorControllerMenu(final int containerId, final Inventory playerInventory, final WindGeneratorControllerBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.getBlockPos());
    }

    private WindGeneratorControllerMenu(final int containerId, final Inventory playerInventory, @Nullable final WindGeneratorControllerBlockEntity blockEntity, final BlockPos pos) {
        super(EEMenus.WIND_GENERATOR_CONTROLLER.get(), containerId);
        this.blockEntity = blockEntity;
        this.container = blockEntity != null ? blockEntity : new SimpleContainer(CONTAINER_SLOTS);
        this.data = blockEntity != null ? blockEntity.getMenuData() : new SimpleContainerData(WindGeneratorControllerBlockEntity.DATA_COUNT);
        this.access = blockEntity != null && blockEntity.getLevel() != null
                ? ContainerLevelAccess.create(blockEntity.getLevel(), pos)
                : ContainerLevelAccess.NULL;

        checkContainerSize(container, CONTAINER_SLOTS);
        checkContainerDataCount(data, WindGeneratorControllerBlockEntity.DATA_COUNT);
        container.startOpen(playerInventory.player);
        addDataSlots(data);

        for (int row = 0; row < CONTAINER_ROWS; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(container, column + row * 9, 8 + column * 18, 18 + row * 18));
            }
        }

        final int playerInventoryY = 103;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, playerInventoryY + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, playerInventoryY + 58));
        }
    }

    @Nullable
    private static WindGeneratorControllerBlockEntity getBlockEntity(final Inventory playerInventory, final BlockPos pos) {
        if (playerInventory.player.level().getBlockEntity(pos) instanceof WindGeneratorControllerBlockEntity blockEntity) {
            return blockEntity;
        }
        return null;
    }

    @Override
    public boolean stillValid(final Player player) {
        return stillValid(access, player, EEBlocks.WIND_GENERATOR_CONTROLLER.get());
    }

    @Override
    public boolean clickMenuButton(final Player player, final int id) {
        return blockEntity != null && blockEntity.handleMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack result = ItemStack.EMPTY;
        final Slot slot = slots.get(index);
        if (slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < CONTAINER_SLOTS) {
                if (!moveItemStackTo(slotStack, CONTAINER_SLOTS, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, CONTAINER_SLOTS, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public void removed(final Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public int getSelectedTier() {
        return data.get(1);
    }

    public int getActiveTier() {
        return data.get(2);
    }

    public boolean isFormed() {
        return data.get(0) == 1;
    }

    public int getEnergyStored() {
        return data.get(3);
    }

    public int getEnergyCapacity() {
        return data.get(4);
    }

    public int getGeneration() {
        return data.get(5);
    }

    public int getCompletionPercent() {
        return data.get(6);
    }

    public boolean isBuildReady() {
        return data.get(7) == 1;
    }

    public int getRequired(final MaterialGroup group) {
        return switch (group) {
            case TOWER -> data.get(8);
            case BLADE -> data.get(10);
            case HUB -> data.get(12);
            case CONNECTOR -> data.get(14);
            default -> 0;
        };
    }

    public int getAvailable(final MaterialGroup group) {
        return switch (group) {
            case TOWER -> data.get(9);
            case BLADE -> data.get(11);
            case HUB -> data.get(13);
            case CONNECTOR -> data.get(15);
            default -> 0;
        };
    }

    public int getObstructionCount() {
        return data.get(16);
    }
}

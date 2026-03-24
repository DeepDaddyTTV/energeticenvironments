package com.deepdaddyttv.energeticenvironments.common.blockentity;

import com.deepdaddyttv.energeticenvironments.common.energy.NotifyingEnergyHandler;
import com.deepdaddyttv.energeticenvironments.common.menu.EnergyCellMenu;
import com.deepdaddyttv.energeticenvironments.registry.EEBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public final class EnergyCellBlockEntity extends BlockEntity implements MenuProvider {
    public static final int DATA_COUNT = 2;
    private static final int CAPACITY = 1_000_000;
    private static final int TRANSFER_RATE = 16_384;

    private final NotifyingEnergyHandler energyStorage = new NotifyingEnergyHandler(CAPACITY, TRANSFER_RATE, TRANSFER_RATE, this::onEnergyChanged);
    private final ContainerData menuData = new ContainerData() {
        @Override
        public int get(final int index) {
            return switch (index) {
                case 0 -> energyStorage.getStoredEnergy();
                case 1 -> energyStorage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(final int index, final int value) {}

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public EnergyCellBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(EEBlockEntities.ENERGY_CELL.get(), pos, blockState);
    }

    public EnergyHandler getEnergyHandler() {
        return energyStorage;
    }

    public ContainerData getMenuData() {
        return menuData;
    }

    public int getStoredEnergy() {
        return energyStorage.getStoredEnergy();
    }

    public int getEnergyCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.energeticenvironments.energy_cell");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
        return new EnergyCellMenu(containerId, inventory, this);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", energyStorage.getStoredEnergy());
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        energyStorage.loadStoredEnergy(input.getIntOr("energy", 0));
    }

    @Override
    public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void onEnergyChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}

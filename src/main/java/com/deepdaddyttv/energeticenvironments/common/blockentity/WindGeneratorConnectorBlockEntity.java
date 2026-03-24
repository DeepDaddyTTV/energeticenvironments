package com.deepdaddyttv.energeticenvironments.common.blockentity;

import com.deepdaddyttv.energeticenvironments.registry.EEBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class WindGeneratorConnectorBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos controllerPos;
    private final IEnergyStorage energyProxy = new EnergyProxy();

    public WindGeneratorConnectorBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(EEBlockEntities.WIND_GENERATOR_CONNECTOR.get(), pos, blockState);
    }

    public void setControllerReference(@Nullable final BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        setChanged();
        if (level != null) {
            level.invalidateCapabilities(worldPosition);
            if (!level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    public void clearControllerReference() {
        setControllerReference(null);
    }

    @Nullable
    public IEnergyStorage getEnergyHandler() {
        return getController() == null ? null : energyProxy;
    }

    @Nullable
    public WindGeneratorControllerBlockEntity getController() {
        if (level == null || controllerPos == null) {
            return null;
        }
        if (!(level.getBlockEntity(controllerPos) instanceof WindGeneratorControllerBlockEntity controller)) {
            return null;
        }
        return controller.isFormed() ? controller : null;
    }

    @Override
    protected void saveAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (controllerPos != null) {
            tag.putLong("controller_pos", controllerPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        controllerPos = tag.contains("controller_pos") ? BlockPos.of(tag.getLong("controller_pos")) : null;
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private final class EnergyProxy implements IEnergyStorage {
        @Override
        public int getEnergyStored() {
            final WindGeneratorControllerBlockEntity controller = getController();
            return controller == null ? 0 : controller.getStoredEnergy();
        }

        @Override
        public int getMaxEnergyStored() {
            final WindGeneratorControllerBlockEntity controller = getController();
            return controller == null ? 0 : controller.getEnergyCapacity();
        }

        @Override
        public int receiveEnergy(final int amount, final boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(final int amount, final boolean simulate) {
            final WindGeneratorControllerBlockEntity controller = getController();
            return controller == null ? 0 : controller.extractThroughConnector(amount, simulate);
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
}

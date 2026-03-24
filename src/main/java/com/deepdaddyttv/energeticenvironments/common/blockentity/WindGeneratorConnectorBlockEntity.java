package com.deepdaddyttv.energeticenvironments.common.blockentity;

import com.deepdaddyttv.energeticenvironments.registry.EEBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class WindGeneratorConnectorBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos controllerPos;
    private final EnergyHandler energyProxy = new EnergyProxy();

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
    public EnergyHandler getEnergyHandler() {
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
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        if (controllerPos != null) {
            output.putLong("controller_pos", controllerPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        final long value = input.getLongOr("controller_pos", Long.MIN_VALUE);
        controllerPos = value == Long.MIN_VALUE ? null : BlockPos.of(value);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private final class EnergyProxy implements EnergyHandler {
        @Override
        public long getAmountAsLong() {
            final WindGeneratorControllerBlockEntity controller = getController();
            return controller == null ? 0L : controller.getStoredEnergy();
        }

        @Override
        public long getCapacityAsLong() {
            final WindGeneratorControllerBlockEntity controller = getController();
            return controller == null ? 0L : controller.getEnergyCapacity();
        }

        @Override
        public int insert(final int amount, final TransactionContext transaction) {
            return 0;
        }

        @Override
        public int extract(final int amount, final TransactionContext transaction) {
            final WindGeneratorControllerBlockEntity controller = getController();
            return controller == null ? 0 : controller.extractThroughConnector(amount, transaction);
        }
    }
}

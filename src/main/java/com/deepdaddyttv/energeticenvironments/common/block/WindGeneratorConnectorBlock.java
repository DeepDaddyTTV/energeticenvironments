package com.deepdaddyttv.energeticenvironments.common.block;

import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorConnectorBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class WindGeneratorConnectorBlock extends Block implements EntityBlock {
    public WindGeneratorConnectorBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected void onRemove(final BlockState state, final Level level, final BlockPos pos, final BlockState newState, final boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof WindGeneratorConnectorBlockEntity connector) {
            connector.clearControllerReference();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new WindGeneratorConnectorBlockEntity(pos, state);
    }
}

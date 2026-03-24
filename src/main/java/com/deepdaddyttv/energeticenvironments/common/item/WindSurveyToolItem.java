package com.deepdaddyttv.energeticenvironments.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public final class WindSurveyToolItem extends Item {
    public WindSurveyToolItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Level level = context.getLevel();
        if (!level.isClientSide() && context.getPlayer() != null) {
            final BlockPos probe = context.getClickedPos().above();
            final boolean skyVisible = level.canSeeSky(probe);
            final boolean raining = level.isRainingAt(probe);
            final boolean thundering = level.isThundering() && raining;
            context.getPlayer().displayClientMessage(Component.translatable(
                    "item.energeticenvironments.wind_survey_tool.readout",
                    probe.getY(),
                    skyVisible ? Component.translatable("ui.energeticenvironments.yes") : Component.translatable("ui.energeticenvironments.no"),
                    raining ? Component.translatable("ui.energeticenvironments.yes") : Component.translatable("ui.energeticenvironments.no"),
                    thundering ? Component.translatable("ui.energeticenvironments.yes") : Component.translatable("ui.energeticenvironments.no")
            ), true);
        }
        return InteractionResult.SUCCESS;
    }
}

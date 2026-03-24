package com.deepdaddyttv.energeticenvironments.common.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record BlockRequirement(MaterialGroup group, Optional<ResourceLocation> block, Optional<ResourceLocation> tag) {
    public static final Codec<BlockRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentableCodec.of(MaterialGroup.values()).fieldOf("group").forGetter(BlockRequirement::group),
            ResourceLocation.CODEC.optionalFieldOf("block").forGetter(BlockRequirement::block),
            ResourceLocation.CODEC.optionalFieldOf("tag").forGetter(BlockRequirement::tag)
    ).apply(instance, BlockRequirement::new));

    public boolean isValidDefinition() {
        return block.isPresent() ^ tag.isPresent();
    }

    public boolean matches(final BlockState state) {
        if (block.isPresent()) {
            final Block expected = BuiltInRegistries.BLOCK.getValue(block.get());
            return expected != null && state.is(expected);
        }
        if (tag.isPresent()) {
            return state.is(TagKey.create(Registries.BLOCK, tag.get()));
        }
        return false;
    }

    public boolean matches(final ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }
        return matches(blockItem.getBlock().defaultBlockState());
    }
}

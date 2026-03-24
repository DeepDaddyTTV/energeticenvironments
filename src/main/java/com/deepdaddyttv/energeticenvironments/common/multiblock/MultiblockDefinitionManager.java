package com.deepdaddyttv.energeticenvironments.common.multiblock;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public final class MultiblockDefinitionManager {
    private static final MultiblockDefinitionManager INSTANCE = new MultiblockDefinitionManager();

    private volatile Map<ResourceLocation, ResolvedMultiblockDefinition> byId = Map.of();
    private volatile List<ResolvedMultiblockDefinition> ordered = List.of();

    private MultiblockDefinitionManager() {}

    public static void registerReloadListener(final AddReloadListenerEvent event) {
        event.addListener(new MultiblockDefinitionReloadListener(INSTANCE));
    }

    void replaceDefinitions(final Map<ResourceLocation, ResolvedMultiblockDefinition> byId, final List<ResolvedMultiblockDefinition> ordered) {
        this.byId = Map.copyOf(byId);
        this.ordered = List.copyOf(ordered);
    }

    public static Optional<ResolvedMultiblockDefinition> get(final ResourceLocation id) {
        return Optional.ofNullable(INSTANCE.byId.get(id));
    }

    public static List<ResolvedMultiblockDefinition> definitionsForController(final Block controllerBlock) {
        final List<ResolvedMultiblockDefinition> matches = INSTANCE.ordered.stream()
                .filter(definition -> definition.controllerBlock() == controllerBlock)
                .collect(Collectors.toList());
        return Collections.unmodifiableList(matches);
    }
}

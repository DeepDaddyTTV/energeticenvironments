package com.deepdaddyttv.energeticenvironments.common.multiblock;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.common.EEConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class MultiblockDefinitionReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final MultiblockDefinitionManager manager;

    public MultiblockDefinitionReloadListener(final MultiblockDefinitionManager manager) {
        this.manager = manager;
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(final ResourceManager resourceManager, final ProfilerFiller profiler) {
        final Map<ResourceLocation, JsonElement> json = new HashMap<>();
        final FileToIdConverter converter = FileToIdConverter.json(EEConstants.DATA_MULTIBLOCK_DIRECTORY);
        converter.listMatchingResources(resourceManager).forEach((file, resource) -> {
            try (var reader = resource.openAsReader()) {
                json.put(converter.fileToId(file), JsonParser.parseReader(reader));
            } catch (final IOException exception) {
                EnergeticEnvironments.LOGGER.error("Failed to read multiblock definition {}", file, exception);
            }
        });
        return json;
    }

    @Override
    protected void apply(final Map<ResourceLocation, JsonElement> objects, final ResourceManager resourceManager, final ProfilerFiller profiler) {
        final Map<ResourceLocation, ResolvedMultiblockDefinition> loaded = new HashMap<>();
        for (final Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            final ResourceLocation id = entry.getKey();
            try {
                final MultiblockDefinition definition = MultiblockDefinition.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                loaded.put(id, ResolvedMultiblockDefinition.resolve(id, definition));
            } catch (final Exception exception) {
                EnergeticEnvironments.LOGGER.error("Failed to load multiblock definition {}", id, exception);
            }
        }

        final List<ResolvedMultiblockDefinition> ordered = new ArrayList<>(loaded.values());
        ordered.sort(Comparator.comparingInt(ResolvedMultiblockDefinition::tierNumber));
        manager.replaceDefinitions(loaded, ordered);
        EnergeticEnvironments.LOGGER.info("Loaded {} multiblock definitions.", ordered.size());
    }
}

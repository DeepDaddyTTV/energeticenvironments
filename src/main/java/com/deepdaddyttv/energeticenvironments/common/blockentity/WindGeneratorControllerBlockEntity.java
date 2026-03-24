package com.deepdaddyttv.energeticenvironments.common.blockentity;

import com.deepdaddyttv.energeticenvironments.common.energy.EnergyTransferHelper;
import com.deepdaddyttv.energeticenvironments.common.energy.NotifyingEnergyHandler;
import com.deepdaddyttv.energeticenvironments.common.multiblock.MaterialGroup;
import com.deepdaddyttv.energeticenvironments.common.multiblock.MultiblockDefinitionManager;
import com.deepdaddyttv.energeticenvironments.common.multiblock.MultiblockMatcher;
import com.deepdaddyttv.energeticenvironments.common.multiblock.ResolvedMultiblockDefinition;
import com.deepdaddyttv.energeticenvironments.common.multiblock.StructureTransform;
import com.deepdaddyttv.energeticenvironments.common.multiblock.WindGenerationCalculator;
import com.deepdaddyttv.energeticenvironments.common.menu.WindGeneratorControllerMenu;
import com.deepdaddyttv.energeticenvironments.registry.EEBlockEntities;
import com.deepdaddyttv.energeticenvironments.registry.EEBlocks;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class WindGeneratorControllerBlockEntity extends BlockEntity implements MenuProvider, Container {
    public static final int CONTAINER_SIZE = 36;
    public static final int DATA_COUNT = 17;
    private static final int REFRESH_INTERVAL = 20;

    private final SimpleContainer inventory = new SimpleContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            super.setChanged();
            WindGeneratorControllerBlockEntity.this.onInventoryChanged();
        }

        @Override
        public boolean stillValid(final Player player) {
            return WindGeneratorControllerBlockEntity.this.stillValid(player);
        }
    };

    private final NotifyingEnergyHandler energyStorage = new NotifyingEnergyHandler(20_000, Integer.MAX_VALUE, 64, this::onEnergyChanged);
    private final int[] requiredCounts = new int[MaterialGroup.values().length];
    private final int[] availableCounts = new int[MaterialGroup.values().length];
    private final ContainerData menuData = new ContainerData() {
        @Override
        public int get(final int index) {
            return switch (index) {
                case 0 -> formed ? 1 : 0;
                case 1 -> getSelectedTierNumber();
                case 2 -> getActiveTierNumber();
                case 3 -> energyStorage.getStoredEnergy();
                case 4 -> energyStorage.getCapacity();
                case 5 -> lastGeneration;
                case 6 -> completionPercent;
                case 7 -> buildReady ? 1 : 0;
                case 8 -> getRequiredCount(MaterialGroup.TOWER);
                case 9 -> getAvailableCount(MaterialGroup.TOWER);
                case 10 -> getRequiredCount(MaterialGroup.BLADE);
                case 11 -> getAvailableCount(MaterialGroup.BLADE);
                case 12 -> getRequiredCount(MaterialGroup.HUB);
                case 13 -> getAvailableCount(MaterialGroup.HUB);
                case 14 -> getRequiredCount(MaterialGroup.CONNECTOR);
                case 15 -> getAvailableCount(MaterialGroup.CONNECTOR);
                case 16 -> obstructionCount;
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

    private int selectedTierIndex;
    private boolean formed;
    @Nullable
    private ResourceLocation activeDefinitionId;
    private int tickCounter;
    private int lastGeneration;
    private int completionPercent;
    private int obstructionCount;
    private boolean buildReady;

    public WindGeneratorControllerBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(EEBlockEntities.WIND_GENERATOR_CONTROLLER.get(), pos, blockState);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final WindGeneratorControllerBlockEntity blockEntity) {
        blockEntity.serverTick();
    }

    public boolean isFormed() {
        return formed;
    }

    public int getStoredEnergy() {
        return energyStorage.getStoredEnergy();
    }

    public int getEnergyCapacity() {
        return energyStorage.getCapacity();
    }

    public int getLastGeneration() {
        return lastGeneration;
    }

    public ContainerData getMenuData() {
        return menuData;
    }

    public int extractThroughConnector(final int amount, final boolean simulate) {
        final ResolvedMultiblockDefinition active = getActiveDefinition();
        if (!formed || active == null) {
            return 0;
        }
        return energyStorage.extractEnergy(Math.min(amount, active.definition().tier().maxOutput()), simulate);
    }

    public void onRemoved() {
        clearConnectorLinks();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.energeticenvironments.wind_generator_controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
        return new WindGeneratorControllerMenu(containerId, inventory, this);
    }

    public boolean handleMenuButton(final Player player, final int buttonId) {
        if (level == null || level.isClientSide()) {
            return false;
        }

        return switch (buttonId) {
            case WindGeneratorControllerMenu.BUTTON_PREVIOUS_TIER -> cycleTier(-1);
            case WindGeneratorControllerMenu.BUTTON_NEXT_TIER -> cycleTier(1);
            case WindGeneratorControllerMenu.BUTTON_VALIDATE -> validateSelectedStructure();
            case WindGeneratorControllerMenu.BUTTON_AUTOBUILD -> autoBuildSelectedStructure();
            default -> false;
        };
    }

    @Override
    protected void saveAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        final NonNullList<ItemStack> items = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            items.set(slot, inventory.getItem(slot));
        }
        ContainerHelper.saveAllItems(tag, items, registries);

        tag.putInt("selected_tier_index", selectedTierIndex);
        tag.putBoolean("formed", formed);
        tag.putInt("energy", energyStorage.getStoredEnergy());
        if (activeDefinitionId != null) {
            tag.putString("active_definition", activeDefinitionId.toString());
        }
    }

    @Override
    protected void loadAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.clearContent();

        final NonNullList<ItemStack> items = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        for (int slot = 0; slot < items.size(); slot++) {
            inventory.setItem(slot, items.get(slot));
        }

        selectedTierIndex = Math.max(0, tag.getInt("selected_tier_index"));
        formed = tag.getBoolean("formed");
        energyStorage.loadStoredEnergy(tag.getInt("energy"));
        final String definitionId = tag.getString("active_definition");
        activeDefinitionId = definitionId.isBlank() ? null : ResourceLocation.tryParse(definitionId);
    }

    @Override
    public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(final int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack removeItem(final int slot, final int amount) {
        return inventory.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slot) {
        return inventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(final int slot, final ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(final Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    private void serverTick() {
        tickCounter++;
        final ResolvedMultiblockDefinition selected = getSelectedDefinition();
        configureEnergyStorage(selected);

        if (tickCounter % REFRESH_INTERVAL == 0) {
            refreshStructureState();
        }

        if (!formed) {
            lastGeneration = 0;
            return;
        }

        final ResolvedMultiblockDefinition active = getActiveDefinition();
        if (active == null) {
            setFormed(null);
            return;
        }

        lastGeneration = WindGenerationCalculator.calculateGeneration(level, worldPosition, getFacing(), active);
        if (lastGeneration > 0) {
            energyStorage.insertImmediate(lastGeneration);
        }

        for (final BlockPos connectorPos : active.connectorPositions()) {
            final BlockPos worldConnectorPos = StructureTransform.toWorld(worldPosition, connectorPos, getFacing());
            EnergyTransferHelper.pushEnergyToNeighbors(level, worldConnectorPos, energyStorage, active.definition().tier().maxOutput());
        }
    }

    private void refreshStructureState() {
        final ResolvedMultiblockDefinition active = getActiveDefinition();
        if (formed && (active == null || !MultiblockMatcher.matches(level, worldPosition, getFacing(), active))) {
            setFormed(null);
        } else if (formed && active != null) {
            linkConnectors(active);
        }

        refreshSummary(formed ? getActiveDefinition() : getSelectedDefinition());
    }

    private void refreshSummary(@Nullable final ResolvedMultiblockDefinition definition) {
        Arrays.fill(requiredCounts, 0);
        Arrays.fill(availableCounts, 0);
        completionPercent = 0;
        obstructionCount = 0;
        buildReady = false;

        if (definition == null || level == null) {
            return;
        }

        for (final var entry : definition.requiredByGroup().entrySet()) {
            requiredCounts[entry.getKey().ordinal()] = entry.getValue();
        }

        final MultiblockMatcher.ScanResult scan = MultiblockMatcher.scan(level, worldPosition, getFacing(), definition, inventory);
        for (final MaterialGroup group : MaterialGroup.values()) {
            availableCounts[group.ordinal()] = scan.availableByGroup().getOrDefault(group, 0);
        }
        completionPercent = scan.completionPercent();
        obstructionCount = scan.obstructionCount();
        buildReady = scan.buildReady();
        setChanged();
    }

    private void configureEnergyStorage(@Nullable final ResolvedMultiblockDefinition definition) {
        if (definition == null) {
            energyStorage.configure(0, 0, 0);
        } else {
            energyStorage.configure(definition.definition().tier().energyCapacity(), Integer.MAX_VALUE, definition.definition().tier().maxOutput());
        }
    }

    private boolean cycleTier(final int delta) {
        if (formed) {
            return false;
        }
        final List<ResolvedMultiblockDefinition> definitions = getDefinitions();
        if (definitions.isEmpty()) {
            return false;
        }
        selectedTierIndex = Math.floorMod(selectedTierIndex + delta, definitions.size());
        refreshSummary(getSelectedDefinition());
        syncBlockState();
        return true;
    }

    private boolean validateSelectedStructure() {
        final ResolvedMultiblockDefinition selected = getSelectedDefinition();
        if (selected == null || level == null) {
            return false;
        }
        refreshSummary(selected);
        if (!MultiblockMatcher.matches(level, worldPosition, getFacing(), selected)) {
            return false;
        }
        setFormed(selected);
        return true;
    }

    private boolean autoBuildSelectedStructure() {
        final ResolvedMultiblockDefinition selected = getSelectedDefinition();
        if (selected == null || level == null || formed) {
            return false;
        }

        final MultiblockMatcher.ScanResult scan = MultiblockMatcher.scan(level, worldPosition, getFacing(), selected, inventory);
        if (!scan.buildReady()) {
            refreshSummary(selected);
            return false;
        }

        int placed = 0;
        for (final ResolvedMultiblockDefinition.ResolvedStructureCell cell : scan.missingCells()) {
            if (placed >= selected.definition().autoBuild().blocksPerOperation()) {
                break;
            }
            final int slot = findMatchingSlot(cell.requirement());
            if (slot < 0) {
                continue;
            }
            final ItemStack stack = inventory.getItem(slot);
            if (!(stack.getItem() instanceof BlockItem blockItem)) {
                continue;
            }
            final BlockPos targetPos = StructureTransform.toWorld(worldPosition, cell.relativePos(), getFacing());
            if (!level.getBlockState(targetPos).canBeReplaced()) {
                continue;
            }

            level.setBlock(targetPos, blockItem.getBlock().defaultBlockState(), Block.UPDATE_ALL);
            stack.shrink(1);
            if (stack.isEmpty()) {
                inventory.setItem(slot, ItemStack.EMPTY);
            }
            placed++;
        }

        refreshSummary(selected);
        if (MultiblockMatcher.matches(level, worldPosition, getFacing(), selected)) {
            setFormed(selected);
        } else {
            syncBlockState();
        }
        return placed > 0;
    }

    private int findMatchingSlot(final com.deepdaddyttv.energeticenvironments.common.multiblock.BlockRequirement requirement) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (requirement.matches(inventory.getItem(slot))) {
                return slot;
            }
        }
        return -1;
    }

    private void setFormed(@Nullable final ResolvedMultiblockDefinition definition) {
        clearConnectorLinks();
        formed = definition != null;
        activeDefinitionId = definition == null ? null : definition.id();
        if (definition != null) {
            linkConnectors(definition);
        }
        refreshSummary(definition == null ? getSelectedDefinition() : definition);
        syncBlockState();
    }

    private void linkConnectors(final ResolvedMultiblockDefinition definition) {
        if (level == null) {
            return;
        }
        for (final BlockPos relativePos : definition.connectorPositions()) {
            final BlockPos connectorPos = StructureTransform.toWorld(worldPosition, relativePos, getFacing());
            if (level.getBlockEntity(connectorPos) instanceof WindGeneratorConnectorBlockEntity connector) {
                connector.setControllerReference(worldPosition);
            }
            level.invalidateCapabilities(connectorPos);
        }
    }

    private void clearConnectorLinks() {
        if (level == null) {
            return;
        }
        final ResolvedMultiblockDefinition active = getActiveDefinition();
        if (active == null) {
            return;
        }
        for (final BlockPos relativePos : active.connectorPositions()) {
            final BlockPos connectorPos = StructureTransform.toWorld(worldPosition, relativePos, getFacing());
            if (level.getBlockEntity(connectorPos) instanceof WindGeneratorConnectorBlockEntity connector) {
                connector.clearControllerReference();
            }
            level.invalidateCapabilities(connectorPos);
        }
    }

    private void onInventoryChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            refreshSummary(formed ? getActiveDefinition() : getSelectedDefinition());
        }
    }

    private void onEnergyChanged() {
        setChanged();
    }

    private void syncBlockState() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private Direction getFacing() {
        final BlockState state = getBlockState();
        return state.hasProperty(com.deepdaddyttv.energeticenvironments.common.block.WindGeneratorControllerBlock.FACING)
                ? state.getValue(com.deepdaddyttv.energeticenvironments.common.block.WindGeneratorControllerBlock.FACING)
                : Direction.NORTH;
    }

    private List<ResolvedMultiblockDefinition> getDefinitions() {
        return MultiblockDefinitionManager.definitionsForController(EEBlocks.WIND_GENERATOR_CONTROLLER.get());
    }

    @Nullable
    private ResolvedMultiblockDefinition getSelectedDefinition() {
        final List<ResolvedMultiblockDefinition> definitions = getDefinitions();
        if (definitions.isEmpty()) {
            return null;
        }
        final int clampedIndex = Math.min(selectedTierIndex, definitions.size() - 1);
        selectedTierIndex = clampedIndex;
        return definitions.get(clampedIndex);
    }

    @Nullable
    private ResolvedMultiblockDefinition getActiveDefinition() {
        return activeDefinitionId == null ? null : MultiblockDefinitionManager.get(activeDefinitionId).orElse(null);
    }

    private int getSelectedTierNumber() {
        final ResolvedMultiblockDefinition definition = getSelectedDefinition();
        return definition == null ? 0 : definition.tierNumber();
    }

    private int getActiveTierNumber() {
        final ResolvedMultiblockDefinition definition = getActiveDefinition();
        return definition == null ? 0 : definition.tierNumber();
    }

    private int getRequiredCount(final MaterialGroup group) {
        return requiredCounts[group.ordinal()];
    }

    private int getAvailableCount(final MaterialGroup group) {
        return availableCounts[group.ordinal()];
    }
}

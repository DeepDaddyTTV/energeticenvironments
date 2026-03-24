package com.deepdaddyttv.energeticenvironments.common.energy;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public final class NotifyingEnergyHandler extends SimpleEnergyHandler {
    private final Runnable onChanged;

    public NotifyingEnergyHandler(final int capacity, final int maxInsert, final int maxExtract, final Runnable onChanged) {
        super(capacity, maxInsert, maxExtract);
        this.onChanged = onChanged;
    }

    public void configure(final int capacity, final int maxInsert, final int maxExtract) {
        final int previous = this.energy;
        this.capacity = Math.max(0, capacity);
        this.maxInsert = Math.max(0, maxInsert);
        this.maxExtract = Math.max(0, maxExtract);
        this.energy = Math.min(this.energy, this.capacity);
        if (previous != this.energy) {
            onEnergyChanged(previous);
        }
    }

    public int insertImmediate(final int amount) {
        if (amount <= 0 || capacity <= energy || maxInsert <= 0) {
            return 0;
        }
        final int previous = this.energy;
        final int inserted = Math.min(amount, Math.min(maxInsert, capacity - energy));
        this.energy += inserted;
        onEnergyChanged(previous);
        return inserted;
    }

    public int extractImmediate(final int amount) {
        if (amount <= 0 || energy <= 0 || maxExtract <= 0) {
            return 0;
        }
        final int previous = this.energy;
        final int extracted = Math.min(amount, Math.min(maxExtract, energy));
        this.energy -= extracted;
        onEnergyChanged(previous);
        return extracted;
    }

    public void loadStoredEnergy(final int amount) {
        set(Math.max(0, amount));
    }

    public int getStoredEnergy() {
        return this.energy;
    }

    public int getCapacity() {
        return this.capacity;
    }

    @Override
    protected void onEnergyChanged(final int previousAmount) {
        this.onChanged.run();
    }
}

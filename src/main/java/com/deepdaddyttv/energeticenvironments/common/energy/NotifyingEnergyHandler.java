package com.deepdaddyttv.energeticenvironments.common.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

public final class NotifyingEnergyHandler extends EnergyStorage {
    private final Runnable onChanged;

    public NotifyingEnergyHandler(final int capacity, final int maxInsert, final int maxExtract, final Runnable onChanged) {
        super(capacity, maxInsert, maxExtract);
        this.onChanged = onChanged;
    }

    public void configure(final int capacity, final int maxInsert, final int maxExtract) {
        final int previous = this.energy;
        this.capacity = Math.max(0, capacity);
        this.maxReceive = Math.max(0, maxInsert);
        this.maxExtract = Math.max(0, maxExtract);
        this.energy = Math.min(this.energy, this.capacity);
        if (previous != this.energy) {
            onEnergyChanged(previous);
        }
    }

    public int insertImmediate(final int amount) {
        return receiveEnergy(amount, false);
    }

    public int extractImmediate(final int amount) {
        return extractEnergy(amount, false);
    }

    public void loadStoredEnergy(final int amount) {
        final int previous = this.energy;
        this.energy = Math.min(Math.max(0, amount), this.capacity);
        if (previous != this.energy) {
            onEnergyChanged(previous);
        }
    }

    public int getStoredEnergy() {
        return this.energy;
    }

    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        final int previous = this.energy;
        final int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received > 0 && previous != this.energy) {
            onEnergyChanged(previous);
        }
        return received;
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        final int previous = this.energy;
        final int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && extracted > 0 && previous != this.energy) {
            onEnergyChanged(previous);
        }
        return extracted;
    }

    private void onEnergyChanged(final int previousAmount) {
        this.onChanged.run();
    }
}

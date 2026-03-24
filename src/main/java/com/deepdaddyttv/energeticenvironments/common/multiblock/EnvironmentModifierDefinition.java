package com.deepdaddyttv.energeticenvironments.common.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public record EnvironmentModifierDefinition(
        String type,
        int minY,
        int maxY,
        double minMultiplier,
        double maxMultiplier,
        double visibleMultiplier,
        double blockedMultiplier,
        double clearMultiplier,
        double rainMultiplier,
        double thunderMultiplier
) {
    public static final Codec<EnvironmentModifierDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(EnvironmentModifierDefinition::type),
            Codec.INT.optionalFieldOf("min_y", 64).forGetter(EnvironmentModifierDefinition::minY),
            Codec.INT.optionalFieldOf("max_y", 256).forGetter(EnvironmentModifierDefinition::maxY),
            Codec.DOUBLE.optionalFieldOf("min_multiplier", 0.25D).forGetter(EnvironmentModifierDefinition::minMultiplier),
            Codec.DOUBLE.optionalFieldOf("max_multiplier", 1.0D).forGetter(EnvironmentModifierDefinition::maxMultiplier),
            Codec.DOUBLE.optionalFieldOf("visible_multiplier", 1.0D).forGetter(EnvironmentModifierDefinition::visibleMultiplier),
            Codec.DOUBLE.optionalFieldOf("blocked_multiplier", 0.0D).forGetter(EnvironmentModifierDefinition::blockedMultiplier),
            Codec.DOUBLE.optionalFieldOf("clear_multiplier", 1.0D).forGetter(EnvironmentModifierDefinition::clearMultiplier),
            Codec.DOUBLE.optionalFieldOf("rain_multiplier", 1.1D).forGetter(EnvironmentModifierDefinition::rainMultiplier),
            Codec.DOUBLE.optionalFieldOf("thunder_multiplier", 1.25D).forGetter(EnvironmentModifierDefinition::thunderMultiplier)
    ).apply(instance, EnvironmentModifierDefinition::new));

    public double evaluate(final Level level, final BlockPos position) {
        return switch (type) {
            case "altitude" -> evaluateAltitude(position.getY());
            case "sky_visibility" -> level.canSeeSky(position) ? visibleMultiplier : blockedMultiplier;
            case "weather" -> {
                if (level.isThundering() && level.isRainingAt(position)) {
                    yield thunderMultiplier;
                }
                if (level.isRainingAt(position)) {
                    yield rainMultiplier;
                }
                yield clearMultiplier;
            }
            default -> 1.0D;
        };
    }

    private double evaluateAltitude(final int y) {
        if (maxY <= minY) {
            return maxMultiplier;
        }
        if (y <= minY) {
            return minMultiplier;
        }
        if (y >= maxY) {
            return maxMultiplier;
        }
        final double progress = (double) (y - minY) / (double) (maxY - minY);
        return minMultiplier + (maxMultiplier - minMultiplier) * progress;
    }
}

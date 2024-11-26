package com.kaboomroads.tehshadur.border;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class ScaleChangingBorderProvider extends SimpleBorderProvider {
    public long timeOfCollapse;
    public final long maxTime;
    public final float threshold;
    public final float minRadius;
    public final float maxRadius;

    public ScaleChangingBorderProvider(AABB bounds, long timeOfCollapse, long maxTime, float threshold, float minRadius, float maxRadius) {
        super(bounds);
        this.timeOfCollapse = timeOfCollapse;
        this.maxTime = maxTime;
        this.threshold = threshold;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
    }

    @Override
    public void tick(long gameTime) {
        updateBounds(gameTime);
    }

    public void updateBounds(long gameTime) {
        float radius = getRadius(gameTime);
        Vec3 pos = bounds.getCenter();
        radius = Math.max(0.1F, radius);
        bounds = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
    }

    public void updateBounds(long gameTime, float partialTick) {
        float radius = getRadius(gameTime, partialTick);
        Vec3 pos = bounds.getCenter();
        radius = Math.max(0.1F, radius);
        bounds = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
    }

    public float getRadius(long gameTime) {
        long timer = timeOfCollapse - gameTime;
        float radius = timer > threshold
                ? minRadius + (timer - threshold) / (maxTime - minRadius) * (maxRadius - minRadius)
                : minRadius * (1 - (float) Math.pow(1 - timer / threshold, 3));
        radius = Math.max(0.1F, radius);
        return radius;
    }

    public float getRadius(long gameTime, float partialTick) {
        float timer = timeOfCollapse - (gameTime + partialTick);
        float radius = timer > threshold
                ? (minRadius + (timer - threshold) / (maxTime - minRadius) * (maxRadius - minRadius))
                : minRadius * (1 - (float) Math.pow(1 - timer / threshold, 3));
        radius = Math.max(0.1F, radius);
        return radius;
    }

    @Override
    public BorderProviderType<? extends ScaleChangingBorderProvider> getBorderProviderType() {
        return BorderProviderTypes.SCALE_CHANGING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScaleChangingBorderProvider that = (ScaleChangingBorderProvider) o;
        return timeOfCollapse == that.timeOfCollapse && maxTime == that.maxTime && Float.compare(threshold, that.threshold) == 0 && Float.compare(minRadius, that.minRadius) == 0 && Float.compare(maxRadius, that.maxRadius) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeOfCollapse, maxTime, threshold, minRadius, maxRadius);
    }

    @Override
    public String toString() {
        return "ScaleChangingBorderProvider{" +
                "timeOfCollapse=" + timeOfCollapse +
                ", maxTime=" + maxTime +
                ", threshold=" + threshold +
                ", minRadius=" + minRadius +
                ", maxRadius=" + maxRadius +
                ", bounds=" + bounds +
                '}';
    }
}

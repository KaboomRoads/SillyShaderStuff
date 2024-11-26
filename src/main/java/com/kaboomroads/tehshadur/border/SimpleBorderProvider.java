package com.kaboomroads.tehshadur.border;

import net.minecraft.world.phys.AABB;

import java.util.Objects;

public class SimpleBorderProvider implements BorderProvider {
    public AABB bounds;

    public SimpleBorderProvider(AABB bounds) {
        this.bounds = bounds;
    }

    @Override
    public AABB getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(AABB bounds) {
        this.bounds = bounds;
    }

    @Override
    public BorderProviderType<? extends SimpleBorderProvider> getBorderProviderType() {
        return BorderProviderTypes.SIMPLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleBorderProvider that = (SimpleBorderProvider) o;
        return Objects.equals(bounds, that.bounds);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bounds);
    }
}

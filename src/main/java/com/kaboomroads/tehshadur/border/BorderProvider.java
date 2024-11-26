package com.kaboomroads.tehshadur.border;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BorderProvider {
    AABB getBounds();

    void setBounds(AABB bounds);

    BorderProviderType<?> getBorderProviderType();

    default VoxelShape getBorderShape() {
        return Shapes.join(
                Shapes.INFINITY,
                Shapes.create(getBounds()),
                BooleanOp.ONLY_FIRST
        );
    }

    default void tick(long gameTime) {
    }

    default boolean intersectsnt(AABB entityBB) {
        AABB bounds = getBounds();
        return entityBB.minX <= bounds.minX || entityBB.minY <= bounds.minY || entityBB.minZ <= bounds.minZ || entityBB.maxX >= bounds.maxX || entityBB.maxY >= bounds.maxY || entityBB.maxZ >= bounds.maxZ;
    }

    default boolean isInsideCloseToBorder(Entity entity, AABB bounds) {
        double d = Math.max(Mth.absMax(bounds.getXsize(), bounds.getZsize()), 1.0);
        return getDistanceToBorder(entity.position()) < d * 2.0 && isWithinBounds(entity.position(), d);
    }

    default double getDistanceToBorder(Vec3 pos) {
        AABB bounds = getBounds();
        double d = pos.z - bounds.minZ;
        double e = bounds.maxZ - pos.z;
        double j = pos.y - bounds.minY;
        double k = bounds.maxY - pos.y;
        double f = pos.x - bounds.minX;
        double g = bounds.maxX - pos.x;
        double h = Math.min(f, g);
        h = Math.min(h, d);
        h = Math.min(h, e);
        h = Math.min(h, j);
        return Math.min(h, k);
    }

    default boolean isWithinBounds(Vec3 pos, double offset) {
        AABB bounds = getBounds();
        return pos.x >= bounds.minX - offset && pos.x < bounds.maxX + offset && pos.y >= bounds.minY - offset && pos.y < bounds.maxY + offset && pos.z >= bounds.minZ - offset && pos.z < bounds.maxZ + offset;
    }
}

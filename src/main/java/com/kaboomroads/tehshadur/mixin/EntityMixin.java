package com.kaboomroads.tehshadur.mixin;

import com.google.common.collect.ImmutableList;
import com.kaboomroads.tehshadur.border.BorderProvider;
import com.kaboomroads.tehshadur.border.SimpleBorderProvider;
import com.kaboomroads.tehshadur.mixinducks.EntityDuck;
import com.kaboomroads.tehshadur.networking.ModEntityDataSerializers;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDuck {
    @Unique
    private static final EntityDataAccessor<BorderProvider> DATA_BORDER_PROVIDER = SynchedEntityData.defineId(Entity.class, ModEntityDataSerializers.BORDER_PROVIDER);

    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract void setPos(Vec3 pos);

    @Shadow
    private static Vec3 collideWithShapes(Vec3 deltaMovement, AABB entityBB, List<VoxelShape> shapes) {
        throw new IllegalStateException();
    }

    @Shadow
    public abstract Level level();

    @Shadow
    @Final
    protected SynchedEntityData entityData;

    @Inject(method = "collectColliders", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;", ordinal = 0))
    private static void collideDivineDominance(@Nullable Entity entity, Level level, List<VoxelShape> collisions, AABB boundingBox, CallbackInfoReturnable<List<VoxelShape>> cir, @Local(ordinal = 0) ImmutableList.Builder<VoxelShape> builder) {
        if (entity != null) {
            BorderProvider divineDominance = ((EntityDuck) entity).tehshadur$getBorderProvider();
            if (divineDominance != null && divineDominance.getBounds() != null && divineDominance.isInsideCloseToBorder(entity, boundingBox))
                builder.add(divineDominance.getBorderShape());
        }
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void collideDivinely(CallbackInfo ci) {
        tehshadur$collideWithActiveBorder();
    }

    @Override
    public void tehshadur$collideWithActiveBorder() {
        BorderProvider activeBorder = tehshadur$getBorderProvider();
        if (activeBorder != null && activeBorder.getBounds() != null) {
            activeBorder.tick(level().getGameTime());
            AABB bb = getBoundingBox();
            Vec3 entityPos = position();
            Vec3 boundsCenter = activeBorder.getBounds().getCenter();
            Vec3 delta = entityPos.subtract(boundsCenter);
            if (activeBorder.intersectsnt(bb)) {
                Vec3 dm = collideWithShapes(delta, getBoundingBox().move(delta.scale(-1)), List.of(activeBorder.getBorderShape()));
                setPos(boundsCenter.add(dm));
            }
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V", ordinal = 0))
    private void defineSynchedData(EntityType<?> entityType, Level level, CallbackInfo ci, @Local(ordinal = 0) SynchedEntityData.Builder builder) {
        builder.define(DATA_BORDER_PROVIDER, new SimpleBorderProvider(null));
    }

//    @Inject(method = "saveWithoutId", at = @At("TAIL"))
//    private void saveDivineDominance(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
//        BorderProvider borderProvider = tehshadur$getBorderProvider();
//        if (borderProvider != null) tag.put("border_provider", ((BorderProviderType<BorderProvider>) borderProvider.getBorderProviderType()).save(borderProvider)));
//    }
//
//    @Inject(method = "load", at = @At("TAIL"))
//    private void loadDivineDominance(CompoundTag tag, CallbackInfo ci) {
//        if (tag.contains("border_provider", Tag.TAG_COMPOUND)) tehshadur$setBorderProvider(BorderProviderType.load(tag.getCompound("border_provider")));
//    }

    @Override
    public BorderProvider tehshadur$getBorderProvider() {
        return entityData.get(DATA_BORDER_PROVIDER);
    }

    @Override
    public void tehshadur$setBorderProvider(BorderProvider borderProvider) {
        entityData.set(DATA_BORDER_PROVIDER, borderProvider);
    }
}

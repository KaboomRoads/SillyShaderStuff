package com.kaboomroads.tehshadur.mixin;

import com.kaboomroads.tehshadur.border.BorderProvider;
import com.kaboomroads.tehshadur.mixinducks.EntityDuck;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CollisionGetter.class)
public interface CollisionGetterMixin {
    @Inject(method = "noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/CollisionGetter;borderCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Lnet/minecraft/world/phys/shapes/VoxelShape;"), cancellable = true)
    default void inject_noCollision(Entity entity, AABB bb, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        BorderProvider divineDominance = ((EntityDuck) entity).tehshadur$getBorderProvider();
        if (divineDominance != null) {
            VoxelShape voxelShape2 = divineDominanceCollision(bb, divineDominance);
            cir.setReturnValue(voxelShape2 == null || !Shapes.joinIsNotEmpty(voxelShape2, Shapes.create(bb), BooleanOp.AND));
        }
    }

    @Unique
    private VoxelShape divineDominanceCollision(AABB box, BorderProvider borderProvider) {
        return borderProvider.getBounds() != null && borderProvider.intersectsnt(box) ? borderProvider.getBorderShape() : null;
    }
}

package com.kaboomroads.tehshadur.client.mixin;

import com.kaboomroads.tehshadur.client.mixinducks.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraDuck {
    @Shadow
    protected abstract void setPosition(Vec3 pos);

    @Shadow
    public abstract Vec3 getPosition();

    @Shadow
    public abstract Quaternionf rotation();

    @Unique
    private float screenShakeStrength;
    @Unique
    private float screenShakeInterval;
    @Unique
    private int screenShakeTime;
    @Unique
    private int screenShakeEaseInTime;
    @Unique
    private int screenShakeStayTime;
    @Unique
    private int screenShakeEaseOutTime;
    @Unique
    private float nextScreenShake = Float.MAX_VALUE;
    @Unique
    private Vec3 lastShakeValue = null;

    @Inject(method = "setup", at = @At(value = "TAIL"))
    private void screenShake(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (screenShakeTime > 0) {
            float f = (float) screenShakeTime - partialTick;
            if (f <= nextScreenShake) {
                nextScreenShake = f - screenShakeInterval;
                float i = 1.0F;
                if (f > screenShakeEaseOutTime + screenShakeStayTime) {
                    float g = (float) (screenShakeEaseInTime + screenShakeStayTime + screenShakeEaseOutTime) - f;
                    i = (g / (float) screenShakeEaseInTime);
                }
                if (f <= screenShakeEaseOutTime) i = (f / (float) screenShakeEaseOutTime);
                Quaternionf rot = rotation();
                Vector3f right = new Vector3f(1, 0, 0).rotate(rot);
                Vector3f up = new Vector3f(0, 1, 0).rotate(rot);
                right.add(up);
                RandomSource random = entity.level().random;
                lastShakeValue = new Vec3((random.nextFloat() - 0.5F) * i * screenShakeStrength, (random.nextFloat() - 0.5F) * i * screenShakeStrength, (random.nextFloat() - 0.5F) * i * screenShakeStrength);
            }
            if (lastShakeValue != null) setPosition(getPosition().add(lastShakeValue));
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickScreenShake(CallbackInfo ci) {
        if (screenShakeTime > 0) screenShakeTime--;
        else {
            nextScreenShake = Float.MAX_VALUE;
            lastShakeValue = null;
        }
    }

    @Override
    public void tehshadur$setScreenShakeTimes(int screenShakeEaseInTime, int screenShakeStayTime, int screenShakeEaseOutTime) {
        this.screenShakeEaseInTime = screenShakeEaseInTime;
        this.screenShakeStayTime = screenShakeStayTime;
        this.screenShakeEaseOutTime = screenShakeEaseOutTime;
        screenShakeTime = this.screenShakeEaseInTime + this.screenShakeStayTime + this.screenShakeEaseOutTime;
    }

    @Override
    public void tehshadur$setScreenShake(float screenShakeStrength, float screenShakeInterval) {
        this.screenShakeStrength = screenShakeStrength;
        this.screenShakeInterval = screenShakeInterval;
    }
}

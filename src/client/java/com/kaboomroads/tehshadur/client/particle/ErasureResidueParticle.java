package com.kaboomroads.tehshadur.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ErasureResidueParticle extends TextureSheetParticle {
    public ErasureResidueParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.friction = 0.9F;
        this.quadSize *= 0.5F;
        this.lifetime = 280 + random.nextInt(20);
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (age % 5 == 0) {
            roll += (float) (Math.PI * 0.25F);
            oRoll = roll;
        }
        if (age >= 200) alpha = 1.0F - (float) (age - 200) / (lifetime - 200);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ErasureResidueParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}

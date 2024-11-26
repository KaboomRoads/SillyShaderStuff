package com.kaboomroads.tehshadur.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class OmenRemnantParticle extends SimpleAnimatedParticle {
    public double initialXd;
    public double initialYd;
    public double initialZd;

    public OmenRemnantParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0.0F);
        this.hasPhysics = false;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        initialXd = xd;
        initialYd = yd;
        initialZd = zd;
        this.quadSize *= 0.5F;
        this.lifetime = 180 + random.nextInt(20);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (age % 5 == 0) {
            roll += (float) (Math.PI * 0.25F);
            oRoll = roll;
        }
        if (age >= 100) {
            alpha = (float) lifetime / (age - 100);
            if (age == 140) {
                xd = -initialXd;
                yd = -initialYd;
                zd = -initialZd;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new OmenRemnantParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}

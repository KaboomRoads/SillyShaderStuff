package com.kaboomroads.tehshadur.particle;

import com.kaboomroads.tehshadur.TehShadur;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModParticles {
    public static final SimpleParticleType EXISTENCE = register("existence", FabricParticleTypes.simple());
    public static final SimpleParticleType OMEN_REMNANT = register("omen_remnant", FabricParticleTypes.simple());

    public static <T extends ParticleOptions> ParticleType<T> register(String name, ParticleType<T> particleType) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, name), particleType);
    }

    public static SimpleParticleType register(String name, SimpleParticleType particleType) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, name), particleType);
    }

    public static void init() {
    }
}

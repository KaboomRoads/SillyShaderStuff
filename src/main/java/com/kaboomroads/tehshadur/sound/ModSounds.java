package com.kaboomroads.tehshadur.sound;

import com.kaboomroads.tehshadur.TehShadur;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent DESOLIS_CANNON_LOAD = register("block.desolis_cannon.load");
    public static final SoundEvent DESOLIS_CANNON_FIRE = register("block.desolis_cannon.fire");
    public static final SoundEvent ERASURE_EXPLODE = register("entity.erasure.explode");

    private static Holder<SoundEvent> register(ResourceLocation name, ResourceLocation location, float range) {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, name, SoundEvent.createFixedRangeEvent(location, range));
    }

    private static SoundEvent register(String name) {
        return register(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, name));
    }

    private static SoundEvent register(ResourceLocation name) {
        return register(name, name);
    }

    private static Holder.Reference<SoundEvent> registerForHolder(String name) {
        return registerForHolder(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, name));
    }

    private static Holder.Reference<SoundEvent> registerForHolder(ResourceLocation name) {
        return registerForHolder(name, name);
    }

    private static SoundEvent register(ResourceLocation name, ResourceLocation location) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, name, SoundEvent.createVariableRangeEvent(location));
    }

    private static Holder.Reference<SoundEvent> registerForHolder(ResourceLocation name, ResourceLocation location) {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, name, SoundEvent.createVariableRangeEvent(location));
    }


    public static void init() {
    }
}

package com.kaboomroads.tehshadur.client.data.model;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

public class ModTextureMappings {
    public static TextureMapping columnBottom(ResourceLocation side, ResourceLocation bottom) {
        return new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.BOTTOM, bottom);
    }

    public static TextureMapping columnMiddle(ResourceLocation side) {
        return new TextureMapping().put(TextureSlot.SIDE, side);
    }

    public static TextureMapping columnTop(ResourceLocation side, ResourceLocation top) {
        return new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.TOP, top);
    }
}

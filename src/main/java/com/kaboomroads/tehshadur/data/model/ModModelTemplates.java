package com.kaboomroads.tehshadur.data.model;

import com.kaboomroads.tehshadur.TehShadur;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class ModModelTemplates {
    public static final ModelTemplate COLUMN_BOTTOM = create("column_bottom", TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate COLUMN_MIDDLE = create("column_middle", TextureSlot.SIDE);
    public static final ModelTemplate COLUMN_TOP = create("column_top", TextureSlot.TOP, TextureSlot.SIDE);

    private static ModelTemplate create(TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.empty(), Optional.empty(), textureSlots);
    }

    private static ModelTemplate create(String string, TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "block/" + string)), Optional.empty(), textureSlots);
    }

    private static ModelTemplate createItem(String string, TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "item/" + string)), Optional.empty(), textureSlots);
    }

    private static ModelTemplate create(String string, String string2, TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "block/" + string)), Optional.of(string2), textureSlots);
    }
}

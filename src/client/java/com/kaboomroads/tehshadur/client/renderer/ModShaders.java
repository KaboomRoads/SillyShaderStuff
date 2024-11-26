package com.kaboomroads.tehshadur.client.renderer;

import com.kaboomroads.tehshadur.TehShadur;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceLocation;

public class ModShaders {
    public static final VertexFormat POSITION_TEX_NORMAL = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV0", VertexFormatElement.UV0)
            .add("Normal", VertexFormatElement.NORMAL)
            .build();
    public static final ShaderProgram DIVINE_DOMINANCE = register("divine_dominance", POSITION_TEX_NORMAL);
    public static final ShaderProgram OMEN_MONOLITH = register("omen_monolith", DefaultVertexFormat.POSITION_TEX);
    public static final ShaderProgram OMEN_RINGS = register("omen_rings", POSITION_TEX_NORMAL);

    private static ShaderProgram register(String string, VertexFormat vertexFormat) {
        return register(string, vertexFormat, ShaderDefines.EMPTY);
    }

    private static ShaderProgram register(String string, VertexFormat vertexFormat, ShaderDefines shaderDefines) {
        ShaderProgram shaderProgram = new ShaderProgram(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "core/" + string), vertexFormat, shaderDefines);
        CoreShaders.PROGRAMS.add(shaderProgram);
        return shaderProgram;
    }

    public static void init() {
    }
}

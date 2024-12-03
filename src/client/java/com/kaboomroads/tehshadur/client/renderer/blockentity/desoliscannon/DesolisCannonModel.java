package com.kaboomroads.tehshadur.client.renderer.blockentity.desoliscannon;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.client.renderer.blockentity.BlockEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class DesolisCannonModel extends BlockEntityModel<DesolisCannonRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "desolis_cannon"), "main");
    public final ModelPart segment_1;
    public final ModelPart segment_2;

    public DesolisCannonModel(ModelPart root) {
        super(root.getChild("root"));
        this.segment_1 = this.root.getChild("segment_1");
        this.segment_2 = this.segment_1.getChild("segment_2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition segment_1 = root.addOrReplaceChild("segment_1", CubeListBuilder.create().texOffs(0, 26).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -20.0F, 0.0F));
        segment_1.addOrReplaceChild("segment_2", CubeListBuilder.create().texOffs(24, 26).addBox(-2.0F, -22.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(DesolisCannonRenderState renderState) {
        super.setupAnim(renderState);
        animate(renderState.fireAnimationState, DesolisCannonAnimation.FIRE, renderState.ageInTicks);
    }
}
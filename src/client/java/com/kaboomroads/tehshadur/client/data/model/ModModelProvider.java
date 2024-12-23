package com.kaboomroads.tehshadur.client.data.model;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.block.custom.DesolisAntennaBlock;
import com.kaboomroads.tehshadur.block.custom.DivineDominanceBlock;
import com.kaboomroads.tehshadur.block.custom.OmenMonolithBlock;
import com.kaboomroads.tehshadur.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.List;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        createDivineDominance(generator, ModBlocks.DIVINE_DOMINANCE, DivineDominanceBlock.PART);
        createDivineDominance(generator, ModBlocks.OMEN_MONOLITH, OmenMonolithBlock.PART);
        createDesolisCannon(generator, ModBlocks.DESOLIS_CANNON);
        createDesolisAntenna(generator, ModBlocks.DESOLIS_ANTENNA);
        createErasureFire(generator, ModBlocks.ERASURE_FIRE);
    }

    private void createErasureFire(BlockModelGenerators generator, Block block) {
        List<ResourceLocation> list = generator.createFloorFireModels(block);
        List<ResourceLocation> list2 = generator.createSideFireModels(block);
        generator.blockStateOutput
                .accept(
                        MultiPartGenerator.multiPart(block)
                                .with(BlockModelGenerators.wrapModels(list, variant -> variant))
                                .with(BlockModelGenerators.wrapModels(list2, variant -> variant))
                                .with(BlockModelGenerators.wrapModels(list2, variant -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))
                                .with(BlockModelGenerators.wrapModels(list2, variant -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)))
                                .with(BlockModelGenerators.wrapModels(list2, variant -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))
                );
    }


    public static void createDesolisAntenna(BlockModelGenerators generator, Block block) {
        generator.blockStateOutput
                .accept(MultiVariantGenerator.multiVariant(block)
                        .with(PropertyDispatch.property(DesolisAntennaBlock.PART).generate(part -> {
                            TextureMapping damn = new TextureMapping().put(TextureSlot.TEXTURE, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "block/desolis"));
                            if (part == 0)
                                return Variant.variant().with(VariantProperties.MODEL, ModModelTemplates.DESOLIS_ANTENNA_0.createWithSuffix(block, "_0", damn, generator.modelOutput));
                            else
                                return Variant.variant().with(VariantProperties.MODEL, ModModelTemplates.DESOLIS_ANTENNA_1.createWithSuffix(block, "_1", damn, generator.modelOutput));
                        }))
                );
    }

    public static void createDesolisCannon(BlockModelGenerators generator, Block block) {
        generator.blockStateOutput
                .accept(
                        MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, ModModelTemplates.DESOLIS_CANNON.create(block, new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block)).put(TextureSlot.PARTICLE, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "block/desolis")), generator.modelOutput)))
                                .with(BlockModelGenerators.createHorizontalFacingDispatch())
                );
        generator.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block, "_item"));
    }

    public static void createDivineDominance(BlockModelGenerators generator, Block block, IntegerProperty partProperty) {
        generator.blockStateOutput
                .accept(MultiVariantGenerator.multiVariant(block)
                        .with(PropertyDispatch.property(partProperty).generate(part -> {
                            if (part == 0)
                                return Variant.variant().with(VariantProperties.MODEL, ModModelTemplates.COLUMN_BOTTOM.createWithSuffix(block, "_" + part, ModTextureMappings.columnBottom(TextureMapping.getBlockTexture(block, "_side_" + part), TextureMapping.getBlockTexture(block, "_bottom")), generator.modelOutput));
                            else if (part == partProperty.max)
                                return Variant.variant().with(VariantProperties.MODEL, ModModelTemplates.COLUMN_TOP.createWithSuffix(block, "_" + part, ModTextureMappings.columnTop(TextureMapping.getBlockTexture(block, "_side_" + part), TextureMapping.getBlockTexture(block, "_top")), generator.modelOutput));
                            return Variant.variant().with(VariantProperties.MODEL, ModModelTemplates.COLUMN_MIDDLE.createWithSuffix(block, "_" + part, ModTextureMappings.columnMiddle(TextureMapping.getBlockTexture(block, "_side_" + part)), generator.modelOutput));
                        }))
                );
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(ModItems.DIVINE_DOMINANCE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.OMEN_MONOLITH, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.OBLITERATION_ROUND, ModelTemplates.FLAT_ITEM);
        generator.declareCustomModelItem(ModItems.DESOLIS_RECIEVER);
    }
}

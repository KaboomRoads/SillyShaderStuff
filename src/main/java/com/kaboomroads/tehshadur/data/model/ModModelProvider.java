package com.kaboomroads.tehshadur.data.model;

import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.block.custom.DivineDominanceBlock;
import com.kaboomroads.tehshadur.block.custom.OmenMonolithBlock;
import com.kaboomroads.tehshadur.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        createDivineDominance(generator, ModBlocks.DIVINE_DOMINANCE, DivineDominanceBlock.PART);
        createDivineDominance(generator, ModBlocks.OMEN_MONOLITH, OmenMonolithBlock.PART);
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
    }
}

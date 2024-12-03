package com.kaboomroads.tehshadur.data;

import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    protected ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    protected ModLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
        builder.add(ModBlocks.DIVINE_DOMINANCE, "Divine Dominance");
        builder.add(ModBlocks.OMEN_MONOLITH, "Omen Monolith");
        builder.add(ModBlocks.DESOLIS_CANNON, "Desolis Cannon");
        builder.add(ModBlocks.DESOLIS_ANTENNA, "Desolis Antenna");
        builder.add(ModBlocks.ERASURE_FIRE, "Erasure Fire");
        builder.add(ModItems.DESOLIS_RECIEVER, "Desolis Reciever");
        builder.add(ModItems.OBLITERATION_ROUND, "Obliteration Round");
        builder.add("item.tehshadur.desolis_reciever.connect", "Antenna paired");
        builder.add("item.tehshadur.desolis_reciever.clear", "Pairings cleared");
        builder.add("item.tehshadur.desolis_reciever.signal_issue", "Cannot receive signal");
        builder.add("subtitle.tehshadur.block.desolis_cannon.load", "Desolis Cannon loaded");
        builder.add("subtitle.tehshadur.block.desolis_cannon.fire", "Desolis Cannon fires");
        builder.add("subtitle.tehshadur.entity.erasure.explode", "Erasure starts");
    }
}

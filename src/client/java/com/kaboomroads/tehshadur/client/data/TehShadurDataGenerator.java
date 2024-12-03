package com.kaboomroads.tehshadur.client.data;

import com.kaboomroads.tehshadur.client.data.model.ModModelProvider;
import com.kaboomroads.tehshadur.client.data.tags.ModBlockTagProvider;
import com.kaboomroads.tehshadur.client.data.tags.ModItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class TehShadurDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        ModBlockTagProvider blockTagProvider = pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider((output, lookup) -> new ModItemTagProvider(output, lookup, blockTagProvider));
        pack.addProvider(ModLootTableProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModLanguageProvider::new);
    }
}

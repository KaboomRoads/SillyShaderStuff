package com.kaboomroads.tehshadur;

import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.border.BorderProviderTypes;
import com.kaboomroads.tehshadur.item.ModItems;
import com.kaboomroads.tehshadur.networking.ModEntityDataSerializers;
import com.kaboomroads.tehshadur.networking.ModPackets;
import net.fabricmc.api.ModInitializer;

public class TehShadur implements ModInitializer {
    public static final String MOD_ID = "tehshadur";

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModPackets.init();
        BorderProviderTypes.init();
        ModEntityDataSerializers.init();
    }
}

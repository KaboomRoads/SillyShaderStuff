package com.kaboomroads.tehshadur;

import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.border.BorderProviderTypes;
import com.kaboomroads.tehshadur.command.CustomAnimationCommand;
import com.kaboomroads.tehshadur.entity.ModEntities;
import com.kaboomroads.tehshadur.entity.ModEntityDataSerializers;
import com.kaboomroads.tehshadur.item.ModItems;
import com.kaboomroads.tehshadur.networking.ModPackets;
import com.kaboomroads.tehshadur.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

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
        ModEntities.init();
        ModSounds.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            CustomAnimationCommand.register(dispatcher);
        });
    }
}

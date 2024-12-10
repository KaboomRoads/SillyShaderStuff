package com.kaboomroads.tehshadur.command;

import com.kaboomroads.tehshadur.networking.payload.EntityAnimationPayload;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class CustomAnimationCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("animate")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("id", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int id = IntegerArgumentType.getInteger(context, "id");
                                            Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                            for (Entity target : targets)
                                                for (ServerPlayer player : context.getSource().getLevel().players())
                                                    player.connection.send(new ClientboundCustomPayloadPacket(new EntityAnimationPayload(target.getId(), id)));
                                            return 1;
                                        }))
                        )
        );
    }
}

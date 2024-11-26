package com.kaboomroads.tehshadur.block.entity.custom;

import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.border.BorderProviderType;
import com.kaboomroads.tehshadur.border.ScaleChangingBorderProvider;
import com.kaboomroads.tehshadur.mixinducks.EntityDuck;
import com.kaboomroads.tehshadur.networking.payload.FlashPayload;
import com.kaboomroads.tehshadur.networking.payload.FovPayload;
import com.kaboomroads.tehshadur.networking.payload.ScreenShakePayload;
import com.kaboomroads.tehshadur.particle.ModParticles;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

public class OmenMonolithBlockEntity extends BlockEntity {
    public ScaleChangingBorderProvider borderProvider = new ScaleChangingBorderProvider(
            null,
            Long.MIN_VALUE,
            2000,
            20,
            10,
            50
    );
    public HashSet<Entity> entities = new HashSet<>();

    public OmenMonolithBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.OMEN_MONOLITH, pos, blockState);
    }

    public OmenMonolithBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag listTag = new ListTag();
        for (Entity entity : entities) {
            System.out.println("SAVED ENTITY");
            listTag.add(NbtUtils.createUUID(entity.getUUID()));
        }
        tag.put("entities", listTag);
        tag.put("border_provider", ((BorderProviderType<ScaleChangingBorderProvider>) borderProvider.getBorderProviderType()).save(borderProvider, registries));
    }

    public HashSet<UUID> entityUUIDs = new HashSet<>();

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        borderProvider = BorderProviderType.load(tag.getCompound("border_provider"), registries);
        entities.clear();
        entityUUIDs.clear();
        ListTag listTag = tag.getList("entities", Tag.TAG_INT_ARRAY);
        for (Tag uuid : listTag) {
            System.out.println("LOADED ENTITY");
            entityUUIDs.add(NbtUtils.loadUUID(uuid));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public static void serverTick(ServerLevel level, BlockPos blockPos, BlockState blockState, OmenMonolithBlockEntity blockEntity) {
        for (UUID uuid : blockEntity.entityUUIDs) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) blockEntity.entities.add(entity);
        }
        blockEntity.entityUUIDs.clear();
        long time = level.getGameTime();
        blockEntity.tickBorder(blockPos, blockState, time);
        blockEntity.entities.addAll(level.getEntities((Entity) null, blockEntity.borderProvider.bounds, e -> blockEntity.borderProvider.bounds.contains(e.position())));
        for (Entity entity : blockEntity.entities)
            ((EntityDuck) entity).tehshadur$setBorderProvider(blockEntity.borderProvider);
        if (blockEntity.borderProvider.bounds != null && time >= blockEntity.borderProvider.timeOfCollapse)
            blockEntity.collapse(level, blockPos);
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, OmenMonolithBlockEntity blockEntity) {
        long time = level.getGameTime();
        blockEntity.tickBorder(blockPos, blockState, time);
    }

    public void tickBorder(BlockPos blockPos, BlockState blockState, long time) {
        if (borderProvider.timeOfCollapse == Long.MIN_VALUE) {
            borderProvider.timeOfCollapse = time + borderProvider.maxTime;
            setChanged();
            getLevel().sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL);
        }
        if (borderProvider.bounds == null) borderProvider.bounds = new AABB(blockPos).move(0, 1.5, 0);
        borderProvider.tick(time);
    }

    public void collapse(ServerLevel level, BlockPos blockPos) {
        for (Entity entity : entities) entity.kill(level);
        level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        Vec3 pos = borderProvider.bounds.getCenter();
        for (ServerPlayer player : level.getPlayers(player -> player.distanceToSqr(pos) <= 256 * 256)) {
            ServerPlayNetworking.send(player, new FlashPayload(ARGB.colorFromFloat(0.85F, 0.0F, 1.0F, 1.0F), 1, 50, 10));
            ServerPlayNetworking.send(player, new FovPayload(0.75F, 5, 1, 40));
            ServerPlayNetworking.send(player, new ScreenShakePayload(2.0F, 0.1F, 5, 25, 30));
            level.sendParticles(player, ModParticles.OMEN_REMNANT, false, pos.x, pos.y, pos.z, 100, 0, 0, 0, 0.25);
        }
    }
}

package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NpcBlockEntity extends BlockEntity implements Nameable {

    private static final Component DEFAULT_NAME = Component
            .translatable(WanderersOfTheRift.translationId("block", "npc"));

    private Holder<NpcIdentity> identity;

    public NpcBlockEntity(BlockPos pos, BlockState blockState) {
        super(WotrBlockEntities.NPC_BLOCK_ENTITY.get(), pos, blockState);
    }

    public @Nullable Holder<NpcIdentity> getNpcIdentity() {
        return identity;
    }

    @Override
    public @NotNull Component getName() {
        if (identity != null) {
            return NpcIdentity.getDisplayName(identity);
        }
        return DEFAULT_NAME;
    }

    @Override
    public @Nullable Component getCustomName() {
        if (identity != null) {
            return NpcIdentity.getDisplayName(identity);
        }
        return null;
    }

    public void interact(Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull Player player) {
        Holder<NpcIdentity> npcIdentity = getNpcIdentity();
        if (npcIdentity == null || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        npcIdentity.value().mobInteraction().interactWithBlock(npcIdentity, serverLevel, pos, block, player);
    }

    @Override
    protected void applyImplicitComponents(@NotNull BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.identity = input.get(WotrDataComponentType.NPC_IDENTITY);
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);

        if (identity != null) {
            builder.set(WotrDataComponentType.NPC_IDENTITY, this.identity);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (identity != null) {
            tag.putString("npc", identity.getKey().location().toString());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("npc")) {
            ResourceKey<NpcIdentity> npc = ResourceKey.create(WotrRegistries.Keys.NPCS,
                    ResourceLocation.parse(tag.getString("npc")));
            identity = registries.get(npc).orElse(null);
        }
    }
}

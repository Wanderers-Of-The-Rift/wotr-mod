package com.wanderersoftherift.wotr.core.npc;

import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

/**
 * Events related to NPCs
 */
public abstract class NpcEvent extends Event {

    private final Holder<NpcIdentity> npc;

    private final Mob mob;
    private final Level level;
    private final BlockPos blockPos;

    protected NpcEvent(Holder<NpcIdentity> npc, Mob mob) {
        this.npc = npc;
        this.mob = mob;

        this.level = mob.level();
        this.blockPos = null;
    }

    protected NpcEvent(Holder<NpcIdentity> npc, Level level, BlockPos blockPos) {
        this.npc = npc;
        this.level = level;
        this.blockPos = blockPos;

        this.mob = null;
    }

    /**
     * @return The involved NPC
     */
    public Holder<NpcIdentity> getNpc() {
        return npc;
    }

    /**
     * @return The level the NPC is in
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @return The position of the block, for block-based NPCs. Null otherwise.
     */
    public @Nullable BlockPos getBlockPos() {
        return blockPos;
    }

    /**
     * @return The mob for mob-based NPCs. Null otherwise.
     */
    public @Nullable Mob getMob() {
        return mob;
    }

    /**
     * @return Level access for the NPC
     */
    public ValidatingLevelAccess getAccess() {
        if (mob != null) {
            return ValidatingLevelAccess.create(mob);
        } else {
            return ValidatingLevelAccess.create(level, blockPos, level.getBlockState(blockPos).getBlock());
        }
    }

    /**
     * Event for when an NPC is interacted with by a player
     */
    public static class OnInteract extends NpcEvent {

        private final Player player;
        private final InteractionHand hand;
        private InteractionResult result = InteractionResult.PASS;

        public OnInteract(Holder<NpcIdentity> npc, Player player, InteractionHand hand, Mob mob) {
            super(npc, mob);
            this.player = player;
            this.hand = hand;
        }

        public OnInteract(Holder<NpcIdentity> npc, Player player, Level level, BlockPos block) {
            super(npc, level, block);
            this.player = player;
            this.hand = InteractionHand.OFF_HAND;
        }

        /**
         * @return The player interacting with the NPC
         */
        public Player getPlayer() {
            return player;
        }

        public InteractionHand getInteractionHand() {
            return hand;
        }

        /**
         * @return The current interaction result. If anything other than {@link InteractionResult.Pass} then further
         *         interaction will be skipped.
         */
        public InteractionResult getResult() {
            return result;
        }

        /**
         * @param result Sets the interaction result.
         */
        public void setResult(InteractionResult result) {
            this.result = result;
        }
    }
}

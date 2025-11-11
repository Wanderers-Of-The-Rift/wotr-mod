package com.wanderersoftherift.wotr.core.npc.interaction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class QuestReceiverInteract extends MenuInteraction {

    public static final MapCodec<QuestReceiverInteract> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NpcInteraction.DIRECT_CODEC.optionalFieldOf("fallback").forGetter(QuestReceiverInteract::fallback)
    ).apply(instance, QuestReceiverInteract::new));

    public QuestReceiverInteract(Optional<NpcInteraction> fallback) {
        super(fallback);
    }

    @Override
    public MapCodec<? extends NpcInteraction> getCodec() {
        return CODEC;
    }

    @Override
    protected boolean interact(
            Holder<NpcIdentity> npc,
            ValidatingLevelAccess access,
            ServerLevel level,
            Player player) {
        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        Optional<QuestState> questToHandIn = activeQuests.getQuestList()
                .stream()
                .filter(quest -> quest.getHandInTo().equals(npc))
                .findFirst();
        if (questToHandIn.isPresent()) {
            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestCompletionMenu(containerId, playerInventory,
                                    access, activeQuests, questToHandIn.get().getId()),
                            NpcIdentity.getDisplayName(npc))
            );
            return true;
        }
        return false;
    }
}

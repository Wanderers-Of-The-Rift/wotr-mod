package com.wanderersoftherift.wotr.gui.menu.slot;

import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuest;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class QuestItemStackHandler extends ItemStackHandler {

    private final Supplier<ActiveQuest> quest;

    public QuestItemStackHandler(Supplier<ActiveQuest> questSupplier) {
        this.quest = questSupplier;
    }

    public QuestItemStackHandler(Supplier<ActiveQuest> questSupplier, int size) {
        super(size);
        this.quest = questSupplier;
    }

    public QuestItemStackHandler(Supplier<ActiveQuest> questSupplier, NonNullList<ItemStack> stacks) {
        super(stacks);
        this.quest = questSupplier;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        ActiveQuest activeQuest = quest.get();
        for (int i = 0; i < activeQuest.goalCount(); i++) {
            if (!activeQuest.isGoalComplete(i) && activeQuest.getGoal(i) instanceof GiveItemGoal goal
                    && goal.item().test(stack)) {
                return true;
            }
        }

        return false;
    }

}

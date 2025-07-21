package com.wanderersoftherift.wotr.item.handler;

import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.core.quest.goal.GiveItemGoal;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * ItemStackHandler that only accepts quest items
 */
public class QuestItemStackHandler extends ItemStackHandler {

    private final Supplier<QuestState> quest;

    /**
     * @param questSupplier A supplier that provides the quest state to accept items for
     */
    public QuestItemStackHandler(Supplier<QuestState> questSupplier) {
        this(questSupplier, 1);
    }

    /**
     * @param questSupplier A supplier that provides the quest state to accept items for
     * @param size          The number of slots in this ItemStackHandler
     */
    public QuestItemStackHandler(Supplier<QuestState> questSupplier, int size) {
        super(size);
        this.quest = questSupplier;
    }

    public QuestItemStackHandler(Supplier<QuestState> questSupplier, NonNullList<ItemStack> stacks) {
        super(stacks);
        this.quest = questSupplier;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        QuestState questState = quest.get();
        for (int i = 0; i < questState.goalCount(); i++) {
            if (!questState.isGoalComplete(i) && questState.getGoal(i) instanceof GiveItemGoal goal
                    && goal.item().test(stack)) {
                return true;
            }
        }

        return false;
    }

}

package com.wanderersoftherift.wotr.core.goal.type;

import com.wanderersoftherift.wotr.core.goal.Goal;
import net.minecraft.world.item.crafting.Ingredient;

public interface ItemGoal extends Goal {

    /**
     * @return The item involved in this goal
     */
    Ingredient item();

    /**
     * @return The amount of items involved in this goal
     */
    int count();

}

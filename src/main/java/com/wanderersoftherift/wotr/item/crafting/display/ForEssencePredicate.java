package com.wanderersoftherift.wotr.item.crafting.display;

import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;

/**
 * Interface for factories that map essence predicates to a desired output for display
 * 
 * @param <T>
 */
public interface ForEssencePredicate<T> extends DisplayContentsFactory<T> {

    T forState(EssencePredicate predicate);
}

package com.wanderersoftherift.wotr.item.crafting.display;

import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;

/**
 * Trivial factory for handling EssencePredicates as themselves
 */
public class EssencePredicateContentsFactory implements ForEssencePredicate<EssencePredicate> {
    @Override
    public EssencePredicate forState(EssencePredicate predicate) {
        return predicate;
    }
}

package com.wanderersoftherift.wotr.interop.rei.common;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import me.shedaniel.rei.api.common.entry.type.EntryType;

/**
 * REI Entry types provided by Wanderers of the Rifts
 */
public final class WotrEntryTypes {
    public static final EntryType<EssencePredicate> ESSENCE = EntryType.deferred(WanderersOfTheRift.id("essence"));

    private WotrEntryTypes() {
    }
}

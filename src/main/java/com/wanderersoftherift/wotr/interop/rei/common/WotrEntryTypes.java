package com.wanderersoftherift.wotr.interop.rei.common;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import me.shedaniel.rei.api.common.entry.type.EntryType;

/**
 * REI Entry types provided by Wanderers of the Rifts
 */
public final class WotrEntryTypes {
    public static final EntryType<EssencePredicate> ESSENCE = EntryType.deferred(WanderersOfTheRift.id("essence"));
    public static final EntryType<RunegemData.ModifierGroup> MODIFIER_GROUP = EntryType
            .deferred(WanderersOfTheRift.id("modifier_group"));

    private WotrEntryTypes() {
    }
}

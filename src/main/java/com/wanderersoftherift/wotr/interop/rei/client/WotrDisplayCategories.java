package com.wanderersoftherift.wotr.interop.rei.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

/**
 * Identifiers for REI Display Categories provided by Wanderers of the Rifts
 */
public final class WotrDisplayCategories {

    public static final CategoryIdentifier<KeyForgeDisplay> KEY_FORGE = CategoryIdentifier.of(WanderersOfTheRift.MODID,
            "key_forge");

    private WotrDisplayCategories() {
    }
}

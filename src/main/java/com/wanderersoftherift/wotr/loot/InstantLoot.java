package com.wanderersoftherift.wotr.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface InstantLoot {

    Codec<InstantLoot> CODEC = WotrRegistries.INSTANT_LOOT_TYPES.byNameCodec()
            .dispatch("loot_type", InstantLoot::codec, Function.identity());

    MapCodec<? extends InstantLoot> codec();

    static boolean tryConsume(ItemStack stack, Player player) {
        var component = stack.get(WotrDataComponentType.INSTANT_LOOT);
        if (component != null) {
            component.applyToPlayer(player, stack.getCount());
            stack.setCount(0);
            return true;
        }
        return false;
    }

    void applyToPlayer(Player player, int count);

}

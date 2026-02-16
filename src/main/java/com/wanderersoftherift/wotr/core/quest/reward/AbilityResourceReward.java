package com.wanderersoftherift.wotr.core.quest.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record AbilityResourceReward(float amount, Holder<AbilityResource> abilityResource) implements Reward {
    public static final DualCodec<AbilityResourceReward> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance
                    .group(Codec.FLOAT.fieldOf("amount").forGetter(AbilityResourceReward::amount),
                            AbilityResource.HOLDER_CODEC.fieldOf("resource")
                                    .forGetter(AbilityResourceReward::abilityResource))
                    .apply(instance, AbilityResourceReward::new)),
            StreamCodec.composite(ByteBufCodecs.FLOAT, AbilityResourceReward::amount,
                    ByteBufCodecs.fromCodecWithRegistries(AbilityResource.HOLDER_CODEC),
                    AbilityResourceReward::abilityResource, AbilityResourceReward::new));

    @Override
    public DualCodec<? extends Reward> getType() {
        return TYPE;
    }

    @Override
    public void apply(Player player) {
        var abilityResourceData = player.getData(WotrAttachments.ABILITY_RESOURCE_DATA);
        abilityResourceData.useAmount(abilityResource, -amount);
    }
}

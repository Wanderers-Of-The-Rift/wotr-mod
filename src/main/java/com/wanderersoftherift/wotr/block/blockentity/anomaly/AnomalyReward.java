package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;

public record AnomalyReward(Optional<MobEffectInstance> effect/* potential alternative: it could cast an ability */,
        Optional<ResourceKey<LootTable>> lootKey) {
    public static final Codec<AnomalyReward> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    MobEffectInstance.CODEC.optionalFieldOf("effect").forGetter(AnomalyReward::effect),
                    ResourceKey.codec(Registries.LOOT_TABLE)
                            .optionalFieldOf("loot_table")
                            .forGetter(AnomalyReward::lootKey)
            ).apply(instance, AnomalyReward::new)
    );

    public static final Codec<Holder<AnomalyReward>> HOLDER_CODEC = LaxRegistryCodec
            .refOrDirect(WotrRegistries.Keys.ANOMALY_REWARD, DIRECT_CODEC);

    public void grantReward(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (effect.isPresent()) {
            var newInstance = new MobEffectInstance(effect.get().getEffect());
            newInstance.update(effect.get());
            player.addEffect(newInstance);
        }

        if (lootKey.isPresent()) {
            var loot = serverLevel.getServer().reloadableRegistries().getLootTable(lootKey.get());
            var lootContent = loot.getRandomItems(new LootParams.Builder(serverLevel).create(ContextKeySet.EMPTY));
            lootContent.forEach(player.getInventory()::placeItemBackInInventory);
        }
    }
}

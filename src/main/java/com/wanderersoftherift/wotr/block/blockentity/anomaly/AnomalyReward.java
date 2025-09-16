package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

public record AnomalyReward(MobEffectInstance effect/* potential alternative: it could cast an ability */,
        Holder<LootTable> loot) {
    public static final Codec<AnomalyReward> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    MobEffectInstance.CODEC.fieldOf("effect").forGetter(AnomalyReward::effect),
                    LootTable.CODEC.fieldOf("loot").forGetter(AnomalyReward::loot)
            ).apply(instance, AnomalyReward::new)
    );

    public static final Codec<Holder<AnomalyReward>> HOLDER_CODEC = LaxRegistryCodec
            .refOrDirect(WotrRegistries.Keys.ANOMALY_REWARD, DIRECT_CODEC);

    public void grantReward(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        var newInstance = new MobEffectInstance(effect.getEffect());
        newInstance.update(effect);
        player.addEffect(newInstance);
        var lootContent = loot.value().getRandomItems(new LootParams.Builder(serverLevel).create(ContextKeySet.EMPTY));
        lootContent.forEach(player.getInventory()::placeItemBackInInventory);
    }
}

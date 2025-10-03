package com.wanderersoftherift.wotr.entity.npc;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.trading.AvailableTrades;
import com.wanderersoftherift.wotr.gui.menu.TradingMenu;
import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * MobInteraction attachment for Merchant behavior
 */
public record MerchantInteract(ResourceKey<LootTable> lootTable) implements MobInteraction {
    public static final MapCodec<MerchantInteract> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(MerchantInteract::lootTable)
    ).apply(instance, MerchantInteract::new));

    @Override
    public MapCodec<? extends MobInteraction> getCodec() {
        return CODEC;
    }

    @Override
    public InteractionResult interact(Mob mob, Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        AvailableTrades availableTrades = player.getData(WotrAttachments.AVAILABLE_TRADES);
        IItemHandlerModifiable merchantInventory = availableTrades.getExisting(mob.getUUID());
        if (merchantInventory == null) {
            LootParams params = new LootParams.Builder(serverPlayer.serverLevel())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.PIGLIN_BARTER);
            LootTable table = params.getLevel().getServer().reloadableRegistries().getLootTable(lootTable);
            merchantInventory = availableTrades.generate(mob.getUUID(), table, params);
        }
        final IItemHandlerModifiable finalMerchantInventory = merchantInventory;
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new TradingMenu(containerId, playerInventory,
                                finalMerchantInventory, player.getData(WotrAttachments.WALLET),
                                ValidatingLevelAccess.create(mob)),
                        mob.getDisplayName())
        );

        return InteractionResult.CONSUME;
    }
}

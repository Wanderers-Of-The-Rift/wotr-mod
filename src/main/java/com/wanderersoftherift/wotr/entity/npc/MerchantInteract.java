package com.wanderersoftherift.wotr.entity.npc;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.trading.AvailableTrades;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.gui.menu.TradingMenu;
import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleMenuProvider;
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
    public void interact(Holder<NpcIdentity> npc, ValidatingLevelAccess access, ServerLevel level, Player player) {
        AvailableTrades availableTrades = player.getData(WotrAttachments.AVAILABLE_TRADES);

        IItemHandlerModifiable merchantInventory = availableTrades.getExisting(npc).orElseGet(() -> {
            LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.PIGLIN_BARTER);
            LootTable table = params.getLevel().getServer().reloadableRegistries().getLootTable(lootTable);
            return availableTrades.create(npc, table.getRandomItems(params));
        });

        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new TradingMenu(containerId, playerInventory,
                                merchantInventory, player.getData(WotrAttachments.WALLET), access),
                        NpcIdentity.getDisplayName(npc))
        );
    }

}

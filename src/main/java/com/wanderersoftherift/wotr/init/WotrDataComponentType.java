package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.ChainAbilityState;
import com.wanderersoftherift.wotr.abilities.attachment.TargetComponent;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.core.npc.trading.Price;
import com.wanderersoftherift.wotr.core.rift.RiftGenerationConfig;
import com.wanderersoftherift.wotr.core.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.item.LootBox;
import com.wanderersoftherift.wotr.item.ability.ActivatableAbility;
import com.wanderersoftherift.wotr.item.currency.CurrencyProvider;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import com.wanderersoftherift.wotr.item.riftkey.RiftKeyParameterData;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import com.wanderersoftherift.wotr.loot.InstantLoot;
import com.wanderersoftherift.wotr.util.listedit.ListEdit;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftPostProcessingStep;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WotrDataComponentType {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, WanderersOfTheRift.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GearSockets>> GEAR_SOCKETS = register(
            "gear_sockets", GearSockets.CODEC, null);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GearImplicits>> GEAR_IMPLICITS = register(
            "gear_implicits", GearImplicits.CODEC, null);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RunegemData>> RUNEGEM_DATA = register(
            "runegem_data", RunegemData.CODEC, null);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> INVENTORY_SNAPSHOT_ID = register(
            "inventory_snapshot_id", UUIDUtil.CODEC, null);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LootBox>> LOOT_BOX = register("loot_box",
            LootBox.CODEC, null);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ActivatableAbility>> ABILITY = register(
            "ability", ActivatableAbility.CODEC, ActivatableAbility.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AbilityUpgradePool>> ABILITY_UPGRADE_POOL = register(
            "ability_upgrade_pool", AbilityUpgradePool.CODEC, AbilityUpgradePool.STREAM_CODEC);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CurrencyProvider>> CURRENCY_PROVIDER = register(
            "currency_provider", CurrencyProvider.CODEC, CurrencyProvider.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Price>> PRICE = register(
            "price", Price.CODEC, Price.STREAM_CODEC
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<NpcIdentity>>> NPC_IDENTITY = register(
            "npc_identity", NpcIdentity.CODEC, NpcIdentity.STREAM_CODEC
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GEAR_RIFT_TIER = register(
            "gear_rift_tier", Codec.INT, ByteBufCodecs.INT);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<InstantLoot>> INSTANT_LOOT = register(
            "instant_loot", InstantLoot.CODEC, ByteBufCodecs.fromCodecWithRegistries(InstantLoot.CODEC));

    public static final class AbilityContextData {
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<ChainAbilityState>> CHAIN_ABILITY_STATE = register(
                "chain_ability_state", ChainAbilityState.CODEC, null
        );

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Set<ResourceLocation>>> CONDITIONS = register(
                "conditions", Codec.list(ResourceLocation.CODEC).xmap(Set::copyOf, List::copyOf), null
        );

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<TargetComponent>> TRIGGER_TARGET = register(
                "trigger_target", TargetComponent.CODEC, null
        );

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> PARENT_ABILITY = register(
                "parent_ability", UUIDUtil.CODEC, null
        );

        private AbilityContextData() {
        }
    }

    public static final class RiftKeyData {
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RIFT_TIER = register(
                "rift_config/tier", Codec.INT, ByteBufCodecs.INT);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<RiftTheme>>> RIFT_THEME = register(
                "rift_config/theme", RiftTheme.CODEC, RiftTheme.STREAM_CODEC);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<ObjectiveType>>> RIFT_OBJECTIVE = register(
                "rift_config/objective", ObjectiveType.CODEC, ObjectiveType.STREAM_CODEC);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> RIFT_SEED = register(
                "rift_config/seed", Codec.LONG, ByteBufCodecs.LONG);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<RiftGenerationConfig>>> GENERATOR_PRESET = register(
                "rift_config/generator_preset", RiftGenerationConfig.HOLDER_CODEC,
                ByteBufCodecs.fromCodecWithRegistries(RiftGenerationConfig.HOLDER_CODEC)
        );
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ListEdit<LayeredRiftLayout.LayoutLayer.Factory>>>> LAYOUT_LAYER_EDIT = register(
                "rift_config/layout_layers_edits",
                ListEdit.editCodec(LayeredRiftLayout.LayoutLayer.Factory.CODEC).listOf(),
                ByteBufCodecs.fromCodecWithRegistries(ListEdit.editCodec(LayeredRiftLayout.LayoutLayer.Factory.CODEC))
                        .apply(ByteBufCodecs.list())
        );
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ListEdit<RiftPostProcessingStep>>>> POST_STEPS_EDIT = register(
                "rift_config/post_steps_edits", ListEdit.editCodec(RiftPostProcessingStep.CODEC).listOf(),
                ByteBufCodecs.fromCodecWithRegistries(ListEdit.editCodec(RiftPostProcessingStep.CODEC))
                        .apply(ByteBufCodecs.list())
        );
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ListEdit<JigsawListProcessor>>>> JIGSAW_PROCESSORS_EDIT = register(
                "rift_config/jigsaw_processors_edits", ListEdit.editCodec(JigsawListProcessor.CODEC).listOf(),
                ByteBufCodecs.fromCodecWithRegistries(ListEdit.editCodec(JigsawListProcessor.CODEC))
                        .apply(ByteBufCodecs.list())
        );
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<RiftKeyParameterData>> RIFT_PARAMETERS = register(
                "rift_config/rift_parameters", RiftKeyParameterData.CODEC,
                ByteBufCodecs.fromCodecWithRegistries(RiftKeyParameterData.CODEC));

        private RiftKeyData() {
        }
    }

    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name,
            final Codec<T> codec,
            @Nullable final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        if (streamCodec == null) {
            return DATA_COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            return DATA_COMPONENTS.register(name,
                    () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }
    }

    static {
        var unused = RiftKeyData.RIFT_TIER; // invokes <cinit>
        var unused2 = AbilityContextData.CHAIN_ABILITY_STATE;
    }
}

package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.core.rift.RiftGenerationConfig;
import com.wanderersoftherift.wotr.item.LootBox;
import com.wanderersoftherift.wotr.item.ability.ActivatableAbility;
import com.wanderersoftherift.wotr.item.currency.CurrencyProvider;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.util.ListEdit;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.List;
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

    public static class RiftConfigWotrDataComponentType {
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ITEM_RIFT_TIER = register(
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
        var unused = RiftConfigWotrDataComponentType.ITEM_RIFT_TIER; // invokes <cinit>
    }
}

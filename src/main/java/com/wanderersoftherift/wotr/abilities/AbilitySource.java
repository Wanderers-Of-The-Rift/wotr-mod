package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEquipmentSlot;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.ability.AbilityModifier;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface AbilitySource {

    Codec<AbilitySource> DIRECT_CODEC = WotrRegistries.ABILITY_SOURCES.byNameCodec()
            .dispatch(AbilitySource::getType, DualCodec::codec);
    StreamCodec<RegistryFriendlyByteBuf, AbilitySource> STREAM_CODEC = ByteBufCodecs
            .registry(WotrRegistries.Keys.ABILITY_SOURCES)
            .dispatch(AbilitySource::getType, DualCodec::streamCodec);

    static AbilitySource byModifierSource(ModifierSource source, int effectIndex) {
        return new ModifierAbilitySource(source, effectIndex);
    }

    static MainAbilitySource sourceForSlot(int slot) {
        return new MainAbilitySource(AbilityEquipmentSlot.forSlot(slot));
    }

    DualCodec<? extends AbilitySource> getType();

    @Nullable ItemStack getItem(LivingEntity entity);

    Holder<Ability> getAbility(Entity entity);

    AbilityUpgradePool upgrades(Entity entity);

    record ModifierAbilitySource(ModifierSource base, int effectIndex) implements AbilitySource {

        public static final DualCodec<ModifierAbilitySource> TYPE = new DualCodec<>(
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        ModifierSource.DIRECT_CODEC.fieldOf("modifier_source").forGetter(ModifierAbilitySource::base),
                        Codec.INT.fieldOf("effect_index").forGetter(ModifierAbilitySource::effectIndex)
                ).apply(instance, ModifierAbilitySource::new)), StreamCodec.composite(
                        ModifierSource.STREAM_CODEC, ModifierAbilitySource::base, ByteBufCodecs.INT,
                        ModifierAbilitySource::effectIndex, ModifierAbilitySource::new
                )
        );

        @Override
        public Holder<Ability> getAbility(Entity entity) {
            if (base.getModifierEffects(entity).get(effectIndex) instanceof AbilityModifier ability) {
                return ability.providedAbility();
            }
            return null;
        }

        @Override
        public AbilityUpgradePool upgrades(Entity entity) {
            return AbilityUpgradePool.EMPTY; // todo how do we handle upgrades for these abilities
        }

        @Override
        public DualCodec<? extends AbilitySource> getType() {
            return TYPE;
        }

        @Override
        public @Nullable ItemStack getItem(LivingEntity entity) {
            if (base instanceof ModifierSource.ItemModifierSource itemModifierSource) {
                return itemModifierSource.getItem(entity);
            }
            return ItemStack.EMPTY;
        }
    }

    record MainAbilitySource(WotrEquipmentSlot slot) implements AbilitySource {

        public static final DualCodec<MainAbilitySource> TYPE = new DualCodec<>(
                WotrEquipmentSlot.DIRECT_CODEC.fieldOf("slot").xmap(MainAbilitySource::new, MainAbilitySource::slot),
                StreamCodec.composite(
                        WotrEquipmentSlot.STREAM_CODEC, MainAbilitySource::slot, MainAbilitySource::new
                )
        );

        @Override
        public DualCodec<? extends AbilitySource> getType() {
            return TYPE;
        }

        @Override
        public @Nullable ItemStack getItem(LivingEntity entity) {
            return slot.getContent(entity);
        }

        @Override
        public Holder<Ability> getAbility(Entity entity) {
            return slot.getContent(entity).get(WotrDataComponentType.ABILITY).ability();
        }

        @Override
        public AbilityUpgradePool upgrades(Entity entity) {
            return slot.getContent(entity).get(WotrDataComponentType.ABILITY_UPGRADE_POOL);
        }

        public Holder<Ability> getMainAbility(Entity entity) {
            return slot.getContent(entity).get(WotrDataComponentType.ABILITY).ability();
        }
    }
}

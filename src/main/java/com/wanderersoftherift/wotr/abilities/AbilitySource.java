package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEquipmentSlot;
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
import java.util.List;
import java.util.stream.Stream;

public interface AbilitySource {

    Codec<AbilitySource> DIRECT_CODEC = WotrRegistries.ABILITY_SOURCES.byNameCodec()
            .dispatch(AbilitySource::getType, DualCodec::codec);
    StreamCodec<RegistryFriendlyByteBuf, AbilitySource> STREAM_CODEC = ByteBufCodecs
            .registry(WotrRegistries.Keys.ABILITY_SOURCES)
            .dispatch(AbilitySource::getType, DualCodec::streamCodec);

    static AbilitySource byModifierSource(ModifierSource source) {
        return new ModifierAbilitySource(source);
    }

    static MainAbilitySource sourceForSlot(int slot) {
        return new MainAbilitySource(AbilityEquipmentSlot.forSlot(slot));
    }

    DualCodec<? extends AbilitySource> getType();

    @Nullable ItemStack getItem(LivingEntity entity);

    List<Holder<Ability>> getAbilities(Entity entity);

    record ModifierAbilitySource(ModifierSource base) implements AbilitySource {

        public static final DualCodec<ModifierAbilitySource> TYPE = new DualCodec<>(
                ModifierSource.DIRECT_CODEC.fieldOf("modifier_source")
                        .xmap(ModifierAbilitySource::new, ModifierAbilitySource::base),
                StreamCodec.composite(
                        ModifierSource.STREAM_CODEC, ModifierAbilitySource::base, ModifierAbilitySource::new
                )
        );

        @Override
        public List<Holder<Ability>> getAbilities(Entity entity) {
            return base.getModifierEffects(entity)
                    .stream()
                    .flatMap(
                            it -> it instanceof AbilityModifier modifier ? Stream.of(modifier.providedAbility()) : null)
                    .toList();
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
            return null;
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
        public List<Holder<Ability>> getAbilities(Entity entity) {
            return List.of(slot.getContent(entity).get(WotrDataComponentType.ABILITY).ability());
        }

        public Holder<Ability> getMainAbility(Entity entity) {
            return slot.getContent(entity).get(WotrDataComponentType.ABILITY).ability();
        }
    }
}

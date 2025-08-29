package com.wanderersoftherift.wotr.item.riftkey;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.RiftSpawnerBlock;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.entity.portal.RiftPortalEntranceEntity;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrEntities;
import com.wanderersoftherift.wotr.init.WotrSoundEvents;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Rift key is an item that when used on a rift spawner will generate a rift portal. It also can close an existing rift
 * without being consumed.
 */
public class RiftKey extends Item {
    private static final String NAME = "item." + WanderersOfTheRift.MODID + ".rift_key.themed";

    public RiftKey(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!(blockstate.getBlock() instanceof RiftSpawnerBlock spawnerBlock)) {
            return InteractionResult.PASS;
        } else if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            Optional<PortalSpawnLocation> spawnLocation = spawnerBlock.getSpawnLocation(level, blockpos,
                    context.getClickedFace());
            if (spawnLocation.isPresent()) {
                PortalSpawnLocation loc = spawnLocation.get();
                List<RiftPortalEntranceEntity> existingRifts = getExistingRifts(level, loc.position());
                if (!existingRifts.isEmpty()) {
                    for (RiftPortalEntranceEntity entrance : existingRifts) {
                        entrance.remove(Entity.RemovalReason.DISCARDED);
                    }
                    return InteractionResult.SUCCESS;
                }

                spawnRift(level, loc.position(), loc.direction(), context.getItemInHand());
                context.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.@NotNull TooltipContext context,
            @NotNull List<Component> components,
            @NotNull TooltipFlag flag) {
        if (stack.has(WotrDataComponentType.RiftConfig.GENERATOR_PRESET)) {
            var preset = stack.get(WotrDataComponentType.RiftConfig.GENERATOR_PRESET);
            var presetString = preset.unwrapKey()
                    .map(it -> Component.literal(it.location().toString()).withStyle())
                    .orElse(Component.literal("Custom")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
            components.add(Component
                    .translatable("tooltip." + WanderersOfTheRift.MODID + ".rift_key_generator_preset", presetString)
                    .withColor(ChatFormatting.GRAY.getColor()));
        }
        var edits = stack.get(WotrDataComponentType.RiftConfig.LAYOUT_LAYER_EDIT);
        if (edits != null && !edits.isEmpty()) {
            components.add(Component.literal("Layout edits: ").withColor(ChatFormatting.GRAY.getColor()));
            for (var edit : edits) {
                components.add(Component.literal(" - ")
                        .append(edit.textComponent())
                        .withColor(ChatFormatting.GRAY.getColor()));
            }
        }

        if (stack.has(WotrDataComponentType.RiftConfig.ITEM_RIFT_TIER)) {
            int tier = stack.getOrDefault(WotrDataComponentType.RiftConfig.ITEM_RIFT_TIER, 0);
            components.add(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".rift_key_tier", tier)
                    .withColor(ChatFormatting.GRAY.getColor()));
        }

        Holder<RiftTheme> riftTheme = stack.get(WotrDataComponentType.RiftConfig.RIFT_THEME);
        if (riftTheme != null) {
            ResourceLocation themeId = ResourceLocation.parse(riftTheme.getRegisteredName());
            Component themeName = Component
                    .translatable("rift_theme." + themeId.getNamespace() + "." + themeId.getPath());
            components.add(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".rift_key_theme", themeName)
                    .withColor(ChatFormatting.GRAY.getColor()));
        }
        Holder<ObjectiveType> objective = stack.get(WotrDataComponentType.RiftConfig.RIFT_OBJECTIVE);
        if (objective != null) {
            ResourceLocation objectiveLoc = ResourceLocation.parse(objective.getRegisteredName());
            Component objectiveName = Component
                    .translatable("objective." + objectiveLoc.getNamespace() + "." + objectiveLoc.getPath() + ".name");
            components.add(
                    Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".rift_key_objective", objectiveName)
                            .withColor(ChatFormatting.GRAY.getColor()));
        }
        var seed = stack.get(WotrDataComponentType.RiftConfig.RIFT_SEED);
        if (seed != null) {
            components.add(Component.translatable(WanderersOfTheRift.translationId("tooltip", "rift_key_seed"), seed)
                    .withColor(ChatFormatting.GRAY.getColor()));
        }
    }

    private List<RiftPortalEntranceEntity> getExistingRifts(Level level, Vec3 pos) {
        return level.getEntities(EntityTypeTest.forClass(RiftPortalEntranceEntity.class),
                new AABB(BlockPos.containing(pos)), x -> true);
    }

    private void spawnRift(Level level, Vec3 pos, Direction dir, ItemStack riftKey) {
        RiftPortalEntranceEntity rift = new RiftPortalEntranceEntity(WotrEntities.RIFT_ENTRANCE.get(), level);
        rift.setPos(pos);
        rift.setYRot(dir.toYRot());
        rift.setBillboard(dir.getAxis().isVertical());
        rift.setKeyItem(riftKey);
        level.addFreshEntity(rift);
        rift.playSound(WotrSoundEvents.RIFT_OPEN.value());
    }

}

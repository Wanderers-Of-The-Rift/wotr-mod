package com.wanderersoftherift.wotr.gui.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Extended ContainerLevelAccess that contains the logic for checking if the menu should still be valid based on its
 * source
 */
public interface ValidatingLevelAccess extends ContainerLevelAccess {
    ValidatingLevelAccess NULL = new ValidatingLevelAccess() {
        @Override
        public <T> @NotNull Optional<T> evaluate(@NotNull BiFunction<Level, BlockPos, T> levelPosConsumer) {
            return Optional.empty();
        }
    };

    default boolean isValid(Player player) {
        return evaluate((level, pos) -> player.canInteractWithBlock(pos, 4.0), true);
    }

    /**
     * @param level
     * @param blockPos
     * @param block
     * @return A validating level access tied to a block
     */
    static ValidatingLevelAccess create(final Level level, final BlockPos blockPos, Block block) {
        return new ValidatingLevelAccess() {
            @Override
            public <T> @NotNull Optional<T> evaluate(@NotNull BiFunction<Level, BlockPos, T> levelPosConsumer) {
                return Optional.of(levelPosConsumer.apply(level, blockPos));
            }

            @Override
            public boolean isValid(Player player) {
                return evaluate((l, pos) -> l.getBlockState(pos).is(block) && player.canInteractWithBlock(pos, 4.0),
                        true);
            }
        };
    }

    /**
     * @param entity
     * @return A validating level access tied to an entity
     */
    static ValidatingLevelAccess create(final Entity entity) {
        return new ValidatingLevelAccess() {
            @Override
            public <T> @NotNull Optional<T> evaluate(@NotNull BiFunction<Level, BlockPos, T> levelPosConsumer) {
                return Optional.of(levelPosConsumer.apply(entity.level(), entity.blockPosition()));
            }

            @Override
            public boolean isValid(Player player) {
                return evaluate((l, pos) -> !entity.isRemoved() && player.canInteractWithEntity(entity, 5.0), true);
            }
        };
    }
}

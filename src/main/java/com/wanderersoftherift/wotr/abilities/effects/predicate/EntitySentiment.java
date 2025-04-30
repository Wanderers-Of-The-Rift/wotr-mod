package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.wanderersoftherift.wotr.Config;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * EntitySentiment provides a number of predicates for checking the Friend/Foe status of different entities.
 */
public enum EntitySentiment implements StringRepresentable {

    ANY("any") {
        @Override
        public boolean matches(Entity a, Entity b) {
            return true;
        }
    },
    FRIEND("friend") {
        @Override
        public boolean matches(Entity a, Entity b) {
            if (a instanceof Player playerA) {
                if (b instanceof Player playerB) {
                    return arePlayersAllied(playerA, playerB);
                } else {
                    return isAllyOfPlayer(playerA, b);
                }
            } else if (b instanceof Player playerB) {
                return isAllyOfPlayer(playerB, a);
            }
            return areNPCsAllied(a, b);
        }
    },
    NOT_FRIEND("not_friend") {
        @Override
        public boolean matches(Entity a, Entity b) {
            return !FRIEND.matches(a, b);
        }
    },
    NEUTRAL("neutral") {
        @Override
        public boolean matches(Entity a, Entity b) {
            return !FRIEND.matches(a, b) && !FOE.matches(a, b);
        }
    },
    NOT_FOE("not_foe") {
        @Override
        public boolean matches(Entity a, Entity b) {
            return !FOE.matches(a, b);
        }
    },
    FOE("foe") {
        @Override
        public boolean matches(Entity a, Entity b) {
            if (a instanceof Player playerA) {
                if (b instanceof Player playerB) {
                    return arePlayersEnemies(playerA, playerB);
                } else {
                    return isEnemyOfPlayer(playerA, b);
                }
            } else if (b instanceof Player playerB) {
                return isEnemyOfPlayer(playerB, a);
            }
            return areNPCsEnemies(a, b);
        }
    };

    public static final StringRepresentable.StringRepresentableCodec<EntitySentiment> CODEC = StringRepresentable
            .fromEnum(EntitySentiment::values);

    private final String name;

    EntitySentiment(String name) {
        this.name = name;
    }

    public abstract boolean matches(Entity a, Entity b);

    public @NotNull String getSerializedName() {
        return name;
    }

    public static boolean areNPCsEnemies(Entity a, Entity b) {
        if (a.isAlliedTo(b)) {
            return false;
        }
        if (!Config.allowPvP && isOwnedByPlayer(a) && isOwnedByPlayer(b)) {
            return false;
        }
        // TODO: hone this further.
        return true;

    }

    public static boolean isEnemyOfPlayer(Player playerA, Entity b) {
        if (MobCategory.MONSTER.equals(b.getClassification(false))) {
            return true;
        }
        // Check actively attacking player
        if (b instanceof Mob mob) {
            if (Config.allowPvP) {
                return mob.getTarget() == playerA;
            } else {
                return mob.getTarget() instanceof Player;
            }
        }

        return false;
    }

    public static boolean arePlayersEnemies(@NotNull Player playerA, @NotNull Player playerB) {
        if (Config.allowPvP) {
            return playerA.isAlliedTo(playerB);
        }
        return false;
    }

    public static boolean areNPCsAllied(@NotNull Entity a, @NotNull Entity b) {
        // TODO: There's not a lot of NPCs that are truly allied, so might need to expand
        return a.isAlliedTo(b);
    }

    public static boolean isAllyOfPlayer(@NotNull Player playerA, @NotNull Entity b) {
        if (!Config.allowPvP && isOwnedByPlayer(b)) {
            return true;
        }
        return playerA.isAlliedTo(b);
    }

    public static boolean isOwnedByPlayer(@NotNull Entity b) {
        if (b instanceof OwnableEntity ownableEntity) {
            return ownableEntity.getOwner() instanceof Player;
        }
        return false;
    }

    public static boolean arePlayersAllied(@NotNull Player playerA, @NotNull Player playerB) {
        if (Config.allowPvP) {
            return playerA.isAlliedTo(playerB);
        } else {
            return true;
        }
    }

}

package com.wanderersoftherift.wotr.entity.projectile;

import com.google.common.collect.Lists;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.effects.SimpleProjectileEffect;
import com.wanderersoftherift.wotr.init.ModEntityDataSerializers;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class SimpleEffectProjectile extends Projectile implements GeoEntity {
    private static final double ARROW_BASE_DAMAGE = 2.0;
    private static final int SHAKE_TIME = 7;
    private static final float WATER_INERTIA = 0.6F;
    private static final float INERTIA = 0.999F;
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(SimpleEffectProjectile.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData
            .defineId(SimpleEffectProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData
            .defineId(SimpleEffectProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<SimpleProjectileConfig.SimpleProjectileConfigRenderConfig> RENDER_CONFIG = SynchedEntityData
            .defineId(SimpleEffectProjectile.class, ModEntityDataSerializers.SIMPLE_PROJECTILE_RENDER_CONFIG.get());

    public AbstractArrow.Pickup pickup = AbstractArrow.Pickup.DISALLOWED;
    public int shakeTime;

    protected int inGroundTime;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Nullable private BlockState lastState;
    private int life;
    private double baseDamage = 2.0;
    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
    @Nullable private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable private List<Entity> piercedAndKilledEntities;
    private ItemStack pickupItemStack = this.getDefaultPickupItem();
    @Nullable private ItemStack firedFromWeapon = null;
    private AbstractAbility ability;
    private SimpleProjectileEffect effect;
    private SimpleProjectileConfig config;

    public SimpleEffectProjectile(EntityType<SimpleEffectProjectile> type, Level level) {
        super(type, level);
    }

    public void setAbility(AbstractAbility ability) {
        this.ability = ability;
    }

    public void setEffect(SimpleProjectileEffect effect) {
        this.effect = effect;
    }

    public void setSoundEvent(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
    }

    public SimpleProjectileConfig getConfig() {
        return config;
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN(d0)) {
            d0 = 1.0;
        }

        d0 *= 64.0 * getViewScale();
        return distance < d0 * d0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ID_FLAGS, (byte) 0);
        builder.define(PIERCE_LEVEL, (byte) 0);
        builder.define(IN_GROUND, false);
        builder.define(RENDER_CONFIG, SimpleProjectileConfig.SimpleProjectileConfigRenderConfig.DEFAULT);
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.life = 0;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.setPos(x, y, z);
        this.setRot(yRot, xRot);
    }

    /**
     * Updates the entity motion clientside, called by packets from the server
     */
    @Override
    public void lerpMotion(double x, double y, double z) {
        super.lerpMotion(x, y, z);
        this.life = 0;
        if (this.isInGround() && Mth.lengthSquared(x, y, z) > 0.0) {
            this.setInGround(false);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (!this.firstTick && this.shakeTime <= 0 && IN_GROUND.equals(accessor) && this.isInGround()) {
            this.shakeTime = SHAKE_TIME;
        }
    }

    @Override
    public void tick() {
        boolean flag = !this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (!blockstate.isAir() && flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.setInGround(true);
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            this.shakeTime--;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)
                || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }

        if (this.isInGround() && flag) {
            if (!this.level().isClientSide()) {
                if (this.lastState != blockstate && this.shouldFall()) {
                    this.startFalling();
                } else {
                    this.tickDespawn();
                }
            }

            this.inGroundTime++;
            if (this.isAlive()) {
                this.applyEffectsFromBlocks();
            }
        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            if (this.isInWater()) {
                this.applyInertia(this.getWaterInertia());
                this.addBubbleParticles(vec32);
            }

            float f;
            if (!flag) {
                f = (float) (Mth.atan2(-vec3.x, -vec3.z) * 180.0F / (float) Math.PI);
            } else {
                f = (float) (Mth.atan2(vec3.x, vec3.z) * 180.0F / (float) Math.PI);
            }

            float f1 = (float) (Mth.atan2(vec3.y, vec3.horizontalDistance()) * 180.0F / (float) Math.PI);
            this.setXRot(lerpRotation(this.getXRot(), f1));
            this.setYRot(lerpRotation(this.getYRot(), f));
            if (flag) {
                BlockHitResult blockhitresult = this.level()
                        .clipIncludingBorder(new ClipContext(vec32, vec32.add(vec3), ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE, this));
                this.stepMoveAndHit(blockhitresult);
            } else {
                this.setPos(vec32.add(vec3));
                this.applyEffectsFromBlocks();
            }

            if (!this.isInWater()) {
                this.applyInertia(INERTIA);
            }

            if (flag && !this.isInGround()) {
                this.applyGravity();
            }

            super.tick();
        }
    }

    private void stepMoveAndHit(BlockHitResult hitResult) {
        while (this.isAlive()) {
            Vec3 vec3 = this.position();
            EntityHitResult entityhitresult = this.findHitEntity(vec3, hitResult.getLocation());
            Vec3 vec31 = Objects.requireNonNullElse(entityhitresult, hitResult).getLocation();
            this.setPos(vec31);
            this.applyEffectsFromBlocks(vec3, vec31);
            if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
                this.handlePortal();
            }

            if (entityhitresult == null) {
                if (this.isAlive() && hitResult.getType() != HitResult.Type.MISS) {
                    if (net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitResult)) {
                        break;
                    }
                    this.hitTargetOrDeflectSelf(hitResult);
                    this.hasImpulse = true;
                }
                break;
            } else if (this.isAlive() && !this.noPhysics && entityhitresult.getType() != HitResult.Type.MISS) {
                if (net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, entityhitresult)) {
                    break;
                }
                ProjectileDeflection projectiledeflection = this.hitTargetOrDeflectSelf(entityhitresult);
                this.hasImpulse = true;
                if (this.getPierceLevel() > 0 && projectiledeflection == ProjectileDeflection.NONE) {
                    continue;
                }
                break;
            }
        }
    }

    private void applyInertia(float inertia) {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.scale((double) inertia));
    }

    private void addBubbleParticles(Vec3 pos) {
        Vec3 vec3 = this.getDeltaMovement();

        for (int i = 0; i < 4; i++) {
            float f = 0.25F;
            this.level()
                    .addParticle(ParticleTypes.BUBBLE, pos.x - vec3.x * 0.25, pos.y - vec3.y * 0.25,
                            pos.z - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    private boolean shouldFall() {
        return this.isInGround() && this.level().noCollision(new AABB(this.position(), this.position()).inflate(0.06));
    }

    private void startFalling() {
        this.setInGround(false);
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply((double) (this.random.nextFloat() * 0.2F),
                (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
        this.life = 0;
    }

    protected boolean isInGround() {
        return this.entityData.get(IN_GROUND);
    }

    protected void setInGround(boolean inGround) {
        this.entityData.set(IN_GROUND, inGround);
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, pos);
        if (type != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    protected void tickDespawn() {
        this.life++;
        if (this.life >= config.groundPersistTicks()) {
            this.discard();
        }
    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }

        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }
    }

    @Override
    protected void onItemBreak(Item item) {
        this.firedFromWeapon = null;
    }

    @Override
    public void onInsideBubbleColumn(boolean downwards) {
        if (!this.isInGround()) {
            super.onInsideBubbleColumn(downwards);
        }
    }

    @Override
    public void push(double x, double y, double z) {
        if (!this.isInGround()) {
            super.push(x, y, z);
        }
    }

    /**
     * Called when the arrow hits an entity
     */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        float f = (float) this.getDeltaMovement().length();
        double d0 = this.baseDamage;
        Entity owner = this.getOwner();
        DamageSource damagesource = this.damageSources()
                .source(DamageTypes.GENERIC, this, (Entity) (owner != null ? owner : this));
        if (this.getWeaponItem() != null && this.level() instanceof ServerLevel serverlevel) {
            d0 = (double) EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource,
                    (float) d0);
        }

        int j = Mth.ceil(Mth.clamp((double) f * d0, 0.0, 2.147483647E9));
        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                this.discard();
                return;
            }

            this.piercingIgnoreEntityIds.add(entity.getId());
        }

        if (owner instanceof LivingEntity livingentity1) {
            livingentity1.setLastHurtMob(entity);
        }

        if (this.level() instanceof ServerLevel serverLevel && effect != null) {
            LivingEntity caster = null;
            if (owner instanceof LivingEntity livingOwner) {
                caster = livingOwner;
            }
            // TODO: capture and carry across ability item
            effect.applyDelayed(serverLevel, entity, List.of(entity.blockPosition()),
                    new AbilityContext(caster, ItemStack.EMPTY));
        }

        if (this.getPierceLevel() <= 0) {
            this.discard();
        }
    }

    private void doSpawnAtLocation(ServerLevel serverlevel2) {
        if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
            this.spawnAtLocation(serverlevel2, this.getPickupItem(), 0.1F);
        }
    }

    private void doDeflect(Entity entity) {
        this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), false);
        this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
    }

    protected void doKnockback(LivingEntity entity, DamageSource damageSource) {
        double d0 = (double) (this.firedFromWeapon != null && this.level() instanceof ServerLevel serverlevel
                ? EnchantmentHelper.modifyKnockback(serverlevel, this.firedFromWeapon, entity, damageSource, 0.0F)
                : 0.0F);
        if (d0 > 0.0) {
            double d1 = Math.max(0.0, 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(d0 * 0.6 * d1);
            if (vec3.lengthSqr() > 0.0) {
                entity.push(vec3.x, 0.1, vec3.z);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.lastState = this.level().getBlockState(result.getBlockPos());
        super.onHitBlock(result);
        ItemStack itemstack = this.getWeaponItem();
        if (this.level() instanceof ServerLevel serverLevel) {
            if (effect != null) {
                // TODO: capture and carry across ability item
                effect.applyDelayed(serverLevel, null, List.of(result.getBlockPos()),
                        new AbilityContext((LivingEntity) this.getOwner(), ItemStack.EMPTY));
            }
        }

        Vec3 vec31 = this.getDeltaMovement();
        Vec3 vec32 = new Vec3(Math.signum(vec31.x), Math.signum(vec31.y), Math.signum(vec31.z));
        Vec3 vec3 = vec32.scale(0.05F);
        this.setPos(this.position().subtract(vec3));
        this.setDeltaMovement(Vec3.ZERO);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.setInGround(true);
        this.shakeTime = SHAKE_TIME;
        this.setPierceLevel((byte) 0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.resetPiercedEntities();
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.firedFromWeapon;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected final SoundEvent getHitGroundSoundEvent() {
        return this.soundEvent;
    }

    protected void doPostHurtEffects(LivingEntity target) {
    }

    /**
     * Gets the EntityRayTraceResult representing the entity hit
     */
    @Nullable protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, startVec, endVec,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof Player && this.getOwner() instanceof Player player
                && !player.canHarmPlayer((Player) entity)) {
            return false;
        } else {
            return super.canHitEntity(entity)
                    && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("life", (short) this.life);
        if (this.lastState != null) {
            compound.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
        }

        compound.putByte("shake", (byte) this.shakeTime);
        compound.putBoolean("inGround", this.isInGround());
        compound.putByte("pickup", (byte) this.pickup.ordinal());
        compound.putDouble("damage", this.baseDamage);
        compound.putByte("PierceLevel", this.getPierceLevel());
        compound.putString("SoundEvent", BuiltInRegistries.SOUND_EVENT.getKey(this.soundEvent).toString());
        if (!this.pickupItemStack.isEmpty()) {
            compound.put("item", this.pickupItemStack.save(this.registryAccess()));
        }
        if (this.firedFromWeapon != null) {
            compound.put("weapon", this.firedFromWeapon.save(this.registryAccess(), new CompoundTag()));
        }
        if (this.config != null) {
            compound.put("config", SimpleProjectileConfig.CODEC.encodeStart(NbtOps.INSTANCE, this.config).getOrThrow());
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.life = compound.getShort("life");
        if (compound.contains("inBlockState", 10)) {
            this.lastState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),
                    compound.getCompound("inBlockState"));
        }

        this.shakeTime = compound.getByte("shake") & 255;
        this.setInGround(compound.getBoolean("inGround"));
        if (compound.contains("damage", 99)) {
            this.baseDamage = compound.getDouble("damage");
        }

        this.pickup = AbstractArrow.Pickup.byOrdinal(compound.getByte("pickup"));
        this.setPierceLevel(compound.getByte("PierceLevel"));
        if (compound.contains("SoundEvent", 8)) {
            this.soundEvent = BuiltInRegistries.SOUND_EVENT
                    .getOptional(ResourceLocation.parse(compound.getString("SoundEvent")))
                    .orElse(this.getDefaultHitGroundSoundEvent());
        }

        if (compound.contains("item", 10)) {
            this.setPickupItemStack(ItemStack.parse(this.registryAccess(), compound.getCompound("item"))
                    .orElse(this.getDefaultPickupItem()));
        } else {
            this.setPickupItemStack(this.getDefaultPickupItem());
        }

        if (compound.contains("weapon", 10)) {
            this.firedFromWeapon = ItemStack.parse(this.registryAccess(), compound.getCompound("weapon")).orElse(null);
        } else {
            this.firedFromWeapon = null;
        }

        if (compound.contains("config", 10)) {
            this.config = SimpleProjectileConfig.CODEC.parse(NbtOps.INSTANCE, compound.get("config"))
                    .resultOrPartial(value -> WanderersOfTheRift.LOGGER.warn("Invalid projectile config: {}", value))
                    .orElse(SimpleProjectileConfig.DEFAULT);
        }
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);

        this.pickup = switch (entity) {
            case Player player when this.pickup == AbstractArrow.Pickup.DISALLOWED -> AbstractArrow.Pickup.ALLOWED;
            case OminousItemSpawner ominousitemspawner -> AbstractArrow.Pickup.DISALLOWED;
            case null, default -> this.pickup;
        };
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    @Override
    public void playerTouch(Player entity) {
        if (!this.level().isClientSide && (this.isInGround() || this.isNoPhysics()) && this.shakeTime <= 0) {
            if (this.tryPickup(entity)) {
                entity.take(this, 1);
                this.discard();
            }
        }
    }

    protected boolean tryPickup(Player player) {
        return switch (this.pickup) {
            case DISALLOWED -> false;
            case ALLOWED -> player.getInventory().add(this.getPickupItem());
            case CREATIVE_ONLY -> player.hasInfiniteMaterials();
        };
    }

    protected ItemStack getPickupItem() {
        return this.pickupItemStack.copy();
    }

    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public ItemStack getPickupItemStackOrigin() {
        return this.pickupItemStack;
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    @Override
    public boolean isAttackable() {
        return this.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
    }

    private void setPierceLevel(byte pierceLevel) {
        this.entityData.set(PIERCE_LEVEL, pierceLevel);
    }

    public void setRenderConfig(SimpleProjectileConfig.SimpleProjectileConfigRenderConfig renderConfig) {
        this.entityData.set(RENDER_CONFIG, renderConfig);
    }

    private void setFlag(int id, boolean value) {
        byte b0 = this.entityData.get(ID_FLAGS);
        if (value) {
            this.entityData.set(ID_FLAGS, (byte) (b0 | id));
        } else {
            this.entityData.set(ID_FLAGS, (byte) (b0 & ~id));
        }
    }

    protected void setPickupItemStack(ItemStack pickupItemStack) {
        if (!pickupItemStack.isEmpty()) {
            this.pickupItemStack = pickupItemStack;
        } else {
            this.pickupItemStack = this.getDefaultPickupItem();
        }
    }

    public byte getPierceLevel() {
        return this.entityData.get(PIERCE_LEVEL);
    }

    public SimpleProjectileConfig.SimpleProjectileConfigRenderConfig getRenderConfig() {
        return this.entityData.get(RENDER_CONFIG);
    }

    public void setBaseDamageFromMob(float velocity) {
        this.setBaseDamage((double) (velocity * 2.0F)
                + this.random.triangle((double) this.level().getDifficulty().getId() * 0.11, 0.574_25));
    }

    protected float getWaterInertia() {
        return 0.6F;
    }

    public boolean isNoPhysics() {
        if (!this.level().isClientSide) {
            return this.noPhysics;
        } else {
            return (this.entityData.get(ID_FLAGS) & 2) != 0;
        }
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && !this.isInGround();
    }

    @Override
    public SlotAccess getSlot(int slot) {
        if (slot == 0) {
            return SlotAccess.of(this::getPickupItemStackOrigin, this::setPickupItemStack);
        } else {
            return super.getSlot(slot);
        }
    }

    @Override
    protected boolean shouldBounceOnWorldBorder() {
        return true;
    }

    public void configure(SimpleProjectileConfig config) {
        this.config = config;
        this.setRenderConfig(config.renderConfig());
        setNoGravity(!config.gravityAffected());
    }

    public static enum Pickup {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;

        public static SimpleEffectProjectile.Pickup byOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal > values().length) {
                ordinal = 0;
            }

            return values()[ordinal];
        }
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "loop", 5,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("loop"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

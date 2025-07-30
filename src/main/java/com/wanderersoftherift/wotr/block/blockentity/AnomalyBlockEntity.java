package com.wanderersoftherift.wotr.block.blockentity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.tools.AnomalyNeedle;
import com.wanderersoftherift.wotr.rift.anomaly.RiftAnomalyTask;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class AnomalyBlockEntity extends BlockEntity {
    private boolean isClosed = false; // Flag to indicate if the anomaly is shut down
    private final List<BlockState> displayBlocks = new ArrayList<>();
    private String themeName = "default"; // Default theme name, can be changed later
    private RiftAnomalyTask anomalyTask;
    private float[] particleColor = new float[] { 1.0f, 1.0f, 1.0f }; // Default particle color (white)
    private final Set<UUID> spawnedMobUUIDs = new HashSet<>();
    private boolean hasSpawned = false; // Flag to track if enemies have been spawned

    public AnomalyBlockEntity(BlockPos pos, BlockState blockState) {
        super(WotrBlockEntities.ANOMALY_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, AnomalyBlockEntity blockEntity) {
        // Spawn particles around the anomaly, color depends on the anomaly task type
        if (!blockEntity.isClosed && level.getGameTime() % 4 == 0) { // Every 4 ticks (5 times per second)
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY();
            double centerZ = pos.getZ() + 0.5;

            // Create swirling particles around the anomaly
            for (int i = 0; i < 3; i++) {
                double angle = (level.getGameTime() + i * 120) * 0.05; // Rotating angle
                double radius = 0.4 + Math.sin(level.getGameTime() * 0.02 + i) * 0.07; // Varying radius

                double x = centerX + Math.cos(angle) * radius;
                double z = centerZ + Math.sin(angle) * radius;
                double y = centerY + 0.7 + Math.sin(level.getGameTime() * 0.03 + i) * 0.1;

                int rgb = ((int) (255 * blockEntity.particleColor[0])) << 16
                        | ((int) (255 * blockEntity.particleColor[1])) << 8
                        | (int) (255 * blockEntity.particleColor[2]);
                level.sendParticles(new DustParticleOptions(rgb, 1.0f), x, y, z, 1, 0, 0, 0, 0.05f);
            }
        }
    }

    public InteractionResult onAnomalyClick(Player player, InteractionHand hand) {
        if (!level.isClientSide && !isClosed) {
            // All anomaly task types
            switch (anomalyTask.type()) {
                case "needle" -> handleNeedleInteraction(player, hand); // Blue particles
                case "bundle" -> handleBundleInteraction(player, hand); // Green particles
                case "battle" -> handleBattleInteraction(player); // Red particles
                default -> System.err.println("Unknown anomaly task: " + anomalyTask.type());
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void handleNeedleInteraction(Player player, InteractionHand hand) {
        // Needle logic
        if (player.getItemInHand(hand).is(WotrItems.ANOMALY_NEEDLE.get())) {
            ItemStack heldItem = player.getItemInHand(hand);
            int charge = AnomalyNeedle.getCharge(heldItem);
            if (charge > 0) {
                // Decrease charge on use
                AnomalyNeedle.setCharge(heldItem, charge - 1);
                closeAnomaly(player);
                giveEffect(player);
            }
        }
    }

    private void handleBundleInteraction(Player player, InteractionHand hand) {
        // Bundle logic
        // Check if player has a bundle in hand, check contents, and remove blocks
        if (player.getItemInHand(hand).getItem() instanceof BundleItem) {
            ItemStack bundle = player.getItemInHand(hand);
            BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
            if (contents != null) {
                Map<BlockState, Integer> toRemoveCount = new HashMap<>();
                for (BlockState state : displayBlocks) {
                    toRemoveCount.put(state, toRemoveCount.getOrDefault(state, 0) + 1);
                }
                List<ItemStack> updated = new ArrayList<>();
                for (ItemStack stack : contents.items()) {
                    BlockState stackState = Block.byItem(stack.getItem()).defaultBlockState();
                    // Remove as many blocks as needed from the displayBlocks
                    Integer countToRemove = toRemoveCount.get(stackState);
                    if (countToRemove != null && countToRemove > 0) {
                        int stackCount = stack.getCount();
                        int toRemove = Math.min(countToRemove, stackCount);
                        // Dont remove more than available
                        if (stackCount > toRemove) {
                            ItemStack newStack = stack.copy();
                            newStack.setCount(stackCount - toRemove);
                            updated.add(newStack);
                        }
                        toRemoveCount.put(stackState, countToRemove - toRemove);
                        for (int i = 0; i < toRemove; i++) {
                            displayBlocks.remove(stackState);
                        }

                    } else {
                        updated.add(stack.copy());
                    }

                    // Update rendering of displayBlocks
                    setChanged();
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.getChunkSource().blockChanged(getBlockPos());
                    }

                }
                // Close when all blocks are handed in
                if (displayBlocks.isEmpty()) {
                    closeAnomaly(player);
                    giveEffect(player);
                }
                bundle.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(updated));
            }
        }
    }

    private void handleBattleInteraction(Player player) {
        // Battle logic
        // Only continue if the player has not spawned enemies yet
        if (hasSpawned) {
            return;
        }
        hasSpawned = true; // Prevent multiple spawns
        giveEffect(player);

        // Get mob data from themed Trial Spawner JSON
        String theme = themeName != null && !themeName.isEmpty() ? themeName : "rift";
        ResourceLocation jsonLocation = ResourceLocation.parse("wotr:trial_spawner/" + theme + ".json");
        ResourceLocation fallbackLocation = ResourceLocation.parse("wotr:trial_spawner/rift.json");
        float simultaneousSpawns = 3.0f; // Default fallback value
        JsonArray defaultSpawnPotentials = new JsonArray();
        {
            JsonObject zombie = new JsonObject();
            zombie.addProperty("weight", 5);
            JsonObject zombieData = new JsonObject();
            JsonObject zombieEntity = new JsonObject();
            zombieEntity.addProperty("id", "minecraft:zombie");
            zombieData.add("entity", zombieEntity);
            zombie.add("data", zombieData);
            defaultSpawnPotentials.add(zombie);
        }
        JsonArray spawnPotentials = defaultSpawnPotentials; // Default array

        // Try to load the JSON from the server's resource manager. Spawn 1 round of mobs
        if (level instanceof ServerLevel serverLevel) {
            try {
                var resourceManager = serverLevel.getServer().getResourceManager();
                var resource = resourceManager.getResource(jsonLocation)
                        .orElseGet(() -> resourceManager.getResource(fallbackLocation).orElse(null)
                        );
                if (resource != null) {
                    try (var reader = resource.openAsReader()) {
                        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                        simultaneousSpawns = root.has("simultaneous_spawns")
                                ? root.get("simultaneous_spawns").getAsFloat()
                                : 3.0f;
                        spawnPotentials = root.getAsJsonArray("spawn_potentials");
                    }
                }
            } catch (Exception e) {
            }

            List<Pair<Float, JsonObject>> weightedEntities = new ArrayList<>();
            for (JsonElement elem : spawnPotentials) {
                JsonObject obj = elem.getAsJsonObject();
                float weight = obj.get("weight").getAsFloat();
                JsonObject entityObj = obj.getAsJsonObject("data").getAsJsonObject("entity");
                weightedEntities.add(new Pair<>(weight, entityObj));
            }
            FastWeightedList<JsonObject> entityList = FastWeightedList.of(weightedEntities.toArray(new Pair[0]));

            int count = Math.round(simultaneousSpawns * anomalyTask.battleScale()); // Multiple 1 round by Scale
            for (int i = 0; i < count; i++) {
                JsonObject entityObj = entityList.random(serverLevel.random);
                String entityId = entityObj.get("id").getAsString();
                ResourceLocation entityRes = ResourceLocation.parse(entityId);
                Optional<Holder.Reference<EntityType<?>>> type = BuiltInRegistries.ENTITY_TYPE.get(entityRes);
                if (type.isPresent()) {
                    EntityType<?> entityType = type.get().value();
                    // Random offset similar to spawner logic
                    double offsetX = (level.random.nextDouble() - level.random.nextDouble()) * 2.0;
                    double offsetZ = (level.random.nextDouble() - level.random.nextDouble()) * 2.0;
                    double offsetY = level.random.nextInt(3) - 1; // -1, 0, or 1

                    double spawnX = getBlockPos().getX() + 0.5 + offsetX;
                    double spawnY = getBlockPos().getY() + offsetY;
                    double spawnZ = getBlockPos().getZ() + 0.5 + offsetZ;

                    // Load Json data
                    CompoundTag nbt = null;
                    if (entityObj.size() > 1) {
                        JsonObject nbtObj = new JsonObject();
                        for (Map.Entry<String, JsonElement> entry : entityObj.entrySet()) {
                            if (!"id".equals(entry.getKey())) {
                                nbtObj.add(entry.getKey(), entry.getValue());
                            }
                        }
                        if (nbtObj.size() > 0) {
                            String nbtString = nbtObj.toString();
                            try {
                                nbt = TagParser.parseTag(nbtString);
                            } catch (Exception e) {
                            }
                        }
                    }

                    Entity entity;
                    // Spawn the entity, with or without NBT data
                    if (nbt != null) {
                        nbt.putString("id", entityId);
                        entity = EntityType.loadEntityRecursive(nbt, serverLevel, EntitySpawnReason.TRIAL_SPAWNER,
                                (e) -> {
                                    e.moveTo(spawnX, spawnY, spawnZ);
                                    return e;
                                }
                        );
                        if (entity != null) {
                            CompoundTag tag = entity.getPersistentData();
                            tag.put("AnomalyBlockPos", NbtUtils.writeBlockPos(getBlockPos()));
                            serverLevel.addFreshEntity(entity);
                        }
                    } else {
                        entity = entityType.spawn(serverLevel, null, null,
                                new BlockPos((int) spawnX, (int) spawnY, (int) spawnZ), EntitySpawnReason.EVENT, true,
                                false);
                        if (entity != null) {
                            CompoundTag tag = entity.getPersistentData();
                            tag.put("AnomalyBlockPos", NbtUtils.writeBlockPos(getBlockPos()));
                        }
                    }

                    // Track spawned mobs by UUID, so we can check if they are killed later
                    if (entity instanceof Mob mob) {
                        spawnedMobUUIDs.add(mob.getUUID());
                        mob.setPersistenceRequired();
                    }
                }
            }
        }
    }

    public void onMobKilled(UUID mobUUID, Player player) {
        if (spawnedMobUUIDs.remove(mobUUID) && spawnedMobUUIDs.isEmpty()) {
            closeAnomaly(player);
        }
    }

    public float getScale() {
        return isClosed ? 0.1f : 1.0f; // 10% scale when closed, 100% otherwise
    }

    public void setTheme(ServerLevel level) {
        if (anomalyTask.selectableThemes().isEmpty()) {
            // Empty or missing selectable themes, use all themes
            Registry<RiftTheme> registry = level.registryAccess().lookupOrThrow(WotrRegistries.Keys.RIFT_THEMES);
            List<Map.Entry<ResourceKey<RiftTheme>, RiftTheme>> themeList = new ArrayList<>(registry.entrySet());
            if (!themeList.isEmpty()) {
                Map.Entry<ResourceKey<RiftTheme>, RiftTheme> randomTheme = themeList
                        .get(new Random().nextInt(themeList.size()));
                String themeId = randomTheme.getKey().location().getPath();
                this.themeName = themeId;
            } else {
                themeName = "none"; // Fallback if no themes are available
            }
        } else {
            // Pick from selectable themes
            Optional<List<String>> selectableThemes = anomalyTask.selectableThemes();
            this.themeName = selectableThemes.filter(list -> !list.isEmpty())
                    .map(list -> list.get(new Random().nextInt(list.size())))
                    .orElse("none");
        }
    }

    public void setAnomalyTask(ServerLevel level) {
        // Select a random anomaly task from the JSONs
        // Load data before setting theme, to get optional selectable themes list
        Registry<RiftAnomalyTask> registry = level.registryAccess().lookupOrThrow(WotrRegistries.Keys.ANOMALY_TASKS);
        FastWeightedList<RiftAnomalyTask> weightedList = RiftAnomalyTask.buildWeightedList(registry.stream().toList());
        RiftAnomalyTask randomTask = weightedList.random(level.random);
        if (randomTask != null) {
            this.anomalyTask = randomTask;
            // Set the theme, required tasks
            setTheme(level);
            displayBlocks.clear();
            if ("bundle".equals(randomTask.type())) {
                // Add bundle requirements, for rendering
                setBundleReqs(level, themeName);
                particleColor = new float[] { 0.0f, 1.0f, 0.0f };
            } else if ("battle".equals(randomTask.type())) {
                particleColor = new float[] { 1.0f, 0.0f, 0.0f };
            } else if ("needle".equals(randomTask.type())) {
                particleColor = new float[] { 0.0f, 0.0f, 1.0f };
            }
        } else {
            System.err.println("No anomaly tasks available.");
            displayBlocks.clear();
        }
    }

    public List<BlockState> getDisplayBlocks() {
        return displayBlocks;
    }

    private void closeAnomaly(Player player) {
        isClosed = true; // Close the anomaly
        setChanged(); // Mark the block entity as changed to update the client
        // Force client synchronization
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(getBlockPos());
        }
        // Give rewards
        if (player instanceof ServerPlayer serverPlayer) {
            giveAnomalyLoot(serverPlayer);
        }

    }

    private void giveEffect(Player player) {
        if (anomalyTask.effect().isPresent()) {
            var eff = anomalyTask.effect().get();
            var mobEffectHolder = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(eff.id()));
            if (mobEffectHolder.isPresent()) {
                var effectInstance = new MobEffectInstance(mobEffectHolder.get(), eff.duration(), eff.amplifier());
                player.addEffect(effectInstance);
            }
        }
    }

    public void setBundleReqs(ServerLevel level, String themeName) {
        // Set the bundle requirements based on the theme's Key Recipe. Cave and Processor themes have no/unavailable
        // essences. Default is Mesa.
        String theme = themeName != null && !themeName.isEmpty() && !"cave".equals(themeName)
                && !"processor".equals(themeName) ? themeName : "mesa";
        ResourceLocation jsonLocation = ResourceLocation.parse("wotr:recipe/rift_theme_" + theme + ".json");
        ResourceLocation fallbackLocation = ResourceLocation.parse("wotr:recipe/rift_theme_mesa.json");
        JsonArray defaultEssenceReqs = new JsonArray();
        {
            JsonObject mesa = new JsonObject();
            mesa.addProperty("essence_type", "wotr:earth");
            mesa.addProperty("min_percent", 50.0);
            defaultEssenceReqs.add(mesa);
        }
        JsonArray essenceReqs = defaultEssenceReqs; // Default array

        if (level instanceof ServerLevel serverLevel) {
            try {
                var resourceManager = serverLevel.getServer().getResourceManager();
                var resource = resourceManager.getResource(jsonLocation)
                        .orElseGet(() -> resourceManager.getResource(fallbackLocation).orElse(null)
                        );
                if (resource != null) {
                    try (var reader = resource.openAsReader()) {
                        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                        essenceReqs = root.getAsJsonArray("essence_reqs");
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to load essence requirements JSON: " + e.getMessage());
            }

            // Convert essence types to wool colors, as we dont have essence items yet
            essenceToWool(essenceReqs);

            // Create a weighted list of essence requirements
            List<Pair<Float, JsonObject>> weightedEssence = new ArrayList<>();
            for (JsonElement elem : essenceReqs) {
                JsonObject obj = elem.getAsJsonObject();
                float weight = obj.get("min_percent").getAsFloat();
                weightedEssence.add(new Pair<>(weight, obj));
            }
            FastWeightedList<JsonObject> essenceList = FastWeightedList.of(weightedEssence.toArray(new Pair[0]));

            // Generate display blocks based on the essence requirements and min/max bundle values
            int min = anomalyTask.bundleMin();
            int max = anomalyTask.bundleMax();
            int count = min + level.random.nextInt(max - min + 1);
            for (int i = 0; i < count; i++) {
                JsonObject pickedEssence = essenceList.random(serverLevel.random);
                String essenceId = pickedEssence.get("essence_type").getAsString();
                ResourceLocation essenceRes = ResourceLocation.parse(essenceId);
                Block block = BuiltInRegistries.BLOCK.get(essenceRes).map(Holder.Reference::value).orElse(Blocks.AIR);
                if (block != Blocks.AIR) {
                    displayBlocks.add(block.defaultBlockState());
                }
            }
            serverLevel.getChunkSource().blockChanged(getBlockPos());
        }
    }

    public static void essenceToWool(JsonArray essenceReqs) {
        // Remove once essence items are implemented
        // This method maps essence types to wool colors for testing purposes
        Map<String, String> essenceToWool = new HashMap<>();
        essenceToWool.put("wotr:earth", "minecraft:brown_wool");
        essenceToWool.put("wotr:nether", "minecraft:red_wool");
        essenceToWool.put("wotr:water", "minecraft:blue_wool");
        essenceToWool.put("wotr:life", "minecraft:green_wool");
        essenceToWool.put("wotr:death", "minecraft:black_wool");
        essenceToWool.put("wotr:light", "minecraft:yellow_wool");
        essenceToWool.put("wotr:mushroom", "minecraft:pink_wool");
        essenceToWool.put("wotr:plant", "minecraft:lime_wool");
        essenceToWool.put("wotr:honey", "minecraft:orange_wool");
        essenceToWool.put("wotr:fabric", "minecraft:purple_wool");

        for (JsonElement elem : essenceReqs) {
            if (elem.isJsonObject()) {
                JsonObject obj = elem.getAsJsonObject();
                if (obj.has("essence_type")) {
                    String oldType = obj.get("essence_type").getAsString();
                    if (essenceToWool.containsKey(oldType)) {
                        obj.addProperty("essence_type", essenceToWool.get(oldType));
                    }
                }
            }
        }
    }

    public void giveAnomalyLoot(ServerPlayer player) {
        // Gives scaled version of rift-completion loot to the player
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        ResourceKey<LootTable> anomalyLootTable = ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.parse("wotr:rift_objective/success"));
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(anomalyLootTable);
        LootParams lootParams = new LootParams.Builder(serverLevel).withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.EMPTY);

        // Get loot and scale down
        var items = lootTable.getRandomItems(lootParams);
        for (ItemStack item : items) {
            int originalCount = item.getCount();
            int newCount = Math.max(1, Math.round(originalCount * anomalyTask.lootModifier()));
            if (newCount > 0) {
                ItemStack scaled = item.copy();
                scaled.setCount(newCount);
                player.getInventory().placeItemBackInInventory(scaled);
            }
        }
    }

    @Override
    public void onLoad() {
        // Set the anomaly data when the block entity is loaded into the rift
        if (level instanceof ServerLevel serverLevel && !isClosed && anomalyTask == null) {
            setAnomalyTask(serverLevel);
            setChanged();
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        isClosed = tag.getBoolean("isClosed");
        hasSpawned = tag.getBoolean("hasSpawned");
        displayBlocks.clear();
        ListTag blockList = tag.getList("DisplayBlocks", Tag.TAG_COMPOUND);
        HolderGetter<Block> blockGetter = registries.lookupOrThrow(Registries.BLOCK);
        for (Tag t : blockList) {
            CompoundTag blockTag = (CompoundTag) t;
            BlockState state = NbtUtils.readBlockState(blockGetter, blockTag);
            displayBlocks.add(state);
        }
        themeName = tag.getString("themeName");
        if (tag.contains("anomalyTaskData", Tag.TAG_COMPOUND)) {
            anomalyTask = RiftAnomalyTask.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("anomalyTaskData"))
                    .result()
                    .orElse(null);
        } else {
            anomalyTask = null;
        }
        if (tag.contains("particleColor", Tag.TAG_LIST)) {
            ListTag colorList = tag.getList("particleColor", Tag.TAG_FLOAT);
            if (colorList.size() == 3) {
                for (int i = 0; i < 3; i++) {
                    particleColor[i] = colorList.getFloat(i);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("isClosed", isClosed);
        tag.putBoolean("hasSpawned", hasSpawned);
        ListTag blockList = new ListTag();
        for (BlockState state : displayBlocks) {
            blockList.add(NbtUtils.writeBlockState(state));
        }
        tag.put("DisplayBlocks", blockList);
        tag.putString("themeName", themeName == null ? "" : themeName);
        if (anomalyTask != null) {
            CompoundTag anomalyTag = RiftAnomalyTask.CODEC.encodeStart(NbtOps.INSTANCE, anomalyTask)
                    .result()
                    .filter(t -> t instanceof CompoundTag)
                    .map(t -> (CompoundTag) t)
                    .orElseThrow(() -> new IllegalStateException("Failed to encode anomalyTask"));
            tag.put("anomalyTaskData", anomalyTag);
        }
        ListTag colorList = new ListTag();
        for (float f : particleColor) {
            colorList.add(net.minecraft.nbt.FloatTag.valueOf(f));
        }
        tag.put("particleColor", colorList);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(
            Connection net,
            ClientboundBlockEntityDataPacket pkt,
            HolderLookup.Provider lookupProvider) {
        handleUpdateTag(pkt.getTag(), lookupProvider);
    }
}
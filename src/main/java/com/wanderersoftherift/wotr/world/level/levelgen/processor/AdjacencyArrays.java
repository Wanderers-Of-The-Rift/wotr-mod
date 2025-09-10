package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A data-holding class used exclusively by {@link RiftAdjacencyProcessorEvaluator}
 */
class AdjacencyArrays {
    public final SecondaryArrays secondaryArrays = new SecondaryArrays();
    public final SecondaryArrays.TertiaryArrays tertiaryArrays = secondaryArrays.tertiaryArrays;
    final BlockState[][][] preloaded;
    final long[][] saveMask;
    final long[][] mergedFlags;

    AdjacencyArrays(Vec3i pieceSize) {
        preloaded = new BlockState[4][pieceSize.getZ() + 2][pieceSize.getX() + 2];
        saveMask = new long[4][pieceSize.getZ() + 2];
        mergedFlags = new long[2][pieceSize.getZ() + 2];
    }

    public class SecondaryArrays {
        public final TertiaryArrays tertiaryArrays = new TertiaryArrays();
        private BlockState[][] preloadedSecondary;
        private long[] saveMaskSecondary;
        private long[] mergedFlagsSecondary;

        public void load(int y) {
            preloadedSecondary = preloaded[y & 3];
            saveMaskSecondary = saveMask[y & 3];
            mergedFlagsSecondary = mergedFlags[y & 1];
        }

        public class TertiaryArrays {

            private long mergedFlagsTertiary;
            private BlockState[] preloadedTertiaryDown;
            private BlockState[] preloadedTertiaryUp;
            private BlockState[] preloadedTertiaryNorth;
            private BlockState[] preloadedTertiarySouth;
            private BlockState[] preloadedTertiaryCenter;
            private long saveMaskTertiaryDown;
            private long saveMaskTertiaryUp;
            private long saveMaskTertiaryNorth;
            private long saveMaskTertiarySouth;
            private long saveMaskTertiaryCenter;

            public void load(int y, int z) {
                mergedFlagsTertiary = mergedFlagsSecondary[z + 1];
                preloadedTertiaryDown = preloaded[(y - 1) & 3][z + 1];
                preloadedTertiaryUp = preloaded[(y + 1) & 3][z + 1];
                preloadedTertiaryNorth = preloadedSecondary[z];
                preloadedTertiarySouth = preloadedSecondary[z + 2];
                preloadedTertiaryCenter = preloadedSecondary[z + 1];
                saveMaskTertiaryDown = saveMask[(y - 1) & 3][z + 1];
                saveMaskTertiaryUp = saveMask[(y + 1) & 3][z + 1];
                saveMaskTertiaryNorth = saveMaskSecondary[z];
                saveMaskTertiarySouth = saveMaskSecondary[z + 2];
                saveMaskTertiaryCenter = saveMaskSecondary[z + 1];
            }

            public boolean testCenter(int x) {
                return ((mergedFlagsTertiary >> (x + 1)) & 1) == 0;
            }

            public void getCrossArray(BlockState[] directionBlocksArray, int x) {
                directionBlocksArray[0] = preloadedTertiaryDown[x + 1];
                directionBlocksArray[1] = preloadedTertiaryUp[x + 1];
                directionBlocksArray[2] = preloadedTertiaryNorth[x + 1];
                directionBlocksArray[3] = preloadedTertiarySouth[x + 1];
                directionBlocksArray[4] = preloadedTertiaryCenter[x];
                directionBlocksArray[5] = preloadedTertiaryCenter[x + 2];
                directionBlocksArray[6] = preloadedTertiaryCenter[x + 1];
            }

            public void saveCrossArray(BlockState[] directionBlocksArray, int x, int modifyMask) {

                if (modifyMask != 0) {
                    if ((modifyMask & 0b111) != 0) {
                        if ((modifyMask & 0b1) != 0) {
                            preloadedTertiaryDown[x + 1] = directionBlocksArray[0];
                            saveMaskTertiaryDown |= 1L << (x + 1);
                        }
                        if ((modifyMask & 0b10) != 0) {
                            preloadedTertiaryUp[x + 1] = directionBlocksArray[1];
                            saveMaskTertiaryUp |= 1L << (x + 1);
                        }
                        if ((modifyMask & 0b100) != 0) {
                            preloadedTertiaryNorth[x + 1] = directionBlocksArray[2];
                            saveMaskTertiaryNorth |= 1L << (x + 1);
                        }
                    }
                    if ((modifyMask & 0b111000) != 0) {
                        if ((modifyMask & 0b1000) != 0) {
                            preloadedTertiarySouth[x + 1] = directionBlocksArray[3];
                            saveMaskTertiarySouth |= 1L << (x + 1);
                        }
                        if ((modifyMask & 0b10000) != 0) {
                            preloadedTertiaryCenter[x] = directionBlocksArray[4];
                            saveMaskTertiaryCenter |= 1L << x;
                        }
                        if ((modifyMask & 0b100000) != 0) {
                            preloadedTertiaryCenter[x + 2] = directionBlocksArray[5];
                            saveMaskTertiaryCenter |= 1L << (x + 2);
                        }
                    }

                    if ((modifyMask & 0b1000000) != 0) {
                        preloadedTertiaryCenter[x + 1] = directionBlocksArray[6];
                        saveMaskTertiaryCenter |= 1L << (x + 1);
                    }
                }
            }

            public void commitSaveMasks(int y, int z) {
                saveMask[(y - 1) & 3][z + 1] = saveMaskTertiaryDown;
                saveMask[(y + 1) & 3][z + 1] = saveMaskTertiaryUp;
                saveMaskSecondary[z] = saveMaskTertiaryNorth;
                saveMaskSecondary[z + 2] = saveMaskTertiarySouth;
                saveMaskSecondary[z + 1] = saveMaskTertiaryCenter;
            }
        }
    }
}

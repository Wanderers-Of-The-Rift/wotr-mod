package com.wanderersoftherift.wotr.client.render;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for extracting wireframe outlines from a {@link BakedModel}. This is used to generate visual outlines.
 */
public class ModelWireframeExtractor {

    /**
     * Extracts the wireframe lines from a given block model.
     *
     * @param model      The baked model of the block.
     * @param state      The block state (nullable).
     * @param rand       A random source for generating variations.
     * @param modelData  Model data for the block.
     * @param renderType The render type (nullable).
     * @return A list of {@link RenderLine} objects representing the wireframe.
     */
    public static List<RenderLine> extract(BakedModel model, @Nullable BlockState state, RandomSource rand,
            ModelData modelData, @Nullable RenderType renderType) {
        Set<RenderLine> lines = new HashSet<>();
        QuadVertexProcessor extractor = new QuadVertexProcessor(lines);
        for (Direction direction : Direction.values()) {
            for (BakedQuad quad : model.getQuads(state, direction, rand, modelData, renderType)) {
                extractor.unpack(quad);
            }
        }

        for (BakedQuad quad : model.getQuads(state, null, rand, modelData, renderType)) {
            extractor.unpack(quad);
        }
        return new ArrayList<>(lines);
    }

    // Modified version of VertexConsumer
    @MethodsReturnNonnullByDefault
    private static class QuadVertexProcessor {
        final Set<RenderLine> lines;
        final Vector3f[] vertices = new Vector3f[4];
        int vertexIndex = 0;

        private QuadVertexProcessor(Set<RenderLine> lines) {
            this.lines = lines;
        }

        /**
         * Adds a vertex to the current quad. Once four vertices are added, the quad is converted into four lines.
         *
         * @param pX X coordinate of the vertex.
         * @param pY Y coordinate of the vertex.
         * @param pZ Z coordinate of the vertex.
         */
        public void vertex(float pX, float pY, float pZ) {
            vertices[vertexIndex++] = new Vector3f(pX, pY, pZ);
            if (vertexIndex == 4) {
                vertexIndex = 0;
                lines.add(RenderLine.from(vertices[0], vertices[1]));
                lines.add(RenderLine.from(vertices[1], vertices[2]));
                lines.add(RenderLine.from(vertices[2], vertices[3]));
                lines.add(RenderLine.from(vertices[3], vertices[0]));
                Arrays.fill(vertices, null);
            }
        }

        /**
         * Extracts vertex positions from a given {@link BakedQuad}.
         *
         * @param pQuad The baked quad to extract from.
         */
        public void unpack(BakedQuad pQuad) {
            int[] quadVertices = pQuad.getVertices();
            for (int i = 0; i < 4; i++) {
                int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
                float x = Float.intBitsToFloat(quadVertices[offset]);
                float y = Float.intBitsToFloat(quadVertices[offset + 1]);
                float z = Float.intBitsToFloat(quadVertices[offset + 2]);
                this.vertex(x, y, z);
            }
        }
    }

    /**
     * Represents a line segment in 3D space, used for wireframe rendering.
     *
     * @param x1   X coordinate of the first point.
     * @param y1   Y coordinate of the first point.
     * @param z1   Z coordinate of the first point.
     * @param x2   X coordinate of the second point.
     * @param y2   Y coordinate of the second point.
     * @param z2   Z coordinate of the second point.
     * @param nX   Normalized X direction of the line.
     * @param nY   Normalized Y direction of the line.
     * @param nZ   Normalized Z direction of the line.
     * @param hash Precomputed hash code for faster lookup.
     */
    public record RenderLine(float x1, float y1, float z1, float x2, float y2, float z2, float nX, float nY, float nZ,
            int hash) {

        /**
         * Creates a new {@link RenderLine} from two 3D points.
         *
         * @param v1 The first point.
         * @param v2 The second point.
         * @return A new {@link RenderLine} object.
         */
        public static RenderLine from(Vector3f v1, Vector3f v2) {
            float nX = v2.x - v1.x;
            float nY = v2.y - v1.y;
            float nZ = v2.z - v1.z;
            float scalar = Math.invsqrt(Math.fma(nX, nX, Math.fma(nY, nY, nZ * nZ)));

            nX *= scalar;
            nY *= scalar;
            nZ *= scalar;

            return new RenderLine(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, nX, nY, nZ,
                    calculateHash(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z));
        }

        private static int calculateHash(float x1, float y1, float z1, float x2, float y2, float z2) {
            int result = Long.hashCode((long) Math.min(x1, x2) * 3200);
            result = 31 * result + Long.hashCode((long) Math.min(y1, y2) * 3200);
            result = 31 * result + Long.hashCode((long) Math.min(z1, z2) * 3200);
            result = 31 * result + Long.hashCode((long) Math.max(x1, x2) * 3200);
            result = 31 * result + Long.hashCode((long) Math.max(y1, y2) * 3200);
            result = 31 * result + Long.hashCode((long) Math.max(z1, z2) * 3200);
            return result;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}

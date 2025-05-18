package com.wanderersoftherift.wotr.client.map;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Utils3D {
    /**
     * Calculates the screen position of a 3D point
     * 
     * @param worldPos position to translate
     * @param camera   camera to use for translation
     */
    public static Vector3f projectPoint(
            Vector3f worldPos,
            VirtualCamera camera,
            Vector2i mapPosition,
            Vector2i mapSize) {
        int mapWidth = mapSize.x; // this is the width and height of the window we want to draw
        int mapHeight = mapSize.y;

        Vector4f transformed = new Vector4f(worldPos, 1.0f).mul(camera.getMVPMatrix()); // transform the point

        if (transformed.w != 0) {
            if (transformed.w < 0) {
                transformed.div((transformed.w));
                transformed.mul(-1); // hacky way to fix the artifact issues - still produces some artifacts but is much
                                     // better
            } else {
                transformed.div(transformed.w); // normalize perspective divide
            }
        } else {
            System.out.println("SOMETHING IS WRONG");
        }

        // convert from clip space (-1 to 1) to screen space (0 to width/height) and apply position offset
        float screenX = mapPosition.x + (transformed.x + 1.0f) * 0.5f * mapWidth;
        float screenY = mapPosition.y + (1.0f - transformed.y) * 0.5f * mapHeight; // y flipped for screen space

        return new Vector3f(screenX, screenY, -transformed.z * 10); // multiplied by 10 because otherwise the float
                                                                    // errors were too significant to work properly on z
                                                                    // ordering
    }

    /**
     * Calculates the screen position of a 3D point
     * 
     * @param worldPos position to translate
     * @param camera   camera to use for translation
     */

    public static Vector3f projectPoint(
            float x,
            float y,
            float z,
            VirtualCamera camera,
            Vector2i mapPosition,
            Vector2i mapSize) {
        int mapWidth = mapSize.x; // this is the width and height of the window we want to draw
        int mapHeight = mapSize.y;

        /*
         * Matrix4f mvpMatrix = new Matrix4f() .set(camera.getProjectionMatrix()) .mul(camera.getViewMatrix());
         */

        Vector4f transformed = new Vector4f(x, y, z, 1.0f).mul(camera.getMVPMatrix()); // transform the point

        if (transformed.w != 0) {
            if (transformed.w < 0) {
                transformed.div((transformed.w));
                transformed.mul(-1); // hacky way to fix the artifact issues - still produces some artifacts but is much
                                     // better
            } else {
                transformed.div(transformed.w); // normalize perspective divide
            }
        } else {
            System.out.println("SOMETHING IS WRONG");
        }

        // convert from clip space (-1 to 1) to screen space (0 to width/height) and apply position offset
        float screenX = mapPosition.x + (transformed.x + 1.0f) * 0.5f * mapWidth;
        float screenY = mapPosition.y + (1.0f - transformed.y) * 0.5f * mapHeight; // y flipped for screen space

        return new Vector3f(screenX, screenY, -transformed.z * 10); // multiplied by 10 because otherwise the float
                                                                    // errors were too significant to work properly on z
                                                                    // ordering
    }

    /**
     * Calculates the vertices of a cuboid
     * 
     * @param point1
     * @param point2
     */
    public static float[][] calculateVertices(Vector3f point1, Vector3f point2) {

        float minX = (float) Math.min(point1.x, point2.x);
        float maxX = (float) Math.max(point1.x, point2.x);
        float minY = (float) Math.min(point1.y, point2.y);
        float maxY = (float) Math.max(point1.y, point2.y);
        float minZ = (float) Math.min(point1.z, point2.z);
        float maxZ = (float) Math.max(point1.z, point2.z);

        return new float[][] { { minX, minY, minZ }, { maxX, minY, minZ }, { maxX, minY, maxZ }, { minX, minY, maxZ },
                { minX, maxY, minZ }, { maxX, maxY, minZ }, { maxX, maxY, maxZ }, { minX, maxY, maxZ } };
    }
}

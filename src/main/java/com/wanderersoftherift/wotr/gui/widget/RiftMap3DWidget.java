package com.wanderersoftherift.wotr.gui.widget;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.map.MapData;
import com.wanderersoftherift.wotr.client.map.MapRoom;
import com.wanderersoftherift.wotr.client.map.Utils3D;
import com.wanderersoftherift.wotr.client.render.MapRenderer3D;
import com.wanderersoftherift.wotr.config.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class RiftMap3DWidget extends AbstractWidget {
    private static final double MIN_SPEED = 0.5;
    private static final double MAX_SPEED = 5.0;
    private static final float MIN_DISTANCE = 1.0f;
    private static final float MAX_DISTANCE = 50.0f;

    private float targetDistance;
    private float targetPitch;
    private float targetYaw;
    private Vector3f targetPos = new Vector3f(0.0f, 0.0f, 0.0f);
    private long ticks = 0;

    private boolean pressingButton = false;
    private boolean justPressed = false;

    private MapRenderer3D mapRenderer;

    private int currentMouseX, currentMouseY;

    public RiftMap3DWidget(int x, int y, int width, int height, float renderDistance) {
        super(x, y, width, height, Component.literal("mapRenderer"));
        this.mapRenderer = new MapRenderer3D(x, y, width, height, renderDistance);
        this.targetPitch = mapRenderer.camPitch;
        this.targetYaw = mapRenderer.camYaw;
        this.targetPos = mapRenderer.camPos;
        this.targetDistance = mapRenderer.distance;
        this.ticks = 0;
    }

    public void resetCam() {
        this.mapRenderer.resetCam();
        this.targetPitch = mapRenderer.camPitch;
        this.targetYaw = mapRenderer.camYaw;
        this.targetPos = mapRenderer.camPos;
        this.targetDistance = mapRenderer.distance;
    }
    
    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int x, int y, float partialTick) {
        guiGraphics.renderOutline(this.getX() - 1, this.getY(), this.getWidth() + 1, this.getHeight(), 0xFFFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Minecraft.getInstance().fpsString, this.getX(), this.getY(), 0xFFFFFFFF);

        double lerpSpeed = ClientConfig.LERP_SPEED.get();
        if (lerpSpeed > 0) {
            this.mapRenderer.camPitch = Mth.lerp((float)lerpSpeed * partialTick, mapRenderer.camPitch, targetPitch);
            this.mapRenderer.camYaw = Mth.lerp((float)lerpSpeed * partialTick, mapRenderer.camYaw, targetYaw);
            this.mapRenderer.camPos.x = Mth.lerp((float)lerpSpeed * partialTick, mapRenderer.camPos.x, targetPos.x);
            this.mapRenderer.camPos.z = Mth.lerp((float)lerpSpeed * partialTick, mapRenderer.camPos.z, targetPos.z);
            this.mapRenderer.distance = Mth.lerp((float)lerpSpeed * partialTick, mapRenderer.distance, targetDistance);
        } else {
            this.mapRenderer.camPitch = targetPitch;
            this.mapRenderer.camYaw = targetYaw;
            this.mapRenderer.camPos = targetPos;
            this.mapRenderer.distance = targetDistance;
        }
        
        this.mapRenderer.renderMap(this.ticks, partialTick, this.currentMouseX, this.currentMouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void tick() {
        this.ticks += 1;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) { // need this to get mouse coords to the rendering for highlighting purposes
        this.currentMouseX = (int) mouseX;
        this.currentMouseY = (int) mouseY;
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (ClientConfig.MOUSE_MODE.get()) {
            if (justPressed) {
                justPressed = false;
                return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }
            int invertY = Minecraft.getInstance().options.invertYMouse().get() ? -1 : 1;

            if (button == 0) {
                targetPitch += (float) dragY * invertY;
                targetPitch = Math.clamp(targetPitch, -90, 90);
                targetYaw += (float) dragX;
            } else if (button == 1) {
                float yawRad = (float) Math.toRadians(targetYaw);
                float speed = (float) Mth.map(mapRenderer.distance, MIN_DISTANCE, MAX_DISTANCE, MIN_SPEED, MAX_SPEED);

                targetPos.z += (float) (dragY * speed * Math.cos(yawRad) - dragX * -1 * speed * Math.sin(yawRad)) / 20;
                targetPos.x += (float) (dragY * speed * Math.sin(yawRad) + dragX * -1 * speed * Math.cos(yawRad)) / 20;
            }

            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        } else {

            if (button == 0) {
                targetPitch += (float) dragY;
                targetPitch = Math.clamp(targetPitch, -90, 90);
                targetYaw += (float) dragX;
            } else if (button == 1) {
                float yawRad = (float) Math.toRadians(mapRenderer.camYaw);

                targetPos.z += (float) (-dragY * Math.cos(yawRad) - dragX * Math.sin(yawRad))/20;
                targetPos.x += (float) (-dragY * Math.sin(yawRad) + dragX * Math.cos(yawRad))/20;
            }
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ClientConfig.MOUSE_MODE.get()) {
            if (!pressingButton) {
                GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                justPressed = true;
            }

            pressingButton = true;
        }

        // find the closest room to the mouse
        float closestZ = Float.MIN_VALUE;
        MapRoom selectedRoom = null;

        for (var entry : MapData.rooms.entrySet()) { // iterate over rooms, foreach not used because I need to change closestZ and selectedCell, and don't like atomic references
            MapRoom room = entry.getValue();
            // get projected vertices of the room
            Vector3f pos2_sub = new Vector3f(room.pos2.x - 0.3f, room.pos2.y - 0.3f, room.pos2.z - 0.3f);
            float[][] vertices = Utils3D.calculateVertices(room.pos1, pos2_sub);

            // project vertices to screen space
            // I dont mind doing this twice (once for rendering as well) because this is only done on click, worst case there will be a little hitch on larger rifts
            Vector3f[] projectedVertices = new Vector3f[8];
            for (int i = 0; i < vertices.length; i++) {
                projectedVertices[i] = Utils3D.projectPoint(vertices[i][0], vertices[i][1], vertices[i][2],
                        this.mapRenderer.getCamera(), this.mapRenderer.mapPosition, this.mapRenderer.mapSize);
            }

            // check if mouse is inside room area
            if (Utils3D.isPointInPolygon((int)mouseX, (int)mouseY, projectedVertices)) {
                // get average z for finding closest room
                float avgZ = 0;
                int count = 0;
                for (Vector3f vertex : projectedVertices) {
                    avgZ += vertex.z;
                    count++;
                }
                avgZ /= count;

                // track closest
                if (avgZ > closestZ) {
                    closestZ = avgZ;
                    selectedRoom = room;
                }
            }
        }

        if (selectedRoom != null) {
            // Handle selection
            WanderersOfTheRift.LOGGER.info("Selected cube at position: " + selectedRoom.pos1);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (ClientConfig.MOUSE_MODE.get()) {
            pressingButton = false;

            GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double a, double b, double c , double d) {
        targetDistance -= (float) d;
        targetDistance = Math.clamp(targetDistance, MIN_DISTANCE, MAX_DISTANCE);

        return super.mouseScrolled(a, b, c, d);
    }
}

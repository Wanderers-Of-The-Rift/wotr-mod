package com.wanderersoftherift.wotr.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.wanderersoftherift.wotr.client.ModShaders;
import com.wanderersoftherift.wotr.client.map.MapCell;
import com.wanderersoftherift.wotr.client.map.MapRoomEffects;
import com.wanderersoftherift.wotr.client.map.VirtualCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import static com.wanderersoftherift.wotr.client.map.MapData.rooms;

public class MapRenderer3D {
    public static final VertexFormatElement EFFECTS = VertexFormatElement.register(6, 0, VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.GENERIC, 1);

    private static final VertexFormat VERTEX_FORMAT = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV0", VertexFormatElement.UV0)
            .add("Color", VertexFormatElement.COLOR)
            .add("Effects", EFFECTS)
            .build();

    public Vector2i mapPosition = new Vector2i(0, 0);
    public Vector2i mapSize = new Vector2i(Minecraft.getInstance().getWindow().getGuiScaledWidth(),
            Minecraft.getInstance().getWindow().getGuiScaledHeight());
    private Vector2i scissorCoords = new Vector2i(0, 0);
    private Vector2i scissorSize = new Vector2i(0, 0);

    private VirtualCamera camera = new VirtualCamera(70.0f, 16f / 9f, 0.1f, 1000.0f);

    public float camPitch = 35;
    public float camYaw = -25;
    public Vector3f camPos = new Vector3f(0.5f);
    public float distance = 10;

    public MapRenderer3D(int x, int y, int width, int height, float renderDistance) {
        setMapSize(x, y, width, height);
        this.renderDistance = renderDistance;
    }

    public void resetCam() {
        camPitch = 35;
        camYaw = 155;
        camPos = new Vector3f(0.35f);
        distance = 10;
    }

    // I used reflection here before but I've learned from my sins and repented.
    // Now I have commited another sin by doing an access transformer.
    public static void putEffects(int effectFlags, BufferBuilder builder) {
        long i = builder.beginElement(EFFECTS);
        ;
        MemoryUtil.memPutFloat(i, (float) effectFlags);
    }

    private float renderDistance = 10;

    private boolean isInRenderDistance(Vector3f pos) {
        return pos.distance(camPos) < renderDistance;
    }

    public void renderMap(long tick, float partialTick) {
        // sets both position and rotation
        camera.orbitAroundOrigin(camPitch, camYaw, distance, camPos.x, camPos.y, camPos.z);

        // bunch of RenderSystem stuff, don't touch, it's radioactive and extremely volatile
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableCull();

        // !!! Scissor coords are from bottom left and not scaled with gui scale !!! the below code renders top left
        // quadrant
        // the RenderSystem is from top left instead because yes
        RenderSystem.enableScissor(scissorCoords.x, scissorCoords.y, scissorSize.x, scissorSize.y);

        RenderSystem.lineWidth(10.0f);
        BufferBuilder lineBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, VERTEX_FORMAT); // prep
                                                                                                                 // the
                                                                                                                 // buffer

        float screenWidth = Minecraft.getInstance().getWindow().getWidth();
        float screenHeight = Minecraft.getInstance().getWindow().getHeight();

        CompiledShaderProgram shader = RenderSystem.setShader(ModShaders.RIFT_MAPPER);
        if (shader != null) {
            Uniform screenSize = shader.getUniform("ScreenSize");
            if (screenSize != null) {
                screenSize.set(screenWidth, screenHeight);
            }
            shader.apply();
        }
        RenderSystem.setShaderGameTime(tick, partialTick);

        // just some testing cubes to render
        // Cube cube1 = new Cube(new Vector3d(0,0,0), new Vector3d(1, 1, 1));
        MapCell centerCube = new MapCell(
                new Vector3f((float) (camPos.x - 0.01), (float) (camPos.y - 0.01), (float) (camPos.z - 0.01)), 0.02f,
                0);

        // MapCell player = new MapCell(new Vector3f(0.25f, 0.25f, 0.25f), new Vector3f(0.75f, 0.75f, 0.75f), 0);

        float pos1 = (float) (0.35f - (0.35 / 2f));
        MapCell player = new MapCell(new Vector3f(pos1, pos1, pos1), 0.35F, 0).setEffects(MapRoomEffects
                .getFlags(new MapRoomEffects.Flag[] { MapRoomEffects.Flag.DOTS, MapRoomEffects.Flag.EDGE_HIGHLIGHT, }));

        rooms.forEach((pos, room) -> {
            if (isInRenderDistance(room.pos1)) room.renderWireframe(lineBuffer, camera, mapPosition, mapSize);
        });
        /*
         * cells.forEach((pos, cell) -> { cell.renderWireframe(lineBuffer, camera, mapPosition, mapSize); });
         */
        // prepare the buffer for rendering and draw it
        MeshData bufferData = lineBuffer.build();
        ;
        if (bufferData != null) {
            BufferUploader.drawWithShader(bufferData);
        }

        // render transparents
        BufferBuilder quadBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, VERTEX_FORMAT); // prep the
                                                                                                           // buffer

        RenderSystem.depthMask(false);

        rooms.forEach((pos, room) -> {
            if (isInRenderDistance(room.pos1))
                room.renderCube(quadBuffer, camera, new Vector4f(0f, 1f, 0f, 0.2f), mapPosition, mapSize);
        });
        /*
         * cells.forEach((pos, cell) -> { cell.renderCube(quadBuffer, camera, new Vector4f(0f, 0f, 1f, 0.2f),
         * mapPosition, mapSize); });
         */

        MeshData quadBufferData = quadBuffer.build();
        if (quadBufferData != null) {
            BufferUploader.drawWithShader(quadBufferData);
        }

        // render non-transparents with proper occlusion
        BufferBuilder quadBuffer2 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, VERTEX_FORMAT); // prep the
                                                                                                            // buffer

        RenderSystem.depthMask(true);

        centerCube.renderCube(quadBuffer2, camera, new Vector4f(0.2f, 0.2f, 0.2f, 1f), mapPosition, mapSize);
        player.renderCube(quadBuffer2, camera, new Vector4f(0f, 0f, 1f, 1f), mapPosition, mapSize);

        MeshData quadBufferData2 = quadBuffer2.build();
        if (quadBufferData2 != null) {
            BufferUploader.drawWithShader(quadBufferData2);
        }

        RenderSystem.depthMask(true);

        BufferBuilder line2Buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, VERTEX_FORMAT); // prep
                                                                                                                  // the
                                                                                                                  // buffer

        player.renderWireframe(line2Buffer, camera, mapPosition, mapSize);

        // prepare the buffer for rendering and draw it
        MeshData buffer2Data = line2Buffer.build();
        ;
        if (buffer2Data != null) {
            BufferUploader.drawWithShader(buffer2Data);
        }

        RenderSystem.disableScissor();
        RenderSystem.disableBlend();
    }

    /**
     * Sets the position and size of the map and calculates the proper scissor coords and size + virtual camera aspect
     * ratio Uses the usual top left corner origin coords and width, height (unlike scissor)
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setMapSize(int x, int y, int width, int height) {
        mapPosition.x = x;
        mapPosition.y = y;
        mapSize.x = width;
        mapSize.y = height;

        // calculate coefficients for scaling
        float widthCoef = (float) Minecraft.getInstance().getWindow().getWidth()
                / Minecraft.getInstance().getWindow().getGuiScaledWidth();
        float heightCoef = (float) Minecraft.getInstance().getWindow().getHeight()
                / Minecraft.getInstance().getWindow().getGuiScaledHeight();

        // calculate the scissor coords - pos is from top left, scissors are from bottom left
        scissorCoords.x = (int) (x * widthCoef);
        scissorCoords.y = (int) (Minecraft.getInstance().getWindow().getHeight() - (y + height) * heightCoef);

        // calculate the scissor size - width, height are scaled to gui, scissor is not so it has to be scaled back
        scissorSize.x = (int) (width * widthCoef);
        scissorSize.y = (int) (height * heightCoef);

        // update aspect ratio of the virtual camera
        camera.setAspectRatio((float) width / height);
    }
}

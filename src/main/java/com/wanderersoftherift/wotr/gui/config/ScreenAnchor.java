package com.wanderersoftherift.wotr.gui.config;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

/**
 * Provides anchors for UI element positioning.
 */
public enum ScreenAnchor implements StringRepresentable {
    TOP_LEFT("top_left", HorizontalAnchor.LEFT, VerticalAnchor.TOP),
    TOP_CENTER("top_center", HorizontalAnchor.CENTER, VerticalAnchor.TOP),
    TOP_RIGHT("top_right", HorizontalAnchor.RIGHT, VerticalAnchor.TOP),
    CENTER_LEFT("center_left", HorizontalAnchor.LEFT, VerticalAnchor.CENTER),
    CENTER("center", HorizontalAnchor.CENTER, VerticalAnchor.CENTER),
    CENTER_RIGHT("center_right", HorizontalAnchor.RIGHT, VerticalAnchor.CENTER),
    BOTTOM_LEFT("bottom_left", HorizontalAnchor.LEFT, VerticalAnchor.BOTTOM),
    BOTTOM_CENTER("bottom_center", HorizontalAnchor.CENTER, VerticalAnchor.BOTTOM),
    BOTTOM_RIGHT("bottom_right", HorizontalAnchor.RIGHT, VerticalAnchor.BOTTOM);

    public static final StringRepresentable.StringRepresentableCodec<ScreenAnchor> CODEC = StringRepresentable
            .fromEnum(ScreenAnchor::values);

    private static final Table<HorizontalAnchor, VerticalAnchor, ScreenAnchor> lookup;

    private final String name;
    private final HorizontalAnchor horizontalAnchor;
    private final VerticalAnchor verticalAnchor;

    static {
        ImmutableTable.Builder<HorizontalAnchor, VerticalAnchor, ScreenAnchor> builder = ImmutableTable.builder();
        for (ScreenAnchor anchor : ScreenAnchor.values()) {
            builder.put(anchor.horizontalAnchor, anchor.verticalAnchor, anchor);
        }
        lookup = builder.build();
    }

    ScreenAnchor(String name, HorizontalAnchor hAnchor, VerticalAnchor vAnchor) {
        this.name = name;
        this.horizontalAnchor = hAnchor;
        this.verticalAnchor = vAnchor;
    }

    /**
     * @param horizontalAnchor
     * @param verticalAnchor
     * @return The screen anchor that combines the given horizontal and vertical anchors
     */
    public static ScreenAnchor get(HorizontalAnchor horizontalAnchor, VerticalAnchor verticalAnchor) {
        return lookup.get(horizontalAnchor, verticalAnchor);
    }

    /**
     * @return The horizontal portion of the anchoring
     */
    public HorizontalAnchor getHorizontal() {
        return horizontalAnchor;
    }

    /**
     * @return The vertical portion of the anchoring
     */
    public VerticalAnchor getVertical() {
        return verticalAnchor;
    }

    /**
     * Given the relative coordinates of the element, its dimensions and the screen dimensions, determines the position
     * of the top-left corner of the element
     * 
     * @param xOffset
     * @param yOffset
     * @param width
     * @param height
     * @param screenWidth
     * @param screenHeight
     * @return The top-left position of the element
     */
    public Vector2i getPos(int xOffset, int yOffset, int width, int height, int screenWidth, int screenHeight) {
        return new Vector2i(horizontalAnchor.getPos(xOffset, width, screenWidth),
                verticalAnchor.getPos(yOffset, height, screenHeight));
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}

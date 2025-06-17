package com.wanderersoftherift.wotr.gui.config;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Orientation to render a UI element with
 */
public enum UIOrientation implements StringRepresentable {
    HORIZONTAL("horizontal", new Vector2i(1, 0)),
    VERTICAL("vertical", new Vector2i(0, 1));

    public static final StringRepresentable.StringRepresentableCodec<UIOrientation> CODEC = StringRepresentable
            .fromEnum(UIOrientation::values);

    private final String name;
    private final Vector2i axis;

    UIOrientation(String name, Vector2i axis) {
        this.name = name;
        this.axis = axis;
    }

    /**
     * @return The next orientation
     */
    public UIOrientation rotate() {
        return UIOrientation.values()[(ordinal() + 1) % UIOrientation.values().length];
    }

    /**
     * @return The axis vector of this orientation
     */
    public Vector2ic axis() {
        return axis;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}

package com.wanderersoftherift.wotr.gui.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.joml.Vector2i;

/**
 * Configuration data for HUD element positioning
 */
public class HudElementConfig {
    private final ModConfigSpec.BooleanValue visible;
    private final ModConfigSpec.EnumValue<ScreenAnchor> anchor;
    private final ModConfigSpec.IntValue x;
    private final ModConfigSpec.IntValue y;
    private final ModConfigSpec.EnumValue<UIOrientation> orientation;

    /**
     * Creates a HudElementConfig that doesn't support orientation
     *
     * @param builder
     * @param elementName
     * @param elementPrefix
     * @param defaultVisible
     * @param defaultAnchor
     * @param defaultX
     * @param defaultY
     */
    public HudElementConfig(ModConfigSpec.Builder builder, String elementName, String elementPrefix,
            boolean defaultVisible, ScreenAnchor defaultAnchor, int defaultX, int defaultY) {
        this(builder, elementName, elementPrefix, defaultVisible, defaultAnchor, defaultX, defaultY, null);
    }

    /**
     * Creates a HudElementConfig that supports orientation
     *
     * @param builder
     * @param elementName
     * @param elementPrefix
     * @param defaultVisible
     * @param defaultAnchor
     * @param defaultX
     * @param defaultY
     * @param defaultOrientation
     */
    public HudElementConfig(ModConfigSpec.Builder builder, String elementName, String elementPrefix,
            boolean defaultVisible, ScreenAnchor defaultAnchor, int defaultX, int defaultY,
            UIOrientation defaultOrientation) {
        builder.push(" == " + elementName + " Location == ");
        visible = builder.comment("Whether to show the " + elementName + " at all")
                .define(elementPrefix + "Visible", defaultVisible);
        anchor = builder.comment(" Where to position the " + elementName + " relative to")
                .defineEnum(elementPrefix + "Anchor", defaultAnchor);
        x = builder.comment(" Relative horizontal position of the " + elementName)
                .defineInRange(elementPrefix + "X", defaultX, Integer.MIN_VALUE, Integer.MAX_VALUE);
        y = builder.comment(" Relative vertical position of the " + elementName)
                .defineInRange(elementPrefix + "Y", defaultY, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (defaultOrientation != null) {
            orientation = builder.comment(" Orientation of the " + elementName)
                    .defineEnum(elementPrefix + "Orientation", defaultOrientation);
        } else {
            orientation = null;
        }
        builder.pop();
    }

    /**
     * Restore the config to its default settings
     */
    public void reset() {
        visible.set(visible.getDefault());
        anchor.set(anchor.getDefault());
        x.set(x.getDefault());
        y.set(y.getDefault());
        if (orientation != null) {
            orientation.set(orientation.getDefault());
        }
    }

    /**
     * @param width        Current width of the element
     * @param height       Current height of the element
     * @param screenWidth  Width of the screen
     * @param screenHeight Height of the screen
     * @return The upper-left corner to render the element, given its current width/height and the screen width/height
     */
    public Vector2i getPosition(int width, int height, int screenWidth, int screenHeight) {
        return getAnchor().getPos(getX(), getY(), width, height, screenWidth, screenHeight);
    }

    /**
     * Shifts anchor to the nearest one to the element, and adjusts the relative positioning such that the element
     * maintains the same position
     *
     * @param width        Width of the element
     * @param height       Height of the element
     * @param screenWidth  Width of the screen
     * @param screenHeight Height of the screen
     */
    public void reanchor(int width, int height, int screenWidth, int screenHeight) {
        Vector2i pos = getPosition(width, height, screenWidth, screenHeight);
        HorizontalAnchor newHorizontalAnchor = HorizontalAnchor.getClosest(pos.x(), width, screenWidth);
        VerticalAnchor newVerticalAnchor = VerticalAnchor.getClosest(pos.y(), height, screenHeight);
        this.anchor.set(ScreenAnchor.get(newHorizontalAnchor, newVerticalAnchor));
        pos.sub(anchor.get().getPos(0, 0, width, height, screenWidth, screenHeight));
        setX(pos.x);
        setY(pos.y);
    }

    /**
     * @return Whether this element config is in its default state
     */
    public boolean isDefault() {
        return x.getDefault().equals(x.get()) && y.getDefault().equals(y.get())
                && anchor.getDefault().equals(anchor.get()) && visible.getDefault().equals(visible.get())
                && (!hasOrientation() || orientation.getDefault().equals(orientation.get()));
    }

    /**
     * @return is the element visible
     */
    public boolean isVisible() {
        return visible.get();
    }

    /**
     * @return Where the element is anchored
     */
    public ScreenAnchor getAnchor() {
        return anchor.get();
    }

    /**
     * @return The x offset of the element from its anchor
     */
    public int getX() {
        return x.get();
    }

    /**
     * @return The y offset of the element from its anchor
     */
    public int getY() {
        return y.get();
    }

    /**
     * @return Whether this element allow orientation
     */
    public boolean hasOrientation() {
        return orientation != null;
    }

    /**
     * @return The current orientation of the element
     */
    public UIOrientation getOrientation() {
        if (orientation != null) {
            return orientation.get();
        }
        return UIOrientation.HORIZONTAL;
    }

    public void setVisible(boolean value) {
        visible.set(value);
    }

    public void setAnchor(ScreenAnchor anchor) {
        this.anchor.set(anchor);
    }

    public void setX(int x) {
        this.x.set(x);
    }

    public void setY(int y) {
        this.y.set(y);
    }

    public void setOrientation(UIOrientation newValue) {
        if (orientation != null) {
            orientation.set(newValue);
        }
    }

    /**
     * Toggles the orientation
     */
    public void reorientate() {
        orientation.set(orientation.get().rotate());
    }

    public ScreenAnchor getDefaultAnchor() {
        return anchor.getDefault();
    }

    public int getDefaultX() {
        return x.getDefault();
    }

    public int getDefaultY() {
        return y.getDefault();
    }

    public UIOrientation getDefaultOrientation() {
        if (orientation != null) {
            return orientation.getDefault();
        }
        return UIOrientation.HORIZONTAL;
    }

    /**
     * Builder for defining HudElementConfig
     */
    public static class Builder {
        private final String name;
        private final String prefix;

        private ScreenAnchor anchor = ScreenAnchor.TOP_LEFT;
        private int x = 0;
        private int y = 0;
        private boolean visible = true;
        private UIOrientation orientation = null;

        /**
         * @param name   The display name of the element being configured to appear in the config comments
         * @param prefix The prefix of the element being configured to be included in all config field names
         */
        public Builder(String name, String prefix) {
            this.name = name;
            this.prefix = prefix;
        }

        /**
         * Sets the default anchor for the element, TOP_LEFT if not specified
         * 
         * @param anchor The default anchor for the element
         * @return The builder for method chaining
         */
        public Builder anchor(ScreenAnchor anchor) {
            this.anchor = anchor;
            return this;
        }

        /**
         * Sets the default offsets for the element, 0,0 if not specified
         * 
         * @param x
         * @param y
         * @return The builder for method chaining
         */
        public Builder offset(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Sets whether the element is visible by default, true if not specified
         * 
         * @param value
         * @return The builder for method chaining
         */
        public Builder visible(boolean value) {
            this.visible = value;
            return this;
        }

        /**
         * Sets whether the element can be rotated, and the default orientation if so
         * 
         * @param defaultOrientation
         * @return The builder for method chaining
         */
        public Builder rotates(UIOrientation defaultOrientation) {
            this.orientation = defaultOrientation;
            return this;
        }

        /**
         * @param builder The ModConfigSpec builder to add the fields into
         * @return The built HudElementConfig
         */
        public HudElementConfig build(ModConfigSpec.Builder builder) {
            return new HudElementConfig(builder, name, prefix, visible, anchor, x, y, orientation);
        }
    }
}

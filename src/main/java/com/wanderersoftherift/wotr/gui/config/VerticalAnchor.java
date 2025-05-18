package com.wanderersoftherift.wotr.gui.config;

/**
 * Provides anchors for UI element positioning
 */
public enum VerticalAnchor {
    TOP {
        @Override
        public int getPos(int y, int height, int screenHeight) {
            return y;
        }
    },
    CENTER {
        @Override
        public int getPos(int y, int height, int screenHeight) {
            return screenHeight / 2 - height / 2 + y;
        }
    },
    BOTTOM {
        @Override
        public int getPos(int y, int height, int screenHeight) {
            return screenHeight - height + y;
        }
    };

    public abstract int getPos(int y, int height, int screenHeight);

    /**
     * @param pos          The top position of this element
     * @param height       The height of the element
     * @param screenHeight The height of the screen
     * @return The closest vertical anchor to the element
     */
    public static VerticalAnchor getClosest(int pos, int height, int screenHeight) {
        VerticalAnchor result = TOP;
        int dist = pos;
        int centerDist = Math.abs(pos + height / 2 - screenHeight / 2);
        if (centerDist < dist) {
            dist = centerDist;
            result = CENTER;
        }
        int rightDist = screenHeight - pos - height;
        if (rightDist < dist) {
            result = BOTTOM;
        }
        return result;
    }
}

package com.wanderersoftherift.wotr.gui.config;

/**
 * Horizontal anchors for UI element positioning
 */
public enum HorizontalAnchor {
    LEFT {
        @Override
        public int getPos(int x, int width, int screenWidth) {
            return x;
        }
    },
    CENTER {
        @Override
        public int getPos(int x, int width, int screenWidth) {
            return screenWidth / 2 - width / 2 + x;
        }
    },
    RIGHT {
        @Override
        public int getPos(int x, int width, int screenWidth) {
            return screenWidth - width + x;
        }
    };

    public abstract int getPos(int x, int width, int screenWidth);

    /**
     * @param pos         The left position of the element
     * @param width       The width of the element
     * @param screenWidth The width of the screen
     * @return The closest anchor to the ui element
     */
    public static HorizontalAnchor getClosest(int pos, int width, int screenWidth) {
        HorizontalAnchor result = LEFT;
        int dist = pos;
        int centerDist = Math.abs(pos + width / 2 - screenWidth / 2);
        if (centerDist < dist) {
            dist = centerDist;
            result = CENTER;
        }
        int rightDist = screenWidth - pos - width;
        if (rightDist < dist) {
            result = RIGHT;
        }
        return result;
    }

}

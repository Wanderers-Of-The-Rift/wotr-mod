package com.wanderersoftherift.wotr.gui.menu.character;

/**
 * A hint for how to order an element relative to another element
 */
public enum OrderHint {
    BEFORE {
        @Override
        public int insertAt(int index) {
            return index;
        }
    },
    AFTER {
        @Override
        public int insertAt(int index) {
            return index + 1;
        }
    };

    /**
     * @param index The index of the item to be relative to
     * @return Index to insert at to respect the order hint
     */
    public abstract int insertAt(int index);
}

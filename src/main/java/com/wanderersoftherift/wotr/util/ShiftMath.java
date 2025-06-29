package com.wanderersoftherift.wotr.util;

public class ShiftMath {

    public static int ceilPow2(int n) {
        return 1 << shiftForCeilPow2(2);
    }

    public static int shiftForCeilPow2(int n) {
        return 32 - Integer.numberOfLeadingZeros(n - 1);
    }
}

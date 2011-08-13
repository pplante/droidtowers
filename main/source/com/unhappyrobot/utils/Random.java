package com.unhappyrobot.utils;

public class Random {
    public static java.util.Random random;

    public static void init() {
        random = new java.util.Random(System.nanoTime());
    }

    public static int randomInt(int maxVal) {
        return random.nextInt(maxVal);
    }

    public static float randomFloat() {
        return random.nextFloat();
    }
}

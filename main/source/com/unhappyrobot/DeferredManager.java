package com.unhappyrobot;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class DeferredManager {
    public static List<Deferrable> deferrables = new ArrayList<Deferrable>();
    private static float currentTime;

    public static float getCurrentTime() {
        return currentTime;
    }

    public static void runEvery(float delayInMillis, Runnable runnable) {
        Deferrable def = new Deferrable();
        def.runDelayInMillis = delayInMillis;
        def.runnable = runnable;

        deferrables.add(def);
    }

    public static void update(float currentTime) {
        DeferredManager.currentTime += currentTime;
        for (Deferrable deferrable : deferrables) {
            deferrable.check();
        }
    }

    private static class Deferrable {
        float lastRun;
        float runDelayInMillis;
        Runnable runnable;

        public void check() {
            if(lastRun + runDelayInMillis <= currentTime) {
                lastRun = currentTime;
                runnable.run();
            }
        }
    }
}

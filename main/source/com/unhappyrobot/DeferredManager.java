package com.unhappyrobot;

import java.util.ArrayList;
import java.util.List;

public class DeferredManager {
    public List<Deferrable> deferrables = new ArrayList<Deferrable>();
    private float currentTime;
    public ThreadGroup deferrableThreadPool = new ThreadGroup("DeferrableManagerPool");

    private static DeferredManager physicsDeferredManager;
    private static DeferredManager gameDeferredManager;

    public static DeferredManager onGameThread() {
        if(gameDeferredManager == null) {
            gameDeferredManager = new DeferredManager();
        }

        return gameDeferredManager;
    }

    public static DeferredManager onPhysicsThread() {
        if(physicsDeferredManager == null) {
            physicsDeferredManager = new DeferredManager();
        }

        return physicsDeferredManager;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void update(float timeDelta) {
        currentTime += timeDelta;

        List<Deferrable> marked = new ArrayList<Deferrable>();
        for (Deferrable deferrable : deferrables) {
            deferrable.check(this);

            if (deferrable.markedForRemoval) {
                marked.add(deferrable);
            }
        }

        deferrables.removeAll(marked);
    }

    public void runEvery(float delayInMillis, Runnable runnable) {
        makeDeferrable(delayInMillis, true, false, runnable);
    }

    public void runAfterDelay(float delayInMillis, Runnable runnable) {
        makeDeferrable(delayInMillis, false, false, runnable);
    }

    private void makeDeferrable(float delayInMillis, boolean runForever, boolean useIsolatedThread, Runnable runnable) {
        Deferrable def = new Deferrable();
        def.runDelayInMillis = delayInMillis;
        def.runForever = runForever;
        def.runnable = runnable;
        def.useIsolatedThread = useIsolatedThread;

        deferrables.add(def);
    }

    public void runAsync(Runnable runnable) {
        makeDeferrable(0.0f, false, true, runnable);
    }

    public void asyncWithDelay(float delayInMillis, Runnable runnable) {
        makeDeferrable(delayInMillis, true, true, runnable);
    }

    public void runOnce(Runnable runnable) {
        makeDeferrable(0, false, false, runnable);
    }

    private static class Deferrable {
        public float lastRun;
        public float runDelayInMillis;
        public boolean runForever;
        public boolean markedForRemoval;
        public boolean useIsolatedThread;
        public Runnable runnable;
        public Thread isolatedThread;

        public void check(DeferredManager manager) {
            if (!markedForRemoval && lastRun + runDelayInMillis <= manager.currentTime) {
                lastRun = manager.currentTime;
                if (useIsolatedThread) {
                    new Thread(manager.deferrableThreadPool, runnable).start();
                } else {
                    runnable.run();
                }

                if (!runForever) {
                    markedForRemoval = true;
                }
            }
        }
    }
}

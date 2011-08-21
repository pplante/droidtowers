package com.unhappyrobot.scripting;

import com.badlogic.gdx.math.Vector2;
import org.mozilla.javascript.ScriptableObject;

public class ScriptedVector2 extends ScriptableObject {
    private Vector2 vector;

    @Override
    public String getClassName() {
        return "Vector2";
    }

    public ScriptedVector2() {
        vector = new Vector2();
    }

    public void jsFunction_set(double x, double y) {
        vector.set((float) x, (float) y);
    }

    public void jsFunction_set(Vector2 otherVector) {
        vector.set(otherVector);
    }
}

package com.unhappyrobot.physics;

import com.badlogic.gdx.math.Vector2;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties("filter")
public class PhysicsBodyPoint {
    public float density;
    public float friction;
    public float bounce;
    public float[] shape;

    public PhysicsBodyPoint() {

    }

    public Vector2[] getVectorData(Vector2 origin, float scale) {
        List<Vector2> data = new ArrayList<Vector2>();

        for (int i = 0; i < shape.length; i += 2) {
            Vector2 point = new Vector2(shape[i], shape[i + 1]);
            point.sub(origin);
            point.mul(scale);
            data.add(point);
        }

        return data.toArray(new Vector2[data.size()]);
    }
}

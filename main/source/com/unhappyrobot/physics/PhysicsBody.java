package com.unhappyrobot.physics;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;

public class PhysicsBody {
    @JsonDeserialize(as=ArrayList.class)
    public
    ArrayList<PhysicsBodyPoint> points;

    public static PhysicsBody fromJson(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        return mapper.readValue(jsonData, PhysicsBody.class);
    }

    public ArrayList<PhysicsBodyPoint> getPoints() {
        return points;
    }
}

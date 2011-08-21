package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.unhappyrobot.WorldManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InputManager {
    public static class Keys extends Input.Keys {
    }

    private HashMap<Integer, ArrayList<Action>> keyBindings;

    private static InputManager instance;

    private InputManager() {
        keyBindings = new HashMap<Integer, ArrayList<Action>>();
    }

    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }

        return instance;
    }

    public static void bind(int keyCode, Action action) {
        ArrayList<Action> actions = getBindingsForKeyCode(keyCode);

        actions.clear();
        actions.add(action);
    }

    private static ArrayList<Action> getBindingsForKeyCode(int keyCode) {
        InputManager inputManager = InputManager.getInstance();

        ArrayList<Action> actions;

        if (!inputManager.keyBindings.containsKey(keyCode)) {
            actions = new ArrayList<Action>();
            inputManager.keyBindings.put(keyCode, actions);
        } else {
            actions = inputManager.keyBindings.get(keyCode);
        }

        return actions;
    }


    public static void listen(int keyCode, Action action) {
        getBindingsForKeyCode(keyCode).add(action);
    }

    public static void update(float timeDelta) {
        if (WorldManager.isLocked()) return;

        InputManager instance = InputManager.getInstance();

        for (Map.Entry<Integer, ArrayList<Action>> entry : instance.keyBindings.entrySet()) {
            if (Gdx.input.isKeyPressed(entry.getKey())) {
                if (entry.getValue().size() > 0) {
                    for (Action action : entry.getValue()) {
                        action.run(timeDelta);
                    }
                }
            }
        }
    }
}

package com.unhappyrobot.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.InvocationTargetException;
import java.util.EmptyStackException;

public class ScriptScope {
    private Context scriptContext;
    private Scriptable scriptScope;
    private Object scriptableObject;

    public boolean parseScript(String scriptSource) {
        scriptContext = Context.enter();

        try {
            scriptContext.setOptimizationLevel(-1);
            scriptScope = scriptContext.initStandardObjects();
            ScriptableObject.defineClass(scriptScope, ScriptedGameObject.class);

            Object jsOut = Context.javaToJS(System.out, scriptScope);
            ScriptableObject.putProperty(scriptScope, "out", jsOut);

            scriptContext.evaluateString(scriptScope, scriptSource, "<cmd>", 1, null);

            Object instance = scriptScope.get("instance", scriptScope);
            if(instance == Scriptable.NOT_FOUND) {
                System.out.println("cannot find instance!");
                return false;
            }

            scriptableObject = instance;

        } catch(JavaScriptException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            Context.exit();
        }

        return true;
    }

    public boolean call(String methodName, Object... args) {
        if(scriptableObject == null) return false;

        Object result = ScriptableObject.callMethod((Scriptable) scriptableObject, methodName, args);

        return true;
    }
}

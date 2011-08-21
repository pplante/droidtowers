package com.unhappyrobot.utils;

import com.badlogic.gdx.files.FileHandle;

import java.io.*;

public class IO {

    public static Reader readFile(String filename) throws IOException {
        File fp = new File(filename);
        return new InputStreamReader(new FileInputStream(fp), "UTF8");
    }

    public static Reader readFile(FileHandle internal) throws IOException {
        return readFile(internal.path());
    }

    public static String readTextFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(readFile(filename));

        String output = "";
        String line = reader.readLine();

        while (line != null) {
            output += line + "\n";
            line = reader.readLine();
        }

        reader.close();

        return output;
    }
}

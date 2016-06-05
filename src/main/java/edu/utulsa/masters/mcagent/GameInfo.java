package edu.utulsa.masters.mcagent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Modifier;

/**
 * Created by chad on 5/31/16.
 */
public class GameInfo {
    private transient static final String FILE_NAME = "game_info.json";

    public static double GRAVITY_ACCEL;
    public static double WALK_SPEED;
    public static double WALK_JUMP_SPEED;
    public static double WALK_JUMP_HALFDIST;
    public static double WALK_JUMP_DIST;
    public static double SPRINT_SPEED;
    public static double SPRINT_JUMP_SPEED;
    public static double SPRINT_JUMP_HALFDIST;
    public static double SPRINT_JUMP_DIST;
    public static double SNEAK_SPEED;
    public static double SNEAK_JUMP_SPEED;
    public static double SNEAK_JUMP_HALFDIST;
    public static double SNEAK_JUMP_DIST;


    public static void initialize() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        try {
            JsonReader reader = new JsonReader(new FileReader(FILE_NAME));
            GameInfo info = gson.fromJson(reader, GameInfo.class);
        } catch (FileNotFoundException e) {
            System.err.println("Could not find 'game_info.json'. Doing nothing now...?");
        }
    }

    public static void saveData() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_NAME), "UTF-8"))) {
            writer.write(gson.toJson(new GameInfo()));
        } catch(FileNotFoundException e) {}
        catch(UnsupportedEncodingException e) {}
        catch(IOException e) {}

    }
}

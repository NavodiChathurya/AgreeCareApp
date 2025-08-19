package com.example.agreecareapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CropDataManager {
    private static final String PREF_NAME = "CropPrefs";
    private static final String KEY_CROPS = "crops";
    private static List<Crop> crops = new ArrayList<>();
    private static SharedPreferences prefs;
    private static Gson gson = new Gson();

    public static void init(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String cropsJson = prefs.getString(KEY_CROPS, null);
        if (cropsJson != null) {
            Type cropListType = new TypeToken<List<Crop>>(){}.getType();
            crops = gson.fromJson(cropsJson, cropListType);
        }
        if (crops == null) {
            crops = new ArrayList<>();
        }
    }

    public static void addCrop(Crop crop) {
        crop.key = UUID.randomUUID().toString(); // Unique key
        crops.add(crop);
        saveCrops();
    }

    public static List<Crop> getCrops() {
        return new ArrayList<>(crops);
    }

    public static void removeCrop(String key) {
        crops.removeIf(crop -> crop.key != null && crop.key.equals(key));
        saveCrops();
    }

    private static void saveCrops() {
        SharedPreferences.Editor editor = prefs.edit();
        String cropsJson = gson.toJson(crops);
        editor.putString(KEY_CROPS, cropsJson);
        editor.apply();
    }
}
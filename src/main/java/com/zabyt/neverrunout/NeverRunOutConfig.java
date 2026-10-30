package com.zabyt.neverrunout;

import com.zabyt.lib.config.SimpleConfig;

import java.util.ArrayList;
import java.util.List;

public class NeverRunOutConfig extends SimpleConfig {

    public static final String MOD_ID = "neverrunout";

    public boolean enabled = true;


    public List<Integer> markedSlots = new ArrayList<>();

    public NeverRunOutConfig() {
        super(MOD_ID);
    }

    private static NeverRunOutConfig instance;

    public static NeverRunOutConfig get() {
        if (instance == null) {
            instance = SimpleConfig.load(MOD_ID, NeverRunOutConfig.class);
        }
        return instance;
    }

    public boolean isMarked(int hotbarSlot) {
        return markedSlots.contains(hotbarSlot);
    }

    public void toggleMark(int hotbarSlot) {
        if (!markedSlots.remove((Integer) hotbarSlot)) {
            markedSlots.add(hotbarSlot);
        }
        save();
    }

    public void clearMarks() {
        markedSlots.clear();
        save();
    }
}

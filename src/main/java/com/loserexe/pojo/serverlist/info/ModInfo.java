package com.loserexe.pojo.serverlist.info;

import com.loserexe.pojo.serverlist.mod.Mod;

import java.util.List;

public class ModInfo {
    private String type;
    private List<Mod> modList;

    public String getType() {
        return type;
    }

    public List<Mod> getModList() {
        return modList;
    }

    @Override
    public String toString() {
        return "ModInfo{" +
                "type='" + type + '\'' +
                ", modList=" + modList +
                '}';
    }
}

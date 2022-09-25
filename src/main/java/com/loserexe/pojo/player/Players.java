package com.loserexe.pojo.player;

import com.loserexe.pojo.player.Player;

import java.util.List;

public class Players {
    private int online;
    private int max;
    private List<Player> sample;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public List<Player> getSample() {
        return sample;
    }

    public void setSample(List<Player> sample) {
        this.sample = sample;
    }

    @Override
    public String toString() {
        return "Players{" +
                "max=" + max +
                ", online=" + online +
                ", sample=" + sample +
                '}';
    }
}

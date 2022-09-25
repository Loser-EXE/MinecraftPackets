package com.loserexe.pojo.serverlist.text;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Extra {
    private String color;
    private List<Extra> extra;
    @SerializedName("strikethrough")
    private boolean strikeThrough;
    private boolean bold;
    private String text;

    public boolean isBold() {
        return bold;
    }

    public String getColor() {
        return color;
    }

    public List<Extra> getExtra() {
        return extra;
    }

    public boolean isStrikeThrough() {
        return strikeThrough;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Extra{" +
                ((color != null) ? "color='" + color + '\'' : "") +
                ((strikeThrough) ? "strikethrough=true" : "") +
                ((bold) ? ", bold=true" : "") +
                ((text != null) ? ", text='" + text + '\'' : "") +
                ((extra != null) ? ", extra=" + extra : "") +
                '}';
    }
}

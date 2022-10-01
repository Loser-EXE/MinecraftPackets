package com.loserexe.pojo.serverlist.handshake.info;

import com.loserexe.pojo.serverlist.handshake.text.Extra;

import java.util.List;

public class Description {
    private final boolean fromString;
    private String text;
    private List<Extra> extra;


    public Description(String description, boolean fromString) {
        this.text = description;
        this.fromString = fromString;
    }

    public String getText() {
        return text;
    }

    public List<Extra> getExtra () {
        return extra;
    }

    @Override
    public String toString() {
        if (this.fromString) return text;

        return "Description{" +
                "extra='" + extra + '\'' +
                "text='" + text + '\'' +
                '}';
    }
}

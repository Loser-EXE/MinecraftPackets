package com.loserexe.pojo.serverlist.handshake.info;

public class Version {
    private int protocol;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "Version{" +
                "name='" + name + '\'' +
                ", protocol=" + protocol +
                '}';
    }
}

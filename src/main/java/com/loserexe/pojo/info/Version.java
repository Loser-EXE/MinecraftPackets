package com.loserexe.pojo.info;

public class Version {
    private String name;
    private int protocol;

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

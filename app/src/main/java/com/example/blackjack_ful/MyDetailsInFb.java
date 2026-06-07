package com.example.blackjack_ful;


public class MyDetailsInFb {
    private int chips; // כמות הגטונים של השחקן
    private String name; // שם השחקן

    // בנאי ריק - חובה עבור Firebase Realtime Database
    public MyDetailsInFb() {
    }

    public MyDetailsInFb(String name, int chips) {
        this.chips = chips;
        this.name = name;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyDetailsInFb{" +
                "chips=" + chips +
                ", name='" + name + '\'' +
                '}';
    }
}

package com.example.blackjack_ful;

public class MyDetailsInFb {
    private int chips;

    // בנאי ריק - חובה ל-Firebase
    public MyDetailsInFb() {
    }

    // בנאי עם ערך
    public MyDetailsInFb(int chips) {
        this.chips = chips;
    }

    // Getter ו-Setter - חובה ל-Firebase
    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    @Override
    public String toString() {
        return "MyDetailsInFb{" +
                "chips=" + chips +
                '}';
    }
}
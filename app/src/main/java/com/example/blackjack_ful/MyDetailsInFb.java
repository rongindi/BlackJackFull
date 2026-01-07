package com.example.blackjack_ful;

public class MyDetailsInFb {
    private int tokens;

    public MyDetailsInFb(int tokens) {
        this.tokens = tokens;

    }

    // MUST have the constructor  for the FireBase
    public MyDetailsInFb() {
    }

    // MUST generate getters and setters for the FireBase


    public int getPrice() {
        return tokens;
    }

    public void setPrice(int price) {
        this.tokens = tokens;
    }

    @Override
    public String toString() {
        return "MyDetailsInFb{" +

                ", tokens=" + tokens +
                '}';
    }
}

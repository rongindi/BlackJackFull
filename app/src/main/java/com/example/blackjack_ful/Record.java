package com.example.blackjack_ful;

/**
 * מחלקת מודל נוספת לייצוג רשומה (שיא).
 * מחלקה זו שימשה כנראה בגרסאות קודמות או כחלופה ל-MyDetailsInFb.
 */
public class Record {
    private String name;
    private int score;

    public Record(String name, int score) {
        this.name = name;
        this.score = score;
    }

    // בנאי ריק - חובה עבור Firebase
    public Record() {
    }

    // Getters ו-Setters חיוניים כדי ש-Firebase יוכל לקרוא ולכתוב את השדות
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

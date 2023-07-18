package com.example.quizapplication;

import java.io.Serializable;

public class Question implements Serializable {
    private String text;
    private boolean answer;
    private int color;

    public Question(String text, boolean answer) {
        this.text = text;
        this.answer = answer;
        this.color = 0; // Default color value
    }

    public Question(String text, boolean answer, int color) {
        this.text = text;
        this.answer = answer;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public boolean isAnswer() {
        return answer;
    }

    public int getColor() {
        return color;
    }
}
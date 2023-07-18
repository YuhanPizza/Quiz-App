package com.example.quizapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank {
    private List<Question> questionList;
    private int currentIndex;

    public QuestionBank() {
        questionList = new ArrayList<>();
        currentIndex = 0;
    }

    public void addQuestion(Question question) {
        questionList.add(question);
    }

    public void shuffleQuestions() {
        Collections.shuffle(questionList);
    }

    public Question getNextQuestion() {
        if (currentIndex < questionList.size()) {
            Question question = questionList.get(currentIndex);
            currentIndex++;
            return question;
        }
        return null;
    }

    public void reset() {
        currentIndex = 0;
    }
}

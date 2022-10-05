package com.paymentraiders.core;

import java.util.ArrayList;
import java.util.List;

public class QuizDetails {

    private List<QuestionDetails> questions;
    private int correctCount;
    private int incorrectCount;
    private int currentQuestion;
    private String currentAnswer;
    private boolean isDone;

    public QuizDetails() {
        this.questions = new ArrayList<QuestionDetails>(10);
        this.correctCount = 0;
        this.incorrectCount = 0;
        this.currentQuestion = 0;
        this.isDone = false;
    }

    public List<QuestionDetails> getQuestions() {
        return this.questions;
    }

    public void setQuestions(List<QuestionDetails> questions) {
        this.questions = questions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public void setIncorrectCount(int incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public int getIncorrectCount() {
        return this.incorrectCount;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentAnswer(String currentAnswer) {
        this.currentAnswer = currentAnswer;
    }

    public String getCurrentAnswer() {
        return this.currentAnswer;
    }

    public void markCorrect() {
        this.correctCount++;
        this.currentQuestion++;

        if (this.currentQuestion == questions.size()) {
            this.isDone = true;
        }
    }

    public void markIncorrect() {
        this.incorrectCount++;
        this.currentQuestion++;

        if (this.currentQuestion == questions.size()) {
            this.isDone = true;
        }
    }


}
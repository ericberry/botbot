package com.paymentraiders.core;

import java.util.List;

public class QuestionDetails {

    private String question;
    private List<String> answerOptions;
    private String correctAnswer;
    private Integer correctAnswerIndex;
    private String reference;


    public QuestionDetails(String question, List<String> answerOptions, String correctAnswer, Integer correctAnswerIndex, String reference) {
        this.question = question;
        this.answerOptions = answerOptions;
        this.correctAnswer = correctAnswer;
        this.correctAnswerIndex = correctAnswerIndex;
        this.reference = reference;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswerOptions() {
        return this.answerOptions;
    }

    public void setAnswerOptions(List<String> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Integer getCorrectAnswerIndex() {
        return this.correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(Integer correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getReference() {
        return this.reference;
    }

    public void setRefrence(String reference) {
        this.reference = reference;
    }

}
package com.example.tabitabi.model;

public enum Question {
	Question1("보물1호는?"),Question2("주 교통수단"),
	Question3("엄마 고향"),Question4("태어난곳");

    private final String questionText;

    Question(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionText() {
        return questionText;
    }
}

package org.example.domain;

public class TextField {
    private String text;

    public String getText() {
        return text;
    }

    public TextField setText(String text) {
        this.text = text;
        return this;
    }
}

package com.google.sps.data;

public class Comment {
    private final String name;
    private final String text;
    private final Long id;

    public Comment(String name, String text, Long id) {
        this.name = name;
        this.text = text;
        this.id = id;
    }

    String getName() {
        return this.name;
    }

    String getText() {
        return this.text;
    }

    Long getID() {
        return this.id;
    }
}
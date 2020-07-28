package com.google.sps.data;

import java.util.Date;

public class Comment {
    private final String name;
    private final String text;
    private final Long id;
    private final Date date;
    private final String email;

    public Comment(String name, String text, Long id, Date date, String email) {
        this.name = name;
        this.text = text;
        this.id = id;
        this.date = date;
        this.email = email;
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

    Date getDate() {
        return this.date;
    }
}

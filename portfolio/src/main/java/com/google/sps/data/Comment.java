package com.google.sps.data;

import java.util.Date;

public class Comment {
    private final String name;
    private final String text;
    private final Long id;
    private final Date date;

    public Comment(String name, String text, Long id, Date date) {
        this.name = name;
        this.text = text;
        this.id = id;
        this.date = date;
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

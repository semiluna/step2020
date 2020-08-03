package com.google.sps.data;

import java.util.Date;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Comment {
    public static Comment create(String name, String text, Date date, String email, Double score) {
        return new AutoValue_Comment(name, text, date, email, score);
    }

    abstract String name();
    abstract String text();
    abstract Date date();
    abstract String email();
    abstract Double score();
}

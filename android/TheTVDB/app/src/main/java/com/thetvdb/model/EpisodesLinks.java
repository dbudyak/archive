package com.thetvdb.model;

/**
 * Created by dbudyak on 27.06.16.
 */
public class EpisodesLinks extends Model {
    private String first = "";
    private String last = "";
    private String next = "";
    private String previous = "";


    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    @Override
    public String toString() {
        return "EpisodesLinks{" +
                "first=" + first +
                ", last=" + last +
                ", next=" + next +
                ", previous=" + previous +
                '}';
    }
}

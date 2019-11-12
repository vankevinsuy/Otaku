package com.wallaby.otaku.models;

public class Movie {
    private String name;
    private String moviePath;

    public Movie(String name, String moviePath) {
        this.name = name;
        this.moviePath = moviePath;
    }

    public String getName() {
        return name;
    }

    public String getMoviePath() {
        return moviePath;
    }
}

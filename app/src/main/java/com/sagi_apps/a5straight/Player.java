package com.sagi_apps.a5straight;

public class Player {

    private String name;
    private int score;
    private String idKeyPlayers;

    public Player() {
    }

    public Player(String name, int score, String idKeyPlayers) {
        this.name = name;
        this.score = score;
        this.idKeyPlayers = idKeyPlayers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getIdKeyPlayers() {
        return idKeyPlayers;
    }

    public void setIdKeyPlayers(String idKeyPlayers) {
        this.idKeyPlayers = idKeyPlayers;
    }
}

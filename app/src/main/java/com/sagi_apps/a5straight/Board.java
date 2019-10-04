package com.sagi_apps.a5straight;

/**
 * Created by User on 28/05/2018.
 */

public class Board {
    private int []arrXO;
    private boolean isTurnAdmin;
    private String nameAdmin;
    private String NameJoin;
    private String idKey;
    private boolean isActive;
    private int press;
    private boolean isFinishGame;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Board(int[] arrXO, boolean isTuenAdmin, String idKeyBoard) {
        this.arrXO = arrXO;
        this.isTurnAdmin = isTuenAdmin;
        this.nameAdmin = "";
        this.NameJoin = "";
        this.idKey = idKeyBoard;
        this.isFinishGame=false;
    }
    public Board(){}

    public int[] getArrXO() {
        return arrXO;
    }

    public void setArrXO(int[] arrXO) {
        this.arrXO = arrXO;
    }

    public boolean isFinishGame() {
        return isFinishGame;
    }

    public void setFinishGame(boolean finishGame) {
        isFinishGame = finishGame;
    }

    public boolean isTurnAdmin() {
        return isTurnAdmin;
    }

    public void setTurnAdmin(boolean turnAdmin) {
        isTurnAdmin = turnAdmin;
    }

    public String getNameAdmin() {
        return nameAdmin;
    }

    public void setNameAdmin(String nameAdmin) {
        this.nameAdmin = nameAdmin;
    }

    public String getNameJoin() {
        return NameJoin;
    }

    public void setNameJoin(String getNameJoin) {
        this.NameJoin = getNameJoin;
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public int getPress() {
        return press;
    }

    public void setPress(int press) {
        this.press = press;
    }
}

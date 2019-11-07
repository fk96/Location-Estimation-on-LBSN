package com.example.burakn.araproje;

public class Friendship {
    private int userid1, userid2;

    public Friendship(int userid1, int userid2) {
        this.userid1 = userid1;
        this.userid2 = userid2;
    }
    public int getUserid1() {
        return userid1;
    }

    public void setUserid1(int userid1) {
        this.userid1 = userid1;
    }
    public int getUserid2() {
        return userid2;
    }

    public void setUserid2(int userid2) {
        this.userid2 = userid2;
    }
}

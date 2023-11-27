package com.example.ads_pbl;

public class Codekey {
    String codeword;
    int key;
    public Codekey(){}
    Codekey(String codeword,int key) {
        this.codeword=codeword;
        this.key=key;

    }
    public String getCodeword() {
        return codeword;
    }public int getKey() {
        return key;
    }
    public void setCodeword(String codeword) {
        this.codeword = codeword;
    }public void setKey(int key) {
        this.key = key;
    }
}

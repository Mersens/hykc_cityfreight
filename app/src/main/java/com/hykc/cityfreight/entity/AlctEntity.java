package com.hykc.cityfreight.entity;

public class AlctEntity {
    private int id;
    private String corporateName;
    private String alctCode;
    private String alctSecret;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getAlctCode() {
        return alctCode;
    }

    public void setAlctCode(String alctCode) {
        this.alctCode = alctCode;
    }

    public String getAlctSecret() {
        return alctSecret;
    }

    public void setAlctSecret(String alctSecret) {
        this.alctSecret = alctSecret;
    }

    public String getAlctKey() {
        return alctKey;
    }

    public void setAlctKey(String alctKey) {
        this.alctKey = alctKey;
    }

    private String alctKey;
}

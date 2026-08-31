package com.zmc.ierbar_web_app.models.user;

import java.time.LocalDateTime;

public class Conversatie {
    private int id;
    private int user_id;
    private String titlu;
    private LocalDateTime data_crearii;

    public Conversatie() {}

    public Conversatie(LocalDateTime data_crearii, int id, String titlu, int user_id) {
        this.data_crearii = data_crearii;
        this.id = id;
        this.titlu = titlu;
        this.user_id = user_id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }

    public LocalDateTime getData_crearii() { return data_crearii; }
    public void setData_crearii(LocalDateTime data_crearii) { this.data_crearii = data_crearii; }
}
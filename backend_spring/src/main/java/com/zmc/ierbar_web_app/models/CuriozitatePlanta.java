package com.zmc.ierbar_web_app.models;

import java.time.LocalDate;

public class CuriozitatePlanta {
    private int id;
    private int plantaId;
    private String numePlanta;
    private String titlu;
    private String curiozitate;
    private String iconita;
    private LocalDate dataGenerare;

    // Getters și Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlantaId() { return plantaId; }
    public void setPlantaId(int plantaId) { this.plantaId = plantaId; }

    public String getNumePlanta() { return numePlanta; }
    public void setNumePlanta(String numePlanta) { this.numePlanta = numePlanta; }

    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }

    public String getCuriozitate() { return curiozitate; }
    public void setCuriozitate(String curiozitate) { this.curiozitate = curiozitate; }

    public LocalDate getDataGenerare() { return dataGenerare; }
    public void setDataGenerare(LocalDate dataGenerare) { this.dataGenerare = dataGenerare; }

    public String getIconita() {return iconita;}
    public void setIconita(String iconita) {this.iconita = iconita;}
}
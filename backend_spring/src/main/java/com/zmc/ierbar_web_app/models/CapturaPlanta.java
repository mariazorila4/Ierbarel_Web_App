package com.zmc.ierbar_web_app.models;

import java.time.LocalDateTime;

import com.zmc.ierbar_web_app.models.simple_factory.Planta;

public class CapturaPlanta {
    private int id;
    private int plantaId;
    private int userId;
    private String numeUtilizator; 
    private String imagineUrl;
    private String locatie;
    private LocalDateTime dataAdaugarii;
    private Planta planta;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlantaId() { return plantaId; }
    public void setPlantaId(int plantaId) { this.plantaId = plantaId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNumeUtilizator() { return numeUtilizator; }
    public void setNumeUtilizator(String numeUtilizator) { this.numeUtilizator = numeUtilizator; }

    public String getImagineUrl() { return imagineUrl; }
    public void setImagineUrl(String imagineUrl) { this.imagineUrl = imagineUrl; }

    public String getLocatie() { return locatie; }
    public void setLocatie(String locatie) { this.locatie = locatie; }

    public LocalDateTime getDataAdaugarii() { return dataAdaugarii; }
    public void setDataAdaugarii(LocalDateTime dataAdaugarii) { this.dataAdaugarii = dataAdaugarii; }

    public Planta getPlanta() {return planta;}
    public void setPlanta(Planta planta) {this.planta = planta;}
}
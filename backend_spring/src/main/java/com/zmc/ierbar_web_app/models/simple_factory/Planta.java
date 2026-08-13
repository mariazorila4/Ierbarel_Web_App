package com.zmc.ierbar_web_app.models.simple_factory;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;

@Table("plante")
public abstract class Planta{
    @Id
    @Column("id")
    protected int id;

    @Column("nume_uzual")
    protected String nume_uzual;

    @Column("denumire_stiintifica")
    protected String denumire_stiintifica;

    @Column("familie")
    protected String familie;

    @Column("descriere")
    protected String descriere;

    @Column("inaltime_maxima")
    protected float inaltime_maxima;

    @Column("perioada_inflorire")
    protected String perioada_inflorire;

    @Column("ciclu_de_viata")
    protected String ciclu_de_viata;

    @Column("poate_fi_uscata")
    private boolean poate_fi_uscata;

    @Column("tip_planta")
    protected TipPlanta tip_planta;

    @Column("categorie_planta") 
    protected CategoriePlanta categorie_planta;

    @Column("imagine_url")
    protected String imagine_url;

    public Planta() {
    }

    public Planta(int id, String nume_uzual, String denumire_stiintifica, String familie, String descriere, float inaltime_maxima, String perioada_inflorire, boolean poate_fi_uscata, String ciclu_de_viata, TipPlanta tip_planta, String imagine_url, CategoriePlanta categorie_planta) {
        this.id = id;
        this.nume_uzual = nume_uzual;
        this.denumire_stiintifica = denumire_stiintifica;
        this.familie = familie;
        this.descriere = descriere;
        this.inaltime_maxima = inaltime_maxima;
        this.perioada_inflorire = perioada_inflorire;
        this.poate_fi_uscata = poate_fi_uscata;
        this.ciclu_de_viata = ciclu_de_viata;
        this.tip_planta = tip_planta;
        this.imagine_url=imagine_url;
        this.categorie_planta = categorie_planta;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume_uzual() {
        return nume_uzual;
    }

    public void setNume_uzual(String nume_uzual) {
        this.nume_uzual = nume_uzual;
    }

    public String getDenumire_stiintifica() {
        return denumire_stiintifica;
    }

    public void setDenumire_stiintifica(String denumire_stiintifica) {
        this.denumire_stiintifica = denumire_stiintifica;
    }

    public String getFamilie() {
        return familie;
    }

    public void setFamilie(String familie) {
        this.familie = familie;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public float getInaltime_maxima() {
        return inaltime_maxima;
    }

    public void setInaltime_maxima(float inaltime_maxima) {
        this.inaltime_maxima = inaltime_maxima;
    }

    public String getPerioada_inflorire() {
        return perioada_inflorire;
    }

    public void setPerioada_inflorire(String perioada_inflorire) {
        this.perioada_inflorire = perioada_inflorire;
    }

    public boolean isPoate_fi_uscata() {
        return poate_fi_uscata;
    }

    public void setPoate_fi_uscata(boolean poate_fi_uscata) {
        this.poate_fi_uscata = poate_fi_uscata;
    }

    public String getCiclu_de_viata() {
        return ciclu_de_viata;
    }

    public void setCiclu_de_viata(String ciclu_de_viata){
        this.ciclu_de_viata=ciclu_de_viata;
    }

    public TipPlanta getTip_planta() {
        return tip_planta;
    }

    public void setTip_planta(TipPlanta tip_planta) {
        this.tip_planta = tip_planta;
    }

    public CategoriePlanta getCategorie_planta() {
        return categorie_planta;
    }

    public void setCategorie_planta(CategoriePlanta categorie_planta) {
        this.categorie_planta = categorie_planta;
    }

    @java.lang.Override
    public java.lang.String toString() {
        final java.lang.StringBuilder sb = new java.lang.StringBuilder("Planta{");
        sb.append("id=").append(id);
        sb.append(", nume_uzual='").append(nume_uzual).append('\'');
        sb.append(", denumire_stiintifica='").append(denumire_stiintifica).append('\'');
        sb.append(", familie='").append(familie).append('\'');
        sb.append(", descriere='").append(descriere).append('\'');
        sb.append(", inaltime_maxima=").append(inaltime_maxima);
        sb.append(", perioada_inflorire='").append(perioada_inflorire).append('\'');
        sb.append(", poate_fi_uscata=").append(isPoate_fi_uscata());
        sb.append(", ciclu_de_viata='").append(ciclu_de_viata).append('\'');
        sb.append(", tip_planta=").append(tip_planta);
        sb.append(", imagine_url").append(imagine_url);
        sb.append(", categorie_planta=").append(categorie_planta);
        sb.append('}');
        return sb.toString();
    }

    public String getImagine_url() {
        return imagine_url;
    }

    public void setImagine_url(String imagine_url) {
        this.imagine_url = imagine_url;
    }
}

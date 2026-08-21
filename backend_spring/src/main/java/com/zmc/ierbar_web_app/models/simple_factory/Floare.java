package com.zmc.ierbar_web_app.models.simple_factory;

import org.springframework.data.relational.core.mapping.Column;

import com.zmc.ierbar_web_app.models.PreparabilPentruUscare;
import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;

public class Floare extends Planta implements PreparabilPentruUscare {
    @Column("numar_petale")
    private int numar_petale;

    @Column("culoare")
    private String culoare;

    public Floare() {
        super();
    }

    public Floare(int id, String nume_uzual, String denumire_stiintifica, String familie, String descriere,
         float inaltime_maxima, String perioada_inflorire, boolean poate_fi_uscata, String ciclu_de_viata,
         TipPlanta tip_planta, String locatie, String imagine_url, CategoriePlanta categorie_planta, int numar_petale, String culoare) {
        super(id, nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima,
             perioada_inflorire, poate_fi_uscata, ciclu_de_viata, tip_planta, locatie, imagine_url, categorie_planta);
        this.numar_petale = numar_petale;
        this.culoare = culoare;
    }

    public int getNumar_petale() {
        return numar_petale;
    }

    public void setNumar_petale(int numar_petale) {
        this.numar_petale = numar_petale;
    }

    public String getCuloare() {
        return culoare;
    }

    public void setCuloare(String culoare) {
        this.culoare = culoare;
    }

    @Override
    public String genereazaPromptUscare(String numePlanta) {
        return "Acționează ca un expert în botanică. Generează instrucțiuni scurte și la obiect despre cum se presează și se usucă floarea numită '" + numePlanta + "' pentru un ierbar. " +
               "Te rog să ții cont de specificul petalelor ei (dacă sunt groase sau subțiri) și dă un sfat despre cum i se pot păstra culorile vii."; 
    }

    @java.lang.Override
    public java.lang.String toString() {
        final java.lang.StringBuilder sb = new java.lang.StringBuilder("Floare{");
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
        sb.append(", categorie_planta=").append(categorie_planta);
        sb.append(", numar_petale=").append(numar_petale);
        sb.append(", culoare='").append(culoare).append('\'');
        sb.append('}');
        return sb.toString();
    }

    
}

package com.zmc.ierbar_web_app.models.simple_factory;

import org.springframework.data.relational.core.mapping.Column;

import com.zmc.ierbar_web_app.models.PreparabilPentruUscare;
import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;

public class Ierburi extends Planta implements PreparabilPentruUscare {
    @Column("tip_tulpina")
    private String tip_tulpina;

    public Ierburi() {
        super();
    }

    public Ierburi(int id, String nume_uzual, String denumire_stiintifica, String familie, String descriere,
        float inaltime_maxima, String perioada_inflorire, boolean poate_fi_uscata, String ciclu_de_viata,
        TipPlanta tip_planta, String locatie, String imagine_url, CategoriePlanta categorie_planta, String tip_tulpina) {
        super(id, nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima,
            perioada_inflorire, poate_fi_uscata, ciclu_de_viata, tip_planta, locatie, imagine_url, categorie_planta);
        this.tip_tulpina = tip_tulpina;
    }

    public String getTip_tulpina() {
        return tip_tulpina;
    }   

    public void setTip_tulpina(String tip_tulpina) {
        this.tip_tulpina = tip_tulpina;
    }

    @java.lang.Override
    public java.lang.String toString() {
        final java.lang.StringBuilder sb = new java.lang.StringBuilder("Ierburi{");
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
        sb.append(", tip_tulpina='").append(tip_tulpina).append('\'');
        
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String genereazaPromptUscare(String numePlanta) {
        return "Explică pe scurt cum se usucă corect planta aromatică/medicinală '" + numePlanta + "'. " +
               "Concentrează-te pe metoda uscării la aer (în buchete atârnate) și menționează cum se previne mucegaiul pentru această specie specifică.";
    }
}

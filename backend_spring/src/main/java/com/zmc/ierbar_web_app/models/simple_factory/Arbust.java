package com.zmc.ierbar_web_app.models.simple_factory;
import org.springframework.data.relational.core.mapping.Column;
import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.PreparabilPentruUscare;

public class Arbust extends Planta implements PreparabilPentruUscare{
    @Column("produce_fructe")    
    private boolean produce_fructe;

    public Arbust() {
        super();
    }

    public Arbust(int id, String nume_uzual, String denumire_stiintifica, String familie, String descriere,
         float inaltime_maxima, String perioada_inflorire, boolean poate_fi_uscata, String ciclu_de_viata, 
         TipPlanta tip_planta, CategoriePlanta categorie_planta, boolean produce_fructe) {
        super(id, nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima, 
            perioada_inflorire, poate_fi_uscata, ciclu_de_viata, tip_planta, categorie_planta);
        this.produce_fructe = produce_fructe;
    }

    public boolean isProduce_fructe() {
        return produce_fructe;
    }

    public void setProduce_fructe(boolean produce_fructe) {
        this.produce_fructe = produce_fructe;
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
        sb.append(", categorie_planta=").append(categorie_planta);
        sb.append(", produce_fructe=").append(isProduce_fructe());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String genereazaPromptUscare(String numePlanta) {
        return "Acționează ca un expert în botanică. Utilizatorul vrea să preseze o mostră din arbustul '" + numePlanta + "'. " +
               "Generează instrucțiuni clare despre cum se taie și se presează o crenguță subțire cu frunze sau flori de la această specie. " +
               "Dacă specia are spini sau părți lemnoase proeminente, explică-i utilizatorului cum să le manipuleze sau să le secționeze pe jumătate înainte de a le pune sub presa de ierbar.";
    }
}

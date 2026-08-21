package com.zmc.ierbar_web_app.models.simple_factory;
import org.springframework.data.relational.core.mapping.Column;
import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.PreparabilPentruUscare;

public class Arbore extends Planta implements PreparabilPentruUscare{
    @Column("tip_coroana")
    private String tip_coroana;

    @Column("tip_frunza")
    private String tip_frunza;

    @Column("pom_fructifer")
    private boolean pom_fructifer;

    public Arbore() {
        super();
    }

    public Arbore(int id, String nume_uzual, String denumire_stiintifica, String familie, String descriere,
         float inaltime_maxima, String perioada_inflorire, boolean poate_fi_uscata, String ciclu_de_viata,
          TipPlanta tip_planta, String locatie, String imagine_url, CategoriePlanta categorie_planta, String tip_coroana, String tip_frunza, boolean pom_fructifer) {
        super(id, nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima, 
            perioada_inflorire, poate_fi_uscata, ciclu_de_viata, tip_planta, locatie, imagine_url, categorie_planta);
        this.tip_coroana = tip_coroana;
        this.tip_frunza = tip_frunza;
        this.pom_fructifer = pom_fructifer;
    }

    public String getTip_coroana() {
        return tip_coroana;
    }

    public void setTip_coroana(String tip_coroana) {
        this.tip_coroana = tip_coroana;
    }

    public String getTip_frunza() {
        return tip_frunza;
    }

    public void setTip_frunza(String tip_frunza) {
        this.tip_frunza = tip_frunza;
    }

    public boolean isPom_fructifer() {
        return pom_fructifer;
    }

    public void setPom_fructifer(boolean pom_fructifer) {
        this.pom_fructifer = pom_fructifer;
    }

    @java.lang.Override
    public java.lang.String toString() {
        final java.lang.StringBuilder sb = new java.lang.StringBuilder("Arbore{");
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
        sb.append(", tip_coroana='").append(tip_coroana).append('\'');
        sb.append(", tip_frunza='").append(tip_frunza).append('\'');
        sb.append(", pom_fructifer=").append(pom_fructifer);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String genereazaPromptUscare(String numePlanta) {
        return "Acționează ca un expert în botanică. Utilizatorul vrea să adauge arborele '" + numePlanta + "' în ierbarul său fizic. " +
               "Generează instrucțiuni scurte despre cum se presează corect frunzele sau florile acestui arbore. " +
               "Oferă un sfat tehnic despre cum trebuie așezate frunzele cu nervuri groase pentru a se usca perfect plat și a nu prinde mucegai sub presă.";
    }
}

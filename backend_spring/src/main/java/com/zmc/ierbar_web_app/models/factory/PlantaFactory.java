package com.zmc.ierbar_web_app.models.factory;
import com.zmc.ierbar_web_app.models.simple_factory.Arbore;
import com.zmc.ierbar_web_app.models.simple_factory.Arbust;
import com.zmc.ierbar_web_app.models.simple_factory.Floare;
import com.zmc.ierbar_web_app.models.simple_factory.Ierburi;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;

public class PlantaFactory {
    public Planta creazaPlanta(CategoriePlanta categorie_planta, 
                                      int id, String nume_uzual, String denumire_stiintifica, 
                                      String familie, String descriere, float inaltime_maxima, 
                                      String perioada_inflorire, String ciclu_de_viata, TipPlanta tip_planta, String locatie, String imagine_url, int numar_petale,
                                      String culoare, String tip_coroana, String tip_frunza, boolean pom_fructifer,
                                      boolean produce_fructe, String tip_tulpina, boolean poate_fi_uscata) {
        
        switch (categorie_planta) {
            case FLOARE:
                return new Floare(id, nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima, perioada_inflorire,
                                poate_fi_uscata, ciclu_de_viata, tip_planta, locatie, imagine_url, categorie_planta, numar_petale, culoare);

            
            case ARBORE:
                return new Arbore(id, nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima, perioada_inflorire, 
                                 poate_fi_uscata, ciclu_de_viata, tip_planta, locatie, imagine_url, categorie_planta, tip_coroana, tip_frunza, pom_fructifer);
            
            case ARBUST:
                return new Arbust(id, nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima, perioada_inflorire, 
                    poate_fi_uscata, ciclu_de_viata, tip_planta, locatie, imagine_url, categorie_planta, produce_fructe);
            
            case IERBURI:
                return new Ierburi(id, nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima, perioada_inflorire, 
                    poate_fi_uscata, ciclu_de_viata, tip_planta, locatie, imagine_url, categorie_planta, tip_tulpina);
            
            default:
                throw new IllegalArgumentException("Categoria de plantă nu este suportată: " + categorie_planta);
        }
    }
}
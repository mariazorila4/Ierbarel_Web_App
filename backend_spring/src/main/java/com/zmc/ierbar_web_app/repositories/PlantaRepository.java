package com.zmc.ierbar_web_app.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.*;
import java.util.List;

@Repository
public class PlantaRepository{
    private final JdbcTemplate jdbcTemplate;

    public PlantaRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    public List<Planta> extrageToatePlantele(){
        String sqlPlante="SELECT * FROM plante";

        return jdbcTemplate.query(sqlPlante, (rs, rand)->{
            CategoriePlanta categoriePlanta= CategoriePlanta.valueOf(rs.getString("categorie"));
            int idPlanta=rs.getInt("id");
            String numeUzual=rs.getString("nume_uzual");
            String numeStiintific=rs.getString("denumire_stiintifica");
            String familie=rs.getString("familie");
            String descriere=rs.getString("descriere");
            float inaltimeMaxima=rs.getFloat("inaltime_maxima");
            String perioadaInflorire=rs.getString("perioada_inflorire");
            String cicluDeViata=rs.getString("ciclu_de_viata");
            boolean poateFiUscata=rs.getBoolean("poate_fi_uscata");
            TipPlanta tipPlanta=TipPlanta.valueOf(rs.getString("tip_planta"));
            int nrPetale=rs.getInt("numar_petale");
            String culoare=rs.getString("culoare");
            String tipCoroana=rs.getString("tip_coroana");
            String tipFrunza=rs.getString("tip_frunza");
            boolean pomFructifer=rs.getBoolean("pom_fructifer");
            boolean produceFructe=rs.getBoolean("produce_fructe");
            String tipTulpina=rs.getString("tip_tulpina");

            PlantaFactory plantaFactory = new PlantaFactory();
            Planta p = plantaFactory.creazaPlanta(categoriePlanta, idPlanta, numeUzual, numeStiintific,
                familie, descriere, inaltimeMaxima, perioadaInflorire, cicluDeViata, tipPlanta,
                nrPetale, culoare, tipCoroana, tipFrunza, pomFructifer, produceFructe, tipTulpina, poateFiUscata);

            p.setId(idPlanta);
            p.setNume_uzual(numeUzual);
            p.setDenumire_stiintifica(numeStiintific);
            p.setFamilie(familie);
            p.setDescriere(descriere);
            p.setCategorie_planta(categoriePlanta);
            p.setInaltime_maxima(inaltimeMaxima);
            p.setPerioada_inflorire(perioadaInflorire);
            p.setCiclu_de_viata(cicluDeViata);
            p.setTip_planta(tipPlanta);

            return p;
        });
    }

    public void salveazaPlantaNoua(Planta planta, int idAdmin){
        String sql="INSERT INTO plante (nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima, "+
                    "perioada_inflorire, poate_fi_uscata, ciclu_de_viata, tip_planta, categorie_planta, numar_petale, "+
                    "culoare, tip_coroana, tip_frunza, pom_fructife, produce_fructe, tip_tulpina, adaugat_de_admin_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String categoriePlanta=planta.getClass().getSimpleName().toUpperCase();
        jdbcTemplate.update(sql, planta.getNume_uzual(), planta.getDenumire_stiintifica(), planta.getFamilie(), planta.getDescriere(),
                    planta.getInaltime_maxima(), planta.getPerioada_inflorire(), planta.isPoate_fi_uscata(), planta.getCiclu_de_viata(),
                    planta.getTip_planta(), categoriePlanta, idAdmin);
    }
}
package com.zmc.ierbar_web_app.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;

@Repository
public class PlantaRepository{
    private final JdbcTemplate jdbcTemplate;

    public PlantaRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    public List<Planta> extrageToatePlantele(){
        String sqlPlante="SELECT * FROM plante";

        return jdbcTemplate.query(sqlPlante, (rs, rand)->{
            CategoriePlanta categoriePlanta= CategoriePlanta.valueOf(rs.getString("categorie_planta"));
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
            String locatie=rs.getString("locatie");
            String imagineUrl=rs.getString("imagine_url");
            int nrPetale=rs.getInt("numar_petale");
            String culoare=rs.getString("culoare");
            String tipCoroana=rs.getString("tip_coroana");
            String tipFrunza=rs.getString("tip_frunza");
            boolean pomFructifer=rs.getBoolean("pom_fructifer");
            boolean produceFructe=rs.getBoolean("produce_fructe");
            String tipTulpina=rs.getString("tip_tulpina");

            PlantaFactory plantaFactory = new PlantaFactory();
            Planta p = plantaFactory.creazaPlanta(categoriePlanta, idPlanta, numeUzual, numeStiintific,
                familie, descriere, inaltimeMaxima, perioadaInflorire, cicluDeViata, tipPlanta, locatie, imagineUrl,
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
            p.setLocatie(locatie);
            p.setImagine_url(imagineUrl);

            return p;
        });
    }

    public void salveazaPlantaNoua(Planta planta, int adminId, String imagineUrl, int numarPetale, 
                                   String culoare, String tipCoroana, String tipFrunza, 
                                   boolean pomFructifer, boolean produceFructe, String tipTulpina) {
        
        String sql = "INSERT INTO plante (nume_uzual, denumire_stiintifica, familie, descriere, inaltime_maxima, " +
                     "perioada_inflorire, poate_fi_uscata, ciclu_de_viata, tip_planta, locatie, categorie_planta, " +
                     "numar_petale, culoare, tip_coroana, tip_frunza, pom_fructifer, produce_fructe, tip_tulpina, " +
                     "admin_plant_id, imagine_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS tip_planta), ?, CAST(? AS categorie_planta), ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        String categoriePlanta = planta.getClass().getSimpleName().toUpperCase();
        
        jdbcTemplate.update(sql, 
            planta.getNume_uzual(), 
            planta.getDenumire_stiintifica(), 
            planta.getFamilie(), 
            planta.getDescriere(),
            planta.getInaltime_maxima(), 
            planta.getPerioada_inflorire(), 
            planta.isPoate_fi_uscata(), 
            planta.getCiclu_de_viata(), 
            planta.getTip_planta().name(), 
            planta.getLocatie(), 
            categoriePlanta, 
            numarPetale, 
            culoare, 
            tipCoroana, 
            tipFrunza, 
            pomFructifer, 
            produceFructe, 
            tipTulpina, 
            adminId, 
            imagineUrl
        );
    }

    public void adaugaInIerbar(int userId, int plantaId){
        String sql="INSERT INTO plante_favorite (user_id, planta_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, userId, plantaId);
    }

    public void stergeDinIerbar(int userId, int plantaId){
        String sql="DELETE FROM plante_favorite WHERE user_id=? AND planta_id=?";
        jdbcTemplate.update(sql, userId, plantaId);
    }

    public void stergePlantaDefinitiv(int idPlanta){
        // 1. Ștergem întâi toate referințele plantei din ierbarul personal al utilizatorilor
        String sqlFavorite="DELETE FROM plante_favorite WHERE planta_id=?";
        jdbcTemplate.update(sqlFavorite, idPlanta);

        // 2. Ștergem planta din tabelul principal
        String sql="DELETE FROM plante WHERE id=?";
        jdbcTemplate.update(sql, idPlanta);
    }

    public void actualizeazaPlanta(int idPlanta, Planta planta, String locatie, String imagineUrl, int numarPetale, 
                                   String culoare, String tipCoroana, String tipFrunza, 
                                   boolean pomFructifer, boolean produceFructe, String tipTulpina) {
        
        String sql = "UPDATE plante SET nume_uzual=?, denumire_stiintifica=?, familie=?, descriere=?, inaltime_maxima=?, " +
                     "perioada_inflorire=?, poate_fi_uscata=?, ciclu_de_viata=?, tip_planta=CAST(? AS tip_planta), " +
                     "numar_petale=?, culoare=?, tip_coroana=?, tip_frunza=?, pom_fructifer=?, produce_fructe=?, tip_tulpina=?, " +
                     "categorie_planta=CAST(? AS categorie_planta), locatie=?, imagine_url=? WHERE id=?";

        String categoriePlanta = planta.getClass().getSimpleName().toUpperCase();
        
        jdbcTemplate.update(sql, 
            planta.getNume_uzual(), 
            planta.getDenumire_stiintifica(), 
            planta.getFamilie(), 
            planta.getDescriere(),
            planta.getInaltime_maxima(), 
            planta.getPerioada_inflorire(), 
            planta.isPoate_fi_uscata(), 
            planta.getCiclu_de_viata(), 
            planta.getTip_planta().name(), 
            numarPetale, 
            culoare, 
            tipCoroana, 
            tipFrunza, 
            pomFructifer, 
            produceFructe, 
            tipTulpina, 
            categoriePlanta, 
            planta.getLocatie(), 
            imagineUrl,
            idPlanta
        );
    }
}
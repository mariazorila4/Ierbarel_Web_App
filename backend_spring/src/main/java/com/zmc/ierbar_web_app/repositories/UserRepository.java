package com.zmc.ierbar_web_app.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;
import com.zmc.ierbar_web_app.models.user.Admin;
import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.models.user.MesajChat;
import com.zmc.ierbar_web_app.models.user.TipUser;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public General cautaUserDupaEmail(String email){
        String sqlUser="SELECT * FROM users WHERE email=?";

        try{
            return jdbcTemplate.queryForObject(sqlUser, (rs, rand)->{
                General g=new General();

                g.setId(rs.getInt("id"));
                g.setUsername(rs.getString("username"));
                g.setEmail(rs.getString("email"));
                g.setPassword(rs.getString("password"));
                g.setStatus(rs.getString("status"));
                
                try {
                    g.setImagine_url(rs.getString("imagine_url"));
                } catch (Exception ignored) {}

                if(rs.getString("tip_user") != null) {
                    g.setTip_user(TipUser.valueOf(rs.getString("tip_user")));
                }

                return g;
            }, email);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public void salveazaUserNou(String username, String email, String parolaCriptata, TipUser tipUser){
        String sql="INSERT INTO users (username, password, email, tip_user) VALUES (?, ?, ?, ?::tip_user)";
        jdbcTemplate.update(sql, username, parolaCriptata, email, tipUser.name());
    }

    public General extrageProfilGeneral(int idUser){
        String sqlUser="SELECT * FROM users WHERE id=? and tip_user='GENERAL'";

        General generalUser=jdbcTemplate.queryForObject(sqlUser, (rs, rand)->{
            General g=new General();
            g.setId(rs.getInt("id"));
            g.setUsername(rs.getString("username"));
            g.setPassword(rs.getString("password"));
            g.setEmail(rs.getString("email"));
            
            try {
                g.setImagine_url(rs.getString("imagine_url"));
            } catch (Exception ignored) {}

            return g;
        }, idUser);

        if(generalUser==null){
            return null;
        }

        String sqlChat="SELECT * FROM istoric_chat WHERE user_id=? ORDER BY data_trimiterii ASC";

        List<MesajChat> istoricExtras=jdbcTemplate.query(sqlChat, (rs, rand)->{
            MesajChat messaj=new MesajChat();
            messaj.setId(rs.getInt("id"));
            messaj.setMesaj(rs.getString("mesaj"));
            messaj.setEste_bot(rs.getBoolean("este_bot"));

            if(rs.getTimestamp("data_trimiterii")!=null){
               messaj.setData_trimiterii(rs.getTimestamp("data_trimiterii").toLocalDateTime()); 
            }
            
            return messaj;
        }, idUser);

        generalUser.setIstoricChatAI(istoricExtras);

        String sqlPlante = "SELECT p.* FROM plante p " +
                   "INNER JOIN plante_favorite pf ON p.id = pf.planta_id " +
                   "WHERE pf.user_id = ?";

        List<Planta> planteFavoriteExtrase=jdbcTemplate.query(sqlPlante, (rs, rand)->{
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
        }, idUser);

        generalUser.setPlanteFavorite(planteFavoriteExtrase);

        return generalUser;
    }

    public Admin extrageProfilAdmin(int idUser){
        String sqlUserAdm="SELECT * FROM users WHERE id=? AND tip_user='ADMIN'";
        
        Admin admin=jdbcTemplate.queryForObject(sqlUserAdm, (rs, rand)->{
            Admin a=new Admin();

            a.setId(rs.getInt("id"));
            a.setUsername(rs.getString("username"));
            a.setPassword(rs.getString("password"));
            a.setEmail(rs.getString("email"));

            return a;
        }, idUser);

        String sqlPlanteIerbar="SELECT * FROM plante WHERE admin_plant_id=?";

        List<Planta> planteIerbarOnline=jdbcTemplate.query(sqlPlanteIerbar, (rs, rand)->{
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

        }, idUser);

        admin.setPlanteIerbarOnline(planteIerbarOnline);

        return admin;
    }

    public List<General> extrageTotiUtilizatorii(){
        String sql="SELECT id, username, email, status FROM users WHERE tip_user='GENERAL'";

        return jdbcTemplate.query(sql, (rs, rand)->{
            General g=new General();
            g.setId(rs.getInt("id"));
            g.setUsername(rs.getString("username"));
            g.setEmail(rs.getString("email"));
            g.setStatus(rs.getString("status"));

            return g;
        });
    }

    public void stergeUser(int id){
        String sql="DELETE FROM users WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    public void schimbaStatusUtilizator(int id, String newStatus){
        String sql="UPDATE users SET status=? WHERE id=?";
        jdbcTemplate.update(sql, newStatus, id);
    }

    public General cautaUserDupaUsername(String username) {
        String sqlUser="SELECT * FROM users WHERE username=?";

        try{
            return jdbcTemplate.queryForObject(sqlUser, (rs, rand)->{
                General g=new General();

                g.setId(rs.getInt("id"));
                g.setUsername(rs.getString("username"));
                g.setEmail(rs.getString("email"));
                g.setPassword(rs.getString("password"));
                g.setStatus(rs.getString("status"));
                
                try {
                    g.setImagine_url(rs.getString("imagine_url"));
                } catch (Exception ignored) {}

                if(rs.getString("tip_user") != null) {
                    g.setTip_user(TipUser.valueOf(rs.getString("tip_user")));
                }

                return g;
            }, username);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    // ==========================================
    // 👤 GESTIONARE PROFIL (CU SUPORT IMAGINE_PROFIL)
    // ==========================================

    public void actualizeazaProfil(int userId, String username, String email, String imagineUrl) {
        String sql = "UPDATE users SET username = ?, email = ?, imagine_url = ? WHERE id = ?";
        jdbcTemplate.update(sql, username, email, imagineUrl, userId);
    }

    public void actualizeazaParola(int userId, String parolaEncodata) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, parolaEncodata, userId);
    }

    public Map<String, Object> extrageStatisticiSistem() {
        // Luăm în calcul utilizatorii GENERAL care au status 'ACTIV' (indiferent de majuscule) sau status necompletat (NULL)
        String sqlUseriActivi = "SELECT COUNT(*) FROM users WHERE tip_user::text = 'GENERAL' AND (status IS NULL OR UPPER(status) = 'ACTIV')";
        String sqlPlanteIdentificate = "SELECT COUNT(*) FROM plante_favorite";
        String sqlSpeciiTotale = "SELECT COUNT(*) FROM plante";

        Integer utilizatoriActivi = jdbcTemplate.queryForObject(sqlUseriActivi, Integer.class);
        Integer planteIdentificate = jdbcTemplate.queryForObject(sqlPlanteIdentificate, Integer.class);
        Integer speciiBazaDeDate = jdbcTemplate.queryForObject(sqlSpeciiTotale, Integer.class);

        return Map.of(
            "utilizatoriActivi", utilizatoriActivi != null ? utilizatoriActivi : 0,
            "planteIdentificate", planteIdentificate != null ? planteIdentificate : 0,
            "speciiBazaDeDate", speciiBazaDeDate != null ? speciiBazaDeDate : 0,
            "eroriServerAstazi", 0
        );
    }
}
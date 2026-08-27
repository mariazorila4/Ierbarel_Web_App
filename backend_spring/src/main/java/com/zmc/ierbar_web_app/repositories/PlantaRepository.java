package com.zmc.ierbar_web_app.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zmc.ierbar_web_app.models.CapturaPlanta;
import com.zmc.ierbar_web_app.models.CuriozitatePlanta;
import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;

@Repository
public class PlantaRepository {
    private final JdbcTemplate jdbcTemplate;

    public PlantaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // METODĂ HELPER: Previne duplicarea codului uriaș de mapare
    private Planta mapeazaRandPlanta(ResultSet rs) throws SQLException {
        String catStr = rs.getString("categorie_planta");
        CategoriePlanta categorie = CategoriePlanta.FLOARE;
        if (catStr != null && !catStr.isBlank()) {
            try { categorie = CategoriePlanta.valueOf(catStr.trim().toUpperCase()); } 
            catch (Exception ignored) {}
        }

        String tipStr = rs.getString("tip_planta");
        TipPlanta tip = TipPlanta.ORNAMENTALA;
        if (tipStr != null && !tipStr.isBlank()) {
            try { tip = TipPlanta.valueOf(tipStr.trim().toUpperCase()); } 
            catch (Exception ignored) {}
        }

        PlantaFactory factory = new PlantaFactory();
        Planta p = factory.creazaPlanta(
                categorie, rs.getInt("admin_plant_id"), rs.getString("nume_uzual"),
                rs.getString("denumire_stiintifica"), rs.getString("familie"), rs.getString("descriere"),
                rs.getFloat("inaltime_maxima"), rs.getString("perioada_inflorire"), rs.getString("ciclu_de_viata"),
                tip, rs.getString("locatie"), rs.getString("imagine_url"),
                rs.getInt("numar_petale"), rs.getString("culoare"), rs.getString("tip_coroana"),
                rs.getString("tip_frunza"), rs.getBoolean("pom_fructifer"), rs.getBoolean("produce_fructe"),
                rs.getString("tip_tulpina"), rs.getBoolean("poate_fi_uscata")
        );
        p.setId(rs.getInt("id"));
        return p;
    }

    // 1. PENTRU UZ INTERN BACKEND: Extrage absolut tot din dicționar
    public List<Planta> extrageToatePlantele() {
        String sql = "SELECT * FROM plante ORDER BY id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapeazaRandPlanta(rs));
    }

    // 2. PENTRU IERBAR GLOBAL (NOU): Extrage doar plantele publice!
    public List<Planta> extragePlantePublice() {
        // Logica SQL: Arată o plantă doar dacă e pusă default de admin (fără nicio captură) 
        // SAU dacă are cel puțin o captură marcată ca PUBLICĂ. (Ascunde automat ce e strict privat).
        String sql = "SELECT * FROM plante p " +
                     "WHERE NOT EXISTS (SELECT 1 FROM capturi_plante c WHERE c.planta_id = p.id) " +
                     "   OR EXISTS (SELECT 1 FROM capturi_plante c WHERE c.planta_id = p.id AND c.este_publica = TRUE) " +
                     "ORDER BY p.id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapeazaRandPlanta(rs));
    }

    // 3. Extrage automat ID-ul ultimei plante salvate în dicționar
    public int extrageUltimulIdPlanta() {
        try {
            return jdbcTemplate.queryForObject("SELECT id FROM plante ORDER BY id DESC LIMIT 1", Integer.class);
        } catch (Exception e) {
            return 0;
        }
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
            planta.getNume_uzual(), planta.getDenumire_stiintifica(), planta.getFamilie(), planta.getDescriere(),
            planta.getInaltime_maxima(), planta.getPerioada_inflorire(), planta.isPoate_fi_uscata(), 
            planta.getCiclu_de_viata(), planta.getTip_planta().name(), planta.getLocatie(), 
            categoriePlanta, numarPetale, culoare, tipCoroana, tipFrunza, pomFructifer, produceFructe, 
            tipTulpina, adminId, imagineUrl
        );
    }
    
    public void publicaPlantaGlobal(int plantaId, String locatie) {
        String sql = "UPDATE plante SET locatie = ? WHERE id = ?";
        try { jdbcTemplate.update(sql, locatie, plantaId); } catch (Exception ignored) {}
    }

    public void adaugaInIerbar(int userId, int plantaId) {
        String sql = "INSERT INTO plante_favorite (user_id, planta_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, userId, plantaId);
    }

    public void stergeDinIerbar(int userId, int plantaId) {
        String sql = "DELETE FROM plante_favorite WHERE user_id=? AND planta_id=?";
        jdbcTemplate.update(sql, userId, plantaId);
    }

    public void stergePlantaDefinitiv(int idPlanta) {
        String sqlFavorite = "DELETE FROM plante_favorite WHERE planta_id=?";
        jdbcTemplate.update(sqlFavorite, idPlanta);
        String sql = "DELETE FROM plante WHERE id=?";
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
            planta.getNume_uzual(), planta.getDenumire_stiintifica(), planta.getFamilie(), planta.getDescriere(),
            planta.getInaltime_maxima(), planta.getPerioada_inflorire(), planta.isPoate_fi_uscata(), 
            planta.getCiclu_de_viata(), planta.getTip_planta().name(), numarPetale, culoare, tipCoroana, tipFrunza, 
            pomFructifer, produceFructe, tipTulpina, categoriePlanta, planta.getLocatie(), imagineUrl, idPlanta
        );
    }

    public void adaugaCapturaInGalerie(int plantaId, int userId, String imagineUrl, String locatie, boolean estePublica) {
        String sql = "INSERT INTO capturi_plante (planta_id, user_id, imagine_url, locatie, este_publica, data_adaugarii) VALUES (?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql, plantaId, userId, imagineUrl, locatie, estePublica);
    }

   public void marcheazaCapturaPublica(int capturaId, int userId, String locatie) {
        String sql = "UPDATE capturi_plante SET este_publica = TRUE, locatie = ? WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, locatie, capturaId, userId);
    }

    public List<CapturaPlanta> extrageGalerieSpecie(int plantaId) {
        String sql = "SELECT c.*, COALESCE(u.username, u.email, 'Anonim') AS nume_user FROM capturi_plante c " +
                    "LEFT JOIN users u ON c.user_id = u.id " +
                    "WHERE c.planta_id = ? AND c.este_publica = TRUE ORDER BY c.data_adaugarii DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CapturaPlanta cap = new CapturaPlanta();
            cap.setId(rs.getInt("id"));
            cap.setPlantaId(rs.getInt("planta_id"));
            cap.setUserId(rs.getInt("user_id"));
            cap.setNumeUtilizator(rs.getString("nume_user"));
            cap.setImagineUrl(rs.getString("imagine_url"));
            cap.setLocatie(rs.getString("locatie"));
            cap.setDataAdaugarii(rs.getTimestamp("data_adaugarii").toLocalDateTime());
            return cap;
        }, plantaId);
    }
    
    public List<CapturaPlanta> extrageIerbarPersonalUser(int userId) {
        String sql = "SELECT c.id AS captura_id, c.planta_id, c.user_id, c.imagine_url AS captura_img, c.locatie AS captura_loc, c.data_adaugarii, " +
                     "COALESCE(u.username, u.email, 'Anonim') AS nume_user, p.* " +
                     "FROM capturi_plante c " +
                     "LEFT JOIN plante p ON c.planta_id = p.id " +
                     "LEFT JOIN users u ON c.user_id = u.id " +
                     "WHERE c.user_id = ? ORDER BY c.data_adaugarii DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CapturaPlanta cap = new CapturaPlanta();
            cap.setId(rs.getInt("captura_id"));
            cap.setPlantaId(rs.getInt("planta_id"));
            cap.setUserId(rs.getInt("user_id"));
            cap.setNumeUtilizator(rs.getString("nume_user"));
            cap.setImagineUrl(rs.getString("captura_img"));
            cap.setLocatie(rs.getString("captura_loc"));
            cap.setDataAdaugarii(rs.getTimestamp("data_adaugarii").toLocalDateTime());
            cap.setPlanta(mapeazaRandPlanta(rs));
            return cap;
        }, userId);
    }

    public List<String> extrageLocatiiSpecie(int plantaId) {
        try {
            String sql = "SELECT DISTINCT locatie FROM capturi_plante WHERE planta_id = ? AND locatie IS NOT NULL AND locatie != '' AND locatie != 'Nespecificată'";
            return jdbcTemplate.queryForList(sql, String.class, plantaId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public void stergeCapturaPersonala(int capturaId, int userId) {
        String sql = "DELETE FROM capturi_plante WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, capturaId, userId);
    }

    public void publicaCapturaExistenta(int plantaId, int userId, String locatie) {
        String sql = "UPDATE capturi_plante SET este_publica = TRUE, locatie = ? " +
                    "WHERE id = (SELECT id FROM capturi_plante WHERE planta_id = ? AND user_id = ? ORDER BY data_adaugarii DESC LIMIT 1)";
        jdbcTemplate.update(sql, locatie, plantaId, userId);
    }

    // ==========================================
    // 💡 METODE CURIOZITĂȚI BOTANICE (CALENDAR)
    // ==========================================
    public void salveazaCuriozitate(int plantaId, String titlu, String curiozitate, String iconita) {
        String sql = "INSERT INTO curiozitati_plante (planta_id, titlu, curiozitate, iconita, data_generare) VALUES (?, ?, ?, ?, CURRENT_DATE)";
        jdbcTemplate.update(sql, plantaId, titlu, curiozitate, iconita);
    }

    public List<CuriozitatePlanta> extrageIstoricCuriozitati() {
        String sql = "SELECT c.*, p.nume_uzual FROM curiozitati_plante c " +
                     "JOIN plante p ON c.planta_id = p.id ORDER BY c.data_generare DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CuriozitatePlanta c = new CuriozitatePlanta();
            c.setId(rs.getInt("id"));
            c.setPlantaId(rs.getInt("planta_id"));
            c.setNumePlanta(rs.getString("nume_uzual"));
            c.setTitlu(rs.getString("titlu"));
            c.setCuriozitate(rs.getString("curiozitate"));
            
            try {
                String ic = rs.getString("iconita");
                c.setIconita(ic != null && !ic.isBlank() ? ic : "🌿");
            } catch (Exception e) { c.setIconita("🌿"); }

            if (rs.getDate("data_generare") != null) {
                c.setDataGenerare(rs.getDate("data_generare").toLocalDate());
            }
            return c;
        });
    }

    public List<String> extrageIstoricCuriozitatiPlanta(int plantaId) {
        String sql = "SELECT curiozitate FROM curiozitati_plante WHERE planta_id = ?";
        try {
            return jdbcTemplate.queryForList(sql, String.class, plantaId);
        } catch (Exception e) {
            return List.of();
        }
    }
}
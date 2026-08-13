package com.zmc.ierbar_web_app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.user.General;
import com.zmc.ierbar_web_app.repositories.UserRepository;

@Component
public class TestBazaDeDate implements CommandLineRunner {
    
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository; // Adăugăm Repository-ul

    // Injectăm ambele dependențe prin constructor
    public TestBazaDeDate(JdbcTemplate jdbcTemplate, UserRepository userRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=========================================");
        System.out.println("🔌 PASUL 1: Testare conexiune simplă (JdbcTemplate)");
        System.out.println("=========================================");

        try {
            Integer nrPlante = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plante", Integer.class);
            System.out.println("Numărul de plante existente în baza de date: " + nrPlante);

            if(nrPlante != null && nrPlante > 0){
                String numePlanta = jdbcTemplate.queryForObject("SELECT nume_uzual FROM plante LIMIT 1", String.class);
                System.out.println("Numele unei plante din baza de date: " + numePlanta);
            }
            
        } catch(Exception e){
            System.out.println("❌ Eroare la conectarea la baza de date: " + e.getMessage());
        }

        System.out.println("\n=========================================");
        System.out.println("🧑‍🌾 PASUL 2: Testare extragere Profil și Liste (UserRepository)");
        System.out.println("=========================================");
        
        try {
            // Încercăm să extragem utilizatorul cu ID-ul 1
            int idDeTest = 2; 
            General user = userRepository.extrageProfilGeneral(idDeTest);

            if (user != null) {
                System.out.println("✅ UTILIZATOR GĂSIT!");
                System.out.println("Nume: " + user.getUsername());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Tip User: " + user.getTip_user());
                
                System.out.println("\n🌱 LISTA DE PLANTE FAVORITE:");
                if (user.getPlanteFavorite() == null || user.getPlanteFavorite().isEmpty()) {
                    System.out.println("   [!] Userul nu are nicio plantă favorită salvată.");
                } else {
                    for (Planta planta : user.getPlanteFavorite()) {
                        System.out.println("   -> " + planta.getNume_uzual() + 
                                           " | Tip Clasă OOP: " + planta.getClass().getSimpleName());
                    }
                }
                
                System.out.println("\n💬 ISTORIC CHAT AI:");
                if(user.getIstoricChatAI() == null || user.getIstoricChatAI().isEmpty()) {
                    System.out.println("   [!] Userul nu are niciun mesaj salvat.");
                } else {
                    System.out.println("   -> Au fost găsite " + user.getIstoricChatAI().size() + " mesaje.");
                }

            } else {
                System.out.println("❌ EROARE: Utilizatorul cu ID-ul " + idDeTest + " nu a fost găsit în BD.");
                System.out.println("Asigură-te că ai adăugat un user cu acest ID și tip_user='GENERAL'.");
            }
            
        } catch(Exception e){
            System.out.println("❌ Eroare la extragerea profilelor: " + e.getMessage());
        }
        
        System.out.println("=========================================\n");
    }
}
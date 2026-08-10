package com.zmc.ierbar_web_app.models.user;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import java.util.List;
import java.util.ArrayList;

public class General extends User {
    private List<Planta> planteFavorite;
    private List<MesajChat> istoricChatAI;

    public General() {
        super();
    }

    public General(int id, String username, String password, String email) {
        super(id, username, password, email, TipUser.GENERAL);
        this.planteFavorite=new ArrayList<>();
        this.istoricChatAI=new ArrayList<>();
    }

    public List<Planta> getPlanteFavorite() {
        return planteFavorite;
    }

    public void setPlanteFavorite(List<Planta> planteFavorite) {
        this.planteFavorite = planteFavorite;
    }

    public List<MesajChat> getIstoricChatAI() {
        return istoricChatAI;
    }

    public void setIstoricChatAI(List<MesajChat> istoricChatAI) {
        this.istoricChatAI = istoricChatAI;
    }

    @Override
    public String getRolString() {
        return "GENERAL";
    }

    public void adaugaPlantaFavorita(Planta planta) {
        if(!planteFavorite.contains(planta)) {
            planteFavorite.add(planta);
        }
    }

    public void stergePlantaFavorita(Planta planta, int idPlanta) {
        planteFavorite.removeIf(p->p.getId()==idPlanta);
    }

    public void adaugaIstoricChatAI(MesajChat mesaj) {
        if(!istoricChatAI.contains(mesaj)) {
            istoricChatAI.add(mesaj);
        }
    }

}

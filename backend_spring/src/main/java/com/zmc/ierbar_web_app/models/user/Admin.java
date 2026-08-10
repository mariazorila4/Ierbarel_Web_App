package com.zmc.ierbar_web_app.models.user;
import java.util.List;
import java.util.ArrayList;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;

public class Admin extends User {
    private List<Planta> planteIerbarOnline;

    public Admin() {
        super();
    }

    public Admin(int id, String username, String password, String email) {
        super(id, username, password, email, TipUser.ADMIN);
        this.planteIerbarOnline=new ArrayList<>();
    }

    public List<Planta> getPlanteIerbarOnline() {
        return planteIerbarOnline;
    }

    public void setPlanteIerbarOnline(List<Planta> planteIerbarOnline) {
        this.planteIerbarOnline = planteIerbarOnline;
    }

    @Override
    public String getRolString() {
        return "ADMIN";
    }

    public void adaugaPlanta(Planta planta) {
        if(!planteIerbarOnline.contains(planta)) {
            planteIerbarOnline.add(planta);
        }
    }

    public void stergePlanta(int idPlanta) {
        planteIerbarOnline.removeIf(p->p.getId()==idPlanta);
    }
}

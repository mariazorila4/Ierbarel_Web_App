package com.zmc.ierbar_web_app.models.user;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDateTime;

@Table("istoric_chat_ai")
public class MesajChat {
    @Id
    @Column("id")
    private int id;

    @Column("mesaj")
    private String mesaj;

    @Column("este_bot")
    private boolean este_bot;

    @Column("data_trimiterii")
    private LocalDateTime data_trimiterii;

    public MesajChat() {
    }

    public MesajChat(int id, String mesaj, boolean este_bot, LocalDateTime data_trimiterii) {
        this.id = id;
        this.mesaj = mesaj;
        this.este_bot = este_bot;
        this.data_trimiterii = data_trimiterii;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public boolean isEste_bot() {
        return este_bot;
    }

    public void setEste_bot(boolean este_bot) {
        this.este_bot = este_bot;
    }

    public LocalDateTime getData_trimiterii() {
        return data_trimiterii;
    }

    public void setData_trimiterii(LocalDateTime data_trimiterii) {
        this.data_trimiterii = data_trimiterii;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MesajChat{");
        sb.append("id=").append(id);
        sb.append(", mesaj='").append(mesaj).append('\'');
        sb.append(", este_bot=").append(este_bot);
        sb.append(", data_trimiterii=").append(data_trimiterii);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MesajChat other = (MesajChat) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
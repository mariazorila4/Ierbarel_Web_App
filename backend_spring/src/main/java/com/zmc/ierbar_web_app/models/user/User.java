package com.zmc.ierbar_web_app.models.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Table("users")
public abstract class User {
    @Id
    @Column("id")
    private int id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("email")
    private String email;

    @Column("tip_user")
    private TipUser tip_user;

    public User() {
    }

    public User(int id, String username, String password, String email, TipUser tip_user) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.tip_user = tip_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TipUser getTip_user() {
        return tip_user;
    }

    public void setTip_user(TipUser tip_user) {
        this.tip_user = tip_user;
    }

    public abstract String getRolString();

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", tip_user=").append(tip_user);
        sb.append('}');
        return sb.toString();
    }
}

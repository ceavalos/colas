package sv.com.freund.rsMHFFactuacionElectronica.entity;

/**
 * Clase DTO de usuarios
 * @author Sault
 * @since 21/02/2020
 * @version 1.0.0
 */
public class User {

    private static final long serialVersionUID = 5926468583005150707L;

    private String user;
    private String pwd;
    private String token;

    public User() {
    }

    public User(String user, String pwd, String token) {
        this.user = user;
        this.pwd = pwd;
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

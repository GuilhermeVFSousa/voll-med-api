package med.voll.api.auth.usuario.enums;

public enum Roles {
    ROLE_ADMIN("ADMIN"),
    ROLE_NORMAL_USER("NORMAL_USER");

    private String role;

    private Roles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
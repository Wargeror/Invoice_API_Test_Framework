package api.base;

public enum Endpoint {
    CLIENTS("/clients"),
    ITEMS("/items"),
    INVOICES("/invoices"),
    DIRECTORIES("/directories"),
    FILES("/files"),
    LOGIN("/login/token");

    private final String path;

    Endpoint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

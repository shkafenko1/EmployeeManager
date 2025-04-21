package by.koronatech.office.core.exceptions;

public class HttpStatusException extends RuntimeException {
    private final int statusCode;

    public HttpStatusException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

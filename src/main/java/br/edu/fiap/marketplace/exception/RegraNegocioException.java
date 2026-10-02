package br.edu.fiap.marketplace.exception;

import org.springframework.http.HttpStatus;

public class RegraNegocioException extends RuntimeException {

    private final HttpStatus status;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
        this.status = HttpStatus.CONFLICT;
    }

    public RegraNegocioException(String mensagem, HttpStatus status) {
        super(mensagem);
        this.status = status != null ? status : HttpStatus.CONFLICT;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
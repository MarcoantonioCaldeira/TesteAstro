package br.com.astro.atendimentos.exceptions;

public class AtendimentoNaoEncontradoException extends RuntimeException{

    public AtendimentoNaoEncontradoException(String message) {
        super(message);
    }
}

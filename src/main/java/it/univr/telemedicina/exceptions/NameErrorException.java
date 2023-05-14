package it.univr.telemedicina.exceptions;

public class NameErrorException extends RuntimeException{
    public NameErrorException() {
        super("Invalid username!");
    }
}

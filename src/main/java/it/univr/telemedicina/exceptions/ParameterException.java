package it.univr.telemedicina.exceptions;

public class ParameterException extends RuntimeException{
    public ParameterException(String msg){super("Parameter Error --> " + msg);}
}

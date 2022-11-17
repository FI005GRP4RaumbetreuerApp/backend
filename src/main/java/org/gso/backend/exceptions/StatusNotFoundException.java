package org.gso.backend.exceptions;

public class StatusNotFoundException extends Exception{
    public StatusNotFoundException(String text){
        super(text);
    }
}

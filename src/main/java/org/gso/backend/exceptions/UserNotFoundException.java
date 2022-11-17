package org.gso.backend.exceptions;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String text){
        super(text);
    }
}

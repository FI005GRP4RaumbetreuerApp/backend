package org.gso.backend.exceptions;

public class RoomNotFoundException extends Exception{
    public RoomNotFoundException(String text){
        super(text);
    }
}

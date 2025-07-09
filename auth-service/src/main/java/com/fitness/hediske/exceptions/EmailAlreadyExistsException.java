package com.fitness.hediske.exceptions;


// EmailAlreadyExistsException.java
public class EmailAlreadyExistsException extends RegistrationException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
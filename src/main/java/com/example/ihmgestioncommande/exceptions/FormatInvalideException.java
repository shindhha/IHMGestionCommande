package com.example.ihmgestioncommande.exceptions;

public class FormatInvalideException extends Exception {

    @Override
    public String getMessage() {
        return "Le format du numéro de série est invalide";
    }
}

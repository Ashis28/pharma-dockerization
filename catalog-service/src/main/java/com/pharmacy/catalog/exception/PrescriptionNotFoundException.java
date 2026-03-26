package com.pharmacy.catalog.exception;

public class PrescriptionNotFoundException extends RuntimeException {
    public PrescriptionNotFoundException(String message) { super(message); }
}

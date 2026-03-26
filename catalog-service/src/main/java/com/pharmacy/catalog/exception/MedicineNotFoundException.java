package com.pharmacy.catalog.exception;

public class MedicineNotFoundException extends RuntimeException {
    public MedicineNotFoundException(String message) { super(message); }
}

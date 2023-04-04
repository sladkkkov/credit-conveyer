package ru.sladkkov.exception.custom;

public class CreditIsVeryBigException extends RuntimeException {
public CreditIsVeryBigException(String message, Throwable cause) {
super(message,cause);
}}

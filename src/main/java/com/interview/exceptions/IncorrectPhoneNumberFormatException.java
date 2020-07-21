package com.interview.exceptions;

/**
 * Object to handle a 10 digit USA formatted (XXX-XXX-XXXX) phone number
 *
 * @author    Mike Buschmeier
 * @creation  18 July 2020
 */
public class IncorrectPhoneNumberFormatException extends Exception{
    public IncorrectPhoneNumberFormatException (String errorMessage){
        super(errorMessage);
    }
}

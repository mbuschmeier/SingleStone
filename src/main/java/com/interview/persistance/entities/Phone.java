package com.interview.persistance.entities;

import com.interview.exceptions.IncorrectPhoneNumberFormatException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Entity to handle a 10 digit USA formatted (XXX-XXX-XXXX) phone number
 *
 * @author Mike Buschmeier
 * @creation 18 July 2020
 */
@Entity
public class Phone
{

    @Id
    @GeneratedValue
    private Long id;
    private String number;
    private Type type;

    //Simple validation looking for a numerical 123-123-1234 sequence
    //Ideally this would be in a validation package or 3rd party library if possible
    @Transient
    private String validNumberFormat = "\\d{3}-\\d{3}-\\d{4}";

    public enum Type
    {
        home, work, mobile
    }

    public Phone()
    {
    }

    public Phone(String number, Type type) throws IncorrectPhoneNumberFormatException
    {
        setNumber(number);
        this.type = type;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number) throws IncorrectPhoneNumberFormatException
    {
        if (validateFormat(number)) {
            this.number = number;
        } else {
            /*NOTE TO SINGLESTONE
                Ideally all messages would be contained in a lexicon or properties file. I opted not to use a properties file in the interest of time on this exercise.
             */
            String errorMessage = number + " is not valid. Valid phone numbers need to be XXX-XXX-XXXX";
            throw new IncorrectPhoneNumberFormatException(errorMessage);
        }
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    private boolean validateFormat(String number)
    {
        return number.matches(validNumberFormat);
    }
}

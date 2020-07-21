package com.interview.persistance.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.interview.exceptions.IncorrectEmailFormatException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity which holds all necessary information for a human contact
 *
 * @author Mike Buschmeier
 * @creation 18 July 2020
 */

@Entity
@Table(name = "contacts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contact
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "name_id", referencedColumnName = "id")
    private Name name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Phone> phone;

    private String email;

    public Contact()
    {
    }

    public Contact(Name name, Address address, List<Phone> phone, String email)
    {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public Long getId()
    {
        return id;
    }

    public Name getName()
    {
        return name;
    }

    public void setName(Name name)
    {
        this.name = name;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public List<Phone> getPhone()
    {
        return phone;
    }

    public void setPhone(List<Phone> phone)
    {
        this.phone = phone;
    }

    public void addPhone(Phone phone)
    {
        if (this.phone == null) {
            this.phone = new ArrayList();
        }
        this.phone.add(phone);
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email) throws IncorrectEmailFormatException
    {
        if (validateEmailFormat(email)) {
            this.email = email;
        } else {
            throw new IncorrectEmailFormatException("Incorrect e-mail format");
        }

    }

    private boolean validateEmailFormat(String email)
    {
        String validEmailFormat = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        return email.matches(validEmailFormat);
    }

}

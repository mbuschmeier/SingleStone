package com.interview.persistance.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.FetchType;

/**
 * Entity to store a name
 *
 * @author Mike Buschmeier
 * @creation 18 July 2020
 */

@Entity
public class Name
{

    @Id
    @GeneratedValue
    private Long id;
    private String first;
    private String middle;
    private String last;

    @OneToOne(mappedBy = "name", fetch = FetchType.EAGER)
    private Contact contacts;

    public Name()
    {
    }

    public Name(String first, String last)
    {
        this.first = first;
        this.last = last;
    }

    public Name(String first, String middle, String last)
    {
        this.first = first;
        this.middle = middle;
        this.last = last;
    }

    public String getFirst()
    {
        return first;
    }

    public void setFirst(String first)
    {
        this.first = first;
    }

    public String getMiddle()
    {
        return middle;
    }

    public void setMiddle(String middle)
    {
        this.middle = middle;
    }

    public String getLast()
    {
        return last;
    }

    public void setLast(String last)
    {
        this.last = last;
    }
}

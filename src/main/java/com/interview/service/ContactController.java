package com.interview.service;

import com.interview.persistance.entities.Contact;
import com.interview.repositories.ContactRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST end point controller
 *
 * @author Mike Buschmeier
 * @creation 18 July 2020
 */
@RestController
public class ContactController
{

    @Autowired
    private ContactRepository contactRepository;

    //Get a single contact
    @GetMapping("/contacts/{id}")
    public Contact getContact(@PathVariable long id) {
        return contactRepository.getOne(id);
    }

    //Get All Contacts
    @GetMapping("/contacts")
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    //Create a new contact
    @PostMapping("/contacts")
    public void newContact(@RequestBody Contact newContact)
    {
        contactRepository.save(newContact);
        contactRepository.flush();
    }

    //Update an existing contact
    @PutMapping("/contacts/{id}")
    public void updateContact(@PathVariable long id, @RequestBody Contact newContact)
    {
        Contact contact = contactRepository.getOne(id);
        BeanUtils.copyProperties(newContact, contact);
        contactRepository.flush();
    }

    //Delete a contact
    @DeleteMapping("/contacts/{id}")
    public void deleteContact(@PathVariable long id) {
        contactRepository.deleteById(id);
    }
}
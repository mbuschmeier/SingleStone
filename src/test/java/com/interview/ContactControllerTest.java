package com.interview;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.exceptions.IncorrectEmailFormatException;
import com.interview.exceptions.IncorrectPhoneNumberFormatException;
import com.interview.persistance.entities.Address;
import com.interview.persistance.entities.Contact;
import com.interview.persistance.entities.Name;
import com.interview.persistance.entities.Phone;
import com.interview.repositories.ContactRepository;
import com.interview.service.ContactController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contact Controller test class
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.config.name=application-test-h2","application.trx.datasource.url=jdbc:h2:mem:trxServiceStatus"})
@AutoConfigureMockMvc
@WebAppConfiguration
public class ContactControllerTest
{

    @Autowired
    private ContactController controller;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
        Test getting a single contact via HTTP GET /contacts/{1}
     */
    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void getSingleContactTest() throws Exception {

        //Create and insert 2 contacts into the DB
        Name name = new Name("First testName 1", "PUT", "Last testName 1");
        Contact contact = createContact(name);
        contactRepository.saveAndFlush(contact);
        Name name2 = new Name("First testName 2", "PUT", "Last testName 2");
        Contact contact2 = createContact(name2);
        contactRepository.saveAndFlush(contact2);

        //Test the REST end point with only the 2nd contact
        String uri = "/contacts/"+contact.getId();
        MvcResult mvcResult = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Contact contactFromREST = mapFromJson(content, Contact.class);

        //Test to make sure we get a response
        assertNotNull(contactFromREST);

        //Test if our contact is the same.
        assertEquals(contactFromREST.getName().getFirst(), name.getFirst());
        assertEquals(contactFromREST.getName().getMiddle(), name.getMiddle());
        assertEquals(contactFromREST.getName().getLast(), name.getLast());

    }

    /**
        Test getting all contacts HTTP GET
     */
    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void getAllContactsTest() throws Exception {

        //Create and insert 2 contacts into the DB
        Name name = new Name("First testName 1", "PUT", "Last testName 1");
        Contact contact = createContact(name);
        contactRepository.saveAndFlush(contact);

        Name name2 = new Name("First testName 2", "PUT", "Last testName 2");
        Contact contact2 = createContact(name2);
        contactRepository.saveAndFlush(contact2);

        //Test the REST end point
        String uri = "/contacts";
        MvcResult mvcResult = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String content = mvcResult.getResponse().getContentAsString();
        Contact[] contactList = mapFromJson(content, Contact[].class);

        //Test to make sure we get only 2 contacts back
        assertEquals(2, contactList.length);

        //Test if our first contact is the same.
        assertEquals(contactList[0].getName().getFirst(), name.getFirst());
        assertEquals(contactList[0].getName().getMiddle(), name.getMiddle());
        assertEquals(contactList[0].getName().getLast(), name.getLast());

        //Test if our second contact is the same
        assertEquals(contactList[1].getName().getFirst(), name2.getFirst());
        assertEquals(contactList[1].getName().getMiddle(), name2.getMiddle());
        assertEquals(contactList[1].getName().getLast(), name2.getLast());
    }

    /**
        Test updating a single contact via HTTP PUT /contacts/{1}
    */
    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void putUpdateContactTest() throws Exception {

        //Hardcoded JSON string which would be expected from the client
        String contactJson = "{\"name\":{\"first\":\"Harold\",\"middle\":\"Francis\",\"last\":\"Gilkey\"},\"address\":{\"street\":\"8360 High Autumn Row\",\"city\":\"Cannon\",\"state\":\"Delaware\",\"zip\":\"19797\"},\"phone\":[{\"number\":\"302-611-9148\",\"type\":\"home\"},{\"number\":\"302-535-9427\",\"type\":\"mobile\"}],\"email\":\"harold.gilkey@yahoo.com\"}";

        Name name = new Name("Bob", "PUT", "Barker");
        Contact contact = createContact(name);
        contactRepository.saveAndFlush(contact);

        //Assert only 1 record exists
        List<Contact> contactEntities = contactRepository.findAll();
        assertEquals(1, contactEntities.size());

        //Assert the name is equal to "Bob, PUT, Barker"
        assertEquals(contact.getName().getFirst(), contactEntities.get(0).getName().getFirst());
        assertEquals(contact.getName().getMiddle(), contactEntities.get(0).getName().getMiddle());
        assertEquals(contact.getName().getLast(), contactEntities.get(0).getName().getLast());

        mockMvc.perform(put("/contacts/"+contact.getId(), 42L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactJson))
                .andExpect(status().isOk());

        contactEntities = contactRepository.findAll();

        //Assert there is still only 1 record
        assertEquals(1, contactEntities.size());

        //Assert the name has been changed to "Harold, Francis, Gilkey"
        assertEquals(contactEntities.get(0).getName().getFirst(), "Harold");
        assertEquals(contactEntities.get(0).getName().getMiddle(), "Francis");
        assertEquals(contactEntities.get(0).getName().getLast(), "Gilkey");
    }

    /**
        Test adding a single contact via HTTP POST /contacts/
    */
    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void postSavesToDatabaseTest() throws Exception {

        //Hardcoded JSON string which would be expected from the client
        String contactJson = "{\"name\":{\"first\":\"Harold\",\"middle\":\"Francis\",\"last\":\"Gilkey\"},\"address\":{\"street\":\"8360 High Autumn Row\",\"city\":\"Cannon\",\"state\":\"Delaware\",\"zip\":\"19797\"},\"phone\":[{\"number\":\"302-611-9148\",\"type\":\"home\"},{\"number\":\"302-535-9427\",\"type\":\"mobile\"}],\"email\":\"harold.gilkey@yahoo.com\"}";

        //Assert the database is empty
        List<Contact> contactEntities = contactRepository.findAll();
        assertEquals(0, contactEntities.size());

        mockMvc.perform(post("/contacts", 42L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactJson))
                .andExpect(status().isOk());

        contactEntities = contactRepository.findAll();

        //Assert we have 1 record in the database
        assertEquals(1, contactEntities.size());

        //Assert the e-mail value is the same.
        assertThat(contactEntities.get(0).getEmail()).isEqualTo("harold.gilkey@yahoo.com");
    }

    /**
        Test deleting a single contact via HTTP GET /contacts/{1}
    */
    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void deleteSingleContactTest() throws Exception {

        //Create and insert 2 contacts into the DB
        Name name = new Name("First testName 1", "PUT", "Last testName 1");
        Contact contactToDelete = createContact(name);
        contactRepository.saveAndFlush(contactToDelete);

        Name name2 = new Name("First testName 2", "PUT", "Last testName 2");
        Contact contactToRemain = createContact(name2);
        contactRepository.saveAndFlush(contactToRemain);

        //Assert 2 records have been saved to the database
        assertEquals(2, contactRepository.findAll().size());

        //Test the REST end point with only the 2nd contact
        MvcResult mvcResult = mockMvc.perform(delete("/contacts/"+contactToDelete.getId())).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        //Grab all records
        List<Contact> contactList = contactRepository.findAll();

        //Assert only 1 record remains in the database
        assertEquals(1, contactList.size());

        //Assert the remaining record is NOT the record we deleted
        assertEquals(contactToRemain.getId(), contactList.get(0).getId());
    }

    /**
        Test for HTTP response 400 due to a badly formed email in JSON
    */
    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void badEmailFormatTest() throws Exception {

        //Hardcoded JSON string which would be expected from the client
        String contactJson = "{\"name\":{\"first\":\"Harold\",\"middle\":\"Francis\",\"last\":\"Gilkey\"},\"address\":{\"street\":\"8360 High Autumn Row\",\"city\":\"Cannon\",\"state\":\"Delaware\",\"zip\":\"19797\"},\"phone\":[{\"number\":\"302-611-9148\",\"type\":\"home\"},{\"number\":\"302-535-9427\",\"type\":\"mobile\"}],\"email\":\"NotAProperEmailFormat\"}";

        //Assert the database is empty
        List<Contact> contactEntities = contactRepository.findAll();
        assertEquals(0, contactEntities.size());

        mockMvc.perform(post("/contacts", 42L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactJson))
                .andExpect(status().isBadRequest());

        //Assert the database did save a record with a bad e-mail
        contactEntities = contactRepository.findAll();
        assertEquals(0, contactEntities.size());
    }

    /**
        Test for HTTP response 400 due to a badly formed phone number in JSON
    */
    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void badPhoneFormatTest() throws Exception {

        //Hardcoded JSON string which would be expected from the client
        String contactJson = "{\"name\":{\"first\":\"Harold\",\"middle\":\"Francis\",\"last\":\"Gilkey\"},\"address\":{\"street\":\"8360 High Autumn Row\",\"city\":\"Cannon\",\"state\":\"Delaware\",\"zip\":\"19797\"},\"phone\":[{\"number\":\"302-611-9148\",\"type\":\"home\"},{\"number\":\"30253523429427\",\"type\":\"mobile\"}],\"email\":\"harold.gilkey@yahoo.com\"}";

        //Assert the database is empty
        List<Contact> contactEntities = contactRepository.findAll();
        assertEquals(0, contactEntities.size());

        mockMvc.perform(post("/contacts", 42L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactJson))
                .andExpect(status().isBadRequest());

        //Assert the database did save a record with a bad phone number
        contactEntities = contactRepository.findAll();
        assertEquals(0, contactEntities.size());
    }

    /**
        Test the ContactController exists
    */
    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    /**
        Convenience method to create a basic contact
    */
    private Contact createContact(Name name)
    {
        Contact testContact = new Contact();
        testContact.setName(name);

        Address address = new Address("8360 High Autumn Row", "Cannon", "Delaware", "19797");
        testContact.setAddress(address);

        try {
            Phone phone = new Phone("302-611-9148", Phone.Type.home);
            Phone phone2 = new Phone("302-535-9427", Phone.Type.mobile);
            testContact.addPhone(phone);
            testContact.addPhone(phone2);
        }
        catch(IncorrectPhoneNumberFormatException e)
        {
            System.out.println("could not create phone numbers");
        }
        try {
            testContact.setEmail("harold.gilkey@yahoo.com");
        }
        catch(IncorrectEmailFormatException e)
        {
            //Normally this would be logged not a system.out
            System.out.println(e);
        }
        return testContact;
    }

    /**
        Map an object to JSON
     */
    private <T> T mapFromJson(String json, Class<T> clazz)throws JsonParseException, JsonMappingException, IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

}

package com.example.arcadecontactmanager;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Set de teste pentru ContactManager.
 * Verificăm operațiunile CRUD, sortarea după nume și filtrarea după codul telefonic.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactManagerTest {

    private static ContactManager contactManager;

    @BeforeAll
    public static void setUp() {
        contactManager = new ContactManager();
        // Golim tabela pentru a începe "de la zero" (atenție la datele reale!)
        List<Contact> existing = contactManager.getContacts();
        for (Contact c : existing) {
            contactManager.deleteContact(c.getName());
        }
    }

    @Test
    @Order(1)
    public void testAddContact() {
        contactManager.addContact("Alice", "+1 123 456 789", "alice@example.com");
        Contact found = contactManager.findContact("Alice");
        assertNotNull(found, "Contactul 'Alice' ar trebui să fie adăugat și găsit.");
        assertEquals("Alice", found.getName());
        assertEquals("+1 123 456 789", found.getPhoneNumber());
        assertEquals("alice@example.com", found.getEmail());
    }

    @Test
    @Order(2)
    public void testUpdateContact() {
        contactManager.updateContact("Alice", "Alice Wonderland", "", "alice@wonderland.com");
        Contact found = contactManager.findContact("Alice Wonderland");
        assertNotNull(found, "Numele ar trebui să fie schimbat în 'Alice Wonderland'.");
        assertEquals("alice@wonderland.com", found.getEmail());
    }

    @Test
    @Order(3)
    public void testSortByName() {
        // Adăugăm un alt contact pentru a verifica sortarea
        contactManager.addContact("Bob", "+44 777 123 456", "bob@uk.org");
        // Sortăm
        contactManager.sortContactsByName();
        List<Contact> sorted = contactManager.getContacts();
        // Ar trebui să fie Alice Wonderland, apoi Bob (alfabetic)
        assertEquals("Alice Wonderland", sorted.get(0).getName());
        assertEquals("Bob", sorted.get(1).getName());
    }

    @Test
    @Order(4)
    public void testFilterByPhoneCode() {
        // Filtrăm după codul "44" (Regatul Unit)
        List<Contact> filtered = contactManager.filterContactsByPhoneCode("44");
        assertEquals(1, filtered.size(), "Ar trebui să fie doar 1 contact cu codul +44");
        assertEquals("Bob", filtered.get(0).getName());
    }

    @Test
    @Order(5)
    public void testDeleteContact() {
        boolean deletedBob = contactManager.deleteContact("Bob");
        assertTrue(deletedBob, "Contactul 'Bob' ar trebui să fie șters");
        boolean deletedAlice = contactManager.deleteContact("Alice Wonderland");
        assertTrue(deletedAlice, "Contactul 'Alice Wonderland' ar trebui să fie șters");

        // Verificăm că nu mai există
        assertNull(contactManager.findContact("Bob"));
        assertNull(contactManager.findContact("Alice Wonderland"));
    }

    @AfterAll
    public static void tearDown() {
        contactManager.close();
    }
}
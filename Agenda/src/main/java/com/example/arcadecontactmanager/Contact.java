package com.example.arcadecontactmanager;

import org.bson.types.ObjectId;

/**
 * Clasa Contact descrie entitatea unui contact stocat în baza de date MongoDB.
 * Conține câmpuri: identificator unic (id), nume (name), număr de telefon (phoneNumber) și email.
 */
public class Contact {

    /**
     * Identificator unic al contactului (atribuit de MongoDB).
     */
    private ObjectId id;

    /**
     * Numele contactului.
     */
    private String name;

    /**
     * Numărul de telefon al contactului.
     */
    private String phoneNumber;

    /**
     * Emailul contactului.
     */
    private String email;

    /**
     * Constructor pentru un contact nou, când id-ul încă nu este atribuit.
     *
     * @param name        Numele contactului
     * @param phoneNumber Numărul de telefon
     * @param email       Adresa de email
     */
    public Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Constructor utilizat la încărcarea contactelor din MongoDB (id-ul este deja cunoscut).
     *
     * @param id          Identificatorul
     * @param name        Numele
     * @param phoneNumber Numărul de telefon
     * @param email       Adresa de email
     */
    public Contact(ObjectId id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Returnează id-ul contactului (ObjectId).
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * Setează id-ul contactului.
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Returnează numele contactului.
     */
    public String getName() {
        return name;
    }

    /**
     * Setează numele contactului.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returnează numărul de telefon al contactului.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Setează numărul de telefon al contactului.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returnează emailul contactului.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setează emailul contactului.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Reprezentare sub formă de șir a contactului (pentru depanare).
     */
    @Override
    public String toString() {
        return "Name: " + name + ", Phone: " + phoneNumber + ", Email: " + email;
    }
}
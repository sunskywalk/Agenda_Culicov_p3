package com.example.arcadecontactmanager;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * Clasa ContactManager este responsabilă pentru interacțiunea cu baza de date MongoDB,
 * stochează lista locală de contacte și oferă operații CRUD.
 */
public class ContactManager {

    /**
     * Lista locală de contacte (sincronizată cu MongoDB).
     */
    private final List<Contact> contacts;

    /**
     * Conexiunea la MongoDB.
     */
    private final MongoClient mongoClient;

    /**
     * Referință la baza de date.
     */
    private final MongoDatabase database;

    /**
     * Colecția în care sunt stocate documentele de contact.
     */
    private final MongoCollection<Document> collection;

    /**
     * Constructor - stabilește conexiunea cu MongoDB și încarcă contactele.
     */
    public ContactManager() {
        this.contacts = new ArrayList<>();
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.database = mongoClient.getDatabase("contactdb");
        this.collection = database.getCollection("contacts");
        loadContactsFromDatabase();
    }

    /**
     * Închide conexiunea cu MongoDB (poate fi apelată la închiderea aplicației).
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    /**
     * Încarcă contactele din MongoDB în lista locală.
     */
    public void loadContactsFromDatabase() {
        contacts.clear();
        FindIterable<Document> docs = collection.find();
        for (Document doc : docs) {
            ObjectId id = doc.getObjectId("_id");
            String name = doc.getString("name");
            String phone = doc.getString("phone");
            String email = doc.getString("email");
            contacts.add(new Contact(id, name, phone, email));
        }
    }

    /**
     * Adaugă un contact (atât în bază, cât și în lista locală).
     *
     * @param name  Numele
     * @param phone Telefonul
     * @param email Emailul
     */
    public void addContact(String name, String phone, String email) {
        Document doc = new Document("name", name)
                .append("phone", phone)
                .append("email", email);
        collection.insertOne(doc);
        ObjectId id = doc.getObjectId("_id");
        contacts.add(new Contact(id, name, phone, email));
    }

    /**
     * Caută un contact după nume (fără a ține cont de majuscule).
     *
     * @param name numele
     * @return Contact sau null, dacă nu este găsit
     */
    public Contact findContact(String name) {
        for (Contact c : contacts) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Șterge un contact după nume, dacă este găsit.
     *
     * @param name numele
     * @return true dacă a fost șters, altfel false
     */
    public boolean deleteContact(String name) {
        Contact toDelete = findContact(name);
        if (toDelete != null) {
            collection.deleteOne(Filters.eq("_id", toDelete.getId()));
            contacts.remove(toDelete);
            return true;
        }
        return false;
    }

    /**
     * Actualizează un contact (dacă este găsit după numele vechi).
     * Câmpurile goale nu sunt modificate.
     *
     * @param oldName numele vechi
     * @param newName numele nou (dacă este gol, nu se schimbă)
     * @param newPhone telefonul nou (dacă este gol, nu se schimbă)
     * @param newEmail emailul nou (dacă este gol, nu se schimbă)
     */
    public void updateContact(String oldName, String newName, String newPhone, String newEmail) {
        Contact toUpdate = findContact(oldName);
        if (toUpdate != null) {
            if (!newName.isEmpty()) {
                toUpdate.setName(newName);
                collection.updateOne(Filters.eq("_id", toUpdate.getId()), Updates.set("name", newName));
            }
            if (!newPhone.isEmpty()) {
                toUpdate.setPhoneNumber(newPhone);
                collection.updateOne(Filters.eq("_id", toUpdate.getId()), Updates.set("phone", newPhone));
            }
            if (!newEmail.isEmpty()) {
                toUpdate.setEmail(newEmail);
                collection.updateOne(Filters.eq("_id", toUpdate.getId()), Updates.set("email", newEmail));
            }
        }
    }

    /**
     * Returnează lista locală de contacte (nesortată).
     */
    public List<Contact> getContacts() {
        return contacts;
    }

    /**
     * Sortează lista de contacte după nume (A-Z, fără a ține cont de majuscule).
     * Se aplică listei locale.
     */
    public void sortContactsByName() {
        Collections.sort(contacts, Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Filtrare după codul telefonic: returnează doar contactele al căror cod coincide cu cel specificat.
     * Lista locală contacts nu este modificată, se returnează o nouă selecție.
     *
     * @param code codul telefonic (de exemplu, "7" pentru Rusia)
     * @return lista contactelor pentru care PhoneCodeUtil.extractPhoneCode coincide cu code
     */
    public List<Contact> filterContactsByPhoneCode(String code) {
        List<Contact> filtered = new ArrayList<>();
        for (Contact c : contacts) {
            String cCode = PhoneCodeUtil.extractPhoneCode(c.getPhoneNumber());
            if (cCode.equals(code)) {
                filtered.add(c);
            }
        }
        return filtered;
    }
}
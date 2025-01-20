package com.example.arcadecontactmanager;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

/**
 * Clasa ContactManagerGUI implementează interfața grafică în stil arcade,
 * gestionează contactele (CRUD, filtrare, sortare) și afișează doar numele contactului.
 * La clic pe un contact, sunt afișate informațiile complete.
 */
public class ContactManagerGUI extends JFrame {

    /**
     * Managerul de contacte care gestionează baza de date și lista locală.
     */
    private final ContactManager contactManager;

    /**
     * Panoul unde sunt afișate cardurile cu numele contactelor.
     */
    private JPanel contactsPanel;

    /**
     * Combo-box pentru selectarea filtrării/sortării:
     * - "Toate contactele (fără sortare)",
     * - "Sortare după nume (A-Z)",
     * - sau coduri de țări.
     */
    private JComboBox<String> filterComboBox;

    /**
     * Creează fereastra aplicației cu interfața arcade.
     */
    public ContactManagerGUI() {
        setTitle("Contact Manager - Arcade Edition");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Simulăm "ecranul" unui automat arcade
        // Creăm containerul principal cu un fundal / bordură specială
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        getContentPane().add(mainPanel);

        // Inițializăm managerul
        contactManager = new ContactManager();

        // Panoul superior (simulăm panoul de control al automatului)
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Zona centrală cu "ecranul", unde vor fi contactele (scroll)
        contactsPanel = new JPanel();
        contactsPanel.setBackground(new Color(20, 20, 20));
        contactsPanel.setLayout(new GridLayout(0, 1, 10, 10));

        JScrollPane scrollPane = new JScrollPane(contactsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 216, 0), 4));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Încărcăm contactele inițiale
        loadContacts();
    }

    /**
     * Creează panoul superior cu butoane pentru "adăugare", "reîmprospătare",
     * "ștergere", "editare" și un combo-box pentru selectarea filtrării/sortării.
     *
     * @return panoul (JPanel)
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(10, 10, 10));
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        Color arcadeYellow = new Color(255, 216, 0);
        Color arcadeBlue = new Color(0, 153, 255);

        JButton addButton = createArcadeButton("➕", arcadeBlue);
        JButton viewButton = createArcadeButton("🔄", arcadeYellow);  // Reîmprospătare
        JButton deleteButton = createArcadeButton("🗑", arcadeBlue);
        JButton editButton = createArcadeButton("✏", arcadeYellow);

        // Combo-box:
        // 1) "Toate contactele (fără sortare)"
        // 2) "Sortare după nume"
        // 3) Urmează lista codurilor de țări.
        List<String> comboItems = new ArrayList<>();
        comboItems.add("Toate contactele (fără sortare)");
        comboItems.add("Sortare după nume (A-Z)");

        // Adăugăm codurile din PhoneCodeUtil
        for (Entry<String, String> entry : PhoneCodeUtil.getCountryCodes().entrySet()) {
            String code = entry.getKey();
            String country = entry.getValue();
            comboItems.add("Cod +" + code + " (" + country + ")");
        }

        filterComboBox = new JComboBox<>(comboItems.toArray(new String[0]));
        filterComboBox.setFont(new Font("Courier", Font.BOLD, 15));
        filterComboBox.setBackground(Color.BLACK);
        filterComboBox.setForeground(arcadeBlue);
        filterComboBox.setBorder(new LineBorder(arcadeBlue, 2));
        filterComboBox.setFocusable(false);

        topPanel.add(addButton);
        topPanel.add(viewButton);
        topPanel.add(deleteButton);
        topPanel.add(editButton);
        topPanel.add(filterComboBox);

        // Handlere
        addButton.addActionListener(e -> addContactDialog());
        viewButton.addActionListener(e -> loadContacts());
        deleteButton.addActionListener(e -> deleteContactDialog());
        editButton.addActionListener(e -> editContactDialog());

        filterComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();
                applyFilterOrSorting(selected);
            }
        });

        return topPanel;
    }

    /**
     * Creează un buton în stil arcade (font mare, bordură colorată).
     *
     * @param text  Textul butonului
     * @param color Culoarea bordurii și textului
     * @return butonul (JButton)
     */
    private JButton createArcadeButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Courier", Font.BOLD, 24));
        button.setBackground(Color.BLACK);
        button.setForeground(color);
        button.setBorder(new LineBorder(color, 2));
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Aplică filtrarea/sortarea în funcție de opțiunea selectată din ComboBox.
     *
     * @param option textul din combo-box
     */
    private void applyFilterOrSorting(String option) {
        if (option.equals("Toate contactele (fără sortare)")) {
            // Afișăm toate contactele așa cum sunt
            contactManager.loadContactsFromDatabase();  // reîncărcăm din bază
            loadContacts();
        } else if (option.equals("Sortare după nume (A-Z)")) {
            contactManager.loadContactsFromDatabase();
            contactManager.sortContactsByName();
            loadContacts();
        } else if (option.startsWith("Cod +")) {
            // De exemplu: "Cod +7 (Rusia/Kazahstan)"
            // Trebuie extras "7" (după "Cod +", până la spațiu)
            String codePart = option.substring(5); // "7 (Rusia/Kazahstan)"
            String code = codePart.split(" ")[0];   // "7"
            contactManager.loadContactsFromDatabase();
            List<Contact> filtered = contactManager.filterContactsByPhoneCode(code);
            loadContacts(filtered); // afișăm doar filtrate
        }
    }

    /**
     * Încarcă cardurile de contacte din lista locală (contactManager.getContacts()).
     */
    private void loadContacts() {
        loadContacts(contactManager.getContacts());
    }

    /**
     * Încarcă cardurile din lista de contacte transmisă (pentru filtrare).
     *
     * @param contacts Lista de contacte
     */
    private void loadContacts(List<Contact> contacts) {
        contactsPanel.removeAll();
        for (Contact c : contacts) {
            JPanel card = createContactCard(c);
            contactsPanel.add(card);
        }
        contactsPanel.revalidate();
        contactsPanel.repaint();
    }

    /**
     * Creează un "card" - inițial afișează doar numele.
     * La clic pe card, apare un dialog cu telefonul și emailul.
     *
     * @param contact contactul
     * @return panoul (JPanel) card
     */
    private JPanel createContactCard(Contact contact) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(new LineBorder(new Color(255, 216, 0), 2));

        JLabel nameLabel = new JLabel(contact.getName(), JLabel.CENTER);
        nameLabel.setFont(new Font("Courier", Font.BOLD, 20));
        nameLabel.setForeground(new Color(0, 153, 255));

        card.add(nameLabel, BorderLayout.CENTER);

        // La clic, afișăm dialogul cu detalii
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showContactDetailsDialog(contact);
            }
        });

        return card;
    }

    /**
     * Afișează o fereastră de dialog cu informații detaliate despre contact (nume, telefon, email).
     *
     * @param contact contactul pentru care se afișează informații
     */
    private void showContactDetailsDialog(Contact contact) {
        String message = "Name: " + contact.getName()
                + "\nPhone: " + contact.getPhoneNumber()
                + "\nEmail: " + contact.getEmail();
        JOptionPane.showMessageDialog(this, message, "Contact Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Fereastră de dialog pentru adăugarea unui contact nou.
     */
    private void addContactDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter name:");
        if (name == null) return;

        String phone = JOptionPane.showInputDialog(this, "Enter phone number:");
        if (phone == null) return;

        String email = JOptionPane.showInputDialog(this, "Enter email:");
        if (email == null) return;

        contactManager.addContact(name, phone, email);
        JOptionPane.showMessageDialog(this, "Contact added successfully.");
        loadContacts();
    }

    /**
     * Fereastră de dialog pentru ștergerea unui contact.
     */
    private void deleteContactDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter the name of the contact to delete:");
        if (name == null) return;

        boolean deleted = contactManager.deleteContact(name);
        if (deleted) {
            JOptionPane.showMessageDialog(this, "Contact deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Contact not found.");
        }
        loadContacts();
    }

    /**
     * Fereastră de dialog pentru editarea unui contact.
     */
    private void editContactDialog() {
        String oldName = JOptionPane.showInputDialog(this, "Enter the name of the contact to edit:");
        if (oldName == null) return;

        Contact contactToEdit = contactManager.findContact(oldName);
        if (contactToEdit == null) {
            JOptionPane.showMessageDialog(this, "Contact not found.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this,
                "Enter new name (leave blank to keep current):",
                contactToEdit.getName());
        if (newName == null) return;

        String newPhone = JOptionPane.showInputDialog(this,
                "Enter new phone (leave blank to keep current):",
                contactToEdit.getPhoneNumber());
        if (newPhone == null) return;

        String newEmail = JOptionPane.showInputDialog(this,
                "Enter new email (leave blank to keep current):",
                contactToEdit.getEmail());
        if (newEmail == null) return;

        contactManager.updateContact(oldName, newName, newPhone, newEmail);
        JOptionPane.showMessageDialog(this, "Contact updated successfully.");
        loadContacts();
    }

    /**
     * Punctul de intrare (alternativ) pentru rularea acestui GUI.
     *
     * @param args argumentele din linia de comandă
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ContactManagerGUI gui = new ContactManagerGUI();
            gui.setVisible(true);
        });
    }
}
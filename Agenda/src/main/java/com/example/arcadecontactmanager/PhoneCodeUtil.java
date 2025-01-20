package com.example.arcadecontactmanager;

import java.util.HashMap;
import java.util.Map;

/**
 * Clasă utilitară pentru lucrul cu codurile telefonice ale țărilor.
 * Permite extragerea prefixului dintr-un număr de telefon și stochează o mapă cod -> țară.
 */
public class PhoneCodeUtil {

    /**
     * Hartă (cod -> numele țării).
     */
    private static final Map<String, String> countryCodes = new HashMap<>();

    static {
        countryCodes.put("1", "SUA/Canada");
        countryCodes.put("7", "Rusia/Kazahstan");
        countryCodes.put("44", "Regatul Unit");
        countryCodes.put("49", "Germania");
        countryCodes.put("81", "Japonia");
        countryCodes.put("86", "China");
        countryCodes.put("91", "India");
        // Poate fi extinsă dacă este necesar
    }

    /**
     * Returnează mapping-ul "cod -> nume țară" (de exemplu, "7" -> "Rusia/Kazahstan").
     *
     * @return hartă nemodificabilă
     */
    public static Map<String, String> getCountryCodes() {
        return countryCodes;
    }

    /**
     * Încearcă să extragă codul telefonic dintr-un număr complet (fără "+", ia 1..3 cifre).
     *
     * @param phoneNumber numărul complet
     * @return codul (șir de caractere), dacă este găsit, altfel un șir gol
     */
    public static String extractPhoneCode(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }

        String normalized = phoneNumber.replaceAll("[^0-9]", "");
        for (int length = 3; length >= 1; length--) {
            if (normalized.length() >= length) {
                String possibleCode = normalized.substring(0, length);
                if (countryCodes.containsKey(possibleCode)) {
                    return possibleCode;
                }
            }
        }
        return "";
    }
}
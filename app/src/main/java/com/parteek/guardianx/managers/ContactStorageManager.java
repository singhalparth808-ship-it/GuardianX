package com.parteek.guardianx.managers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages local emergency contact storage.
 *
 * Version: v0.6
 */
public class ContactStorageManager {

    private static final String PREF_NAME = "guardian_contacts";
    private static final String KEY_CONTACTS = "saved_contacts";

    private final SharedPreferences sharedPreferences;

    public ContactStorageManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean saveContact(String name, String phone) {

        String cleanedName = name.trim();
        String cleanedPhone = phone.trim();

        if (cleanedName.isEmpty() || cleanedPhone.isEmpty()) {
            return false;
        }

        if (isDuplicateNumber(cleanedPhone)) {
            return false;
        }

        String existingContacts = sharedPreferences.getString(KEY_CONTACTS, "");

        String newContact = cleanedName + " - " + cleanedPhone;

        String updatedContacts;

        if (existingContacts == null || existingContacts.isEmpty()) {
            updatedContacts = newContact;
        } else {
            updatedContacts = existingContacts + "\n" + newContact;
        }

        sharedPreferences.edit()
                .putString(KEY_CONTACTS, updatedContacts)
                .apply();

        return true;
    }

    public String getSavedContactsText() {

        String contacts = sharedPreferences.getString(KEY_CONTACTS, "");

        if (contacts == null || contacts.isEmpty()) {
            return "";
        }

        return contacts;
    }

    public List<String> getSavedContactLines() {

        List<String> contactLines = new ArrayList<>();

        String contacts = getSavedContactsText();

        if (contacts.isEmpty()) {
            return contactLines;
        }

        String[] lines = contacts.split("\\n");

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                contactLines.add(line.trim());
            }
        }

        return contactLines;
    }

    public List<String> getSavedPhoneNumbers() {

        List<String> phoneNumbers = new ArrayList<>();

        List<String> contactLines = getSavedContactLines();

        for (String line : contactLines) {

            String[] parts = line.split(" - ");

            if (parts.length >= 2) {
                String phone = parts[1].trim();

                if (!phone.isEmpty()) {
                    phoneNumbers.add(phone);
                }
            }
        }

        return phoneNumbers;
    }

    public boolean hasContacts() {
        return !getSavedPhoneNumbers().isEmpty();
    }

    public boolean isDuplicateNumber(String phone) {

        String cleanedPhone = phone.trim();

        List<String> phoneNumbers = getSavedPhoneNumbers();

        for (String savedPhone : phoneNumbers) {
            if (savedPhone.equals(cleanedPhone)) {
                return true;
            }
        }

        return false;
    }

    public void deleteContact(String contactLineToDelete) {

        List<String> contactLines = getSavedContactLines();

        StringBuilder updatedContacts = new StringBuilder();

        for (String contactLine : contactLines) {

            if (!contactLine.equals(contactLineToDelete)) {

                if (updatedContacts.length() > 0) {
                    updatedContacts.append("\n");
                }

                updatedContacts.append(contactLine);
            }
        }

        sharedPreferences.edit()
                .putString(KEY_CONTACTS, updatedContacts.toString())
                .apply();
    }
}
package com.parteek.guardianx.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parteek.guardianx.R;

/**
 * Allows users to save emergency contacts locally.
 *
 * Version: v0.5
 */
public class ContactsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "guardian_contacts";
    private static final String KEY_CONTACTS = "saved_contacts";

    private EditText contactNameInput;
    private EditText contactPhoneInput;
    private TextView savedContactsText;
    private Button saveContactButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        initializeViews();
        loadSavedContacts();
        setupSaveButton();
    }

    private void initializeViews() {
        contactNameInput = findViewById(R.id.contactNameInput);
        contactPhoneInput = findViewById(R.id.contactPhoneInput);
        savedContactsText = findViewById(R.id.savedContactsText);
        saveContactButton = findViewById(R.id.saveContactButton);
    }

    private void setupSaveButton() {
        saveContactButton.setOnClickListener(view -> saveContact());
    }

    private void saveContact() {

        String name = contactNameInput.getText().toString().trim();
        String phone = contactPhoneInput.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, R.string.enter_contact_details, Toast.LENGTH_SHORT).show();
            return;
        }

        String existingContacts =
                sharedPreferences.getString(KEY_CONTACTS, "");

        String newContact = name + " - " + phone;

        String updatedContacts;

        if (existingContacts == null || existingContacts.isEmpty()) {
            updatedContacts = newContact;
        } else {
            updatedContacts = existingContacts + "\n" + newContact;
        }

        sharedPreferences.edit()
                .putString(KEY_CONTACTS, updatedContacts)
                .apply();

        contactNameInput.setText("");
        contactPhoneInput.setText("");

        loadSavedContacts();

        Toast.makeText(this, R.string.contact_saved, Toast.LENGTH_SHORT).show();
    }

    private void loadSavedContacts() {

        String contacts =
                sharedPreferences.getString(KEY_CONTACTS, "");

        if (contacts == null || contacts.isEmpty()) {
            savedContactsText.setText(R.string.no_contacts_saved);
        } else {
            savedContactsText.setText(contacts);
        }
    }
}
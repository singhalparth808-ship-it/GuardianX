package com.parteek.guardianx.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.parteek.guardianx.R;
import com.parteek.guardianx.managers.ContactStorageManager;

import java.util.List;

/**
 * Allows users to save emergency contacts locally.
 *
 * Version: v0.6
 */
public class ContactsActivity extends AppCompatActivity {

    private EditText contactNameInput;
    private Button saveContactButton;
    private EditText contactPhoneInput;
    private LinearLayout savedContactsContainer;
    private ContactStorageManager contactStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactStorageManager = new ContactStorageManager(this);

        initializeViews();
        loadSavedContacts();
        setupSaveButton();
    }

    private void initializeViews() {
        contactNameInput = findViewById(R.id.contactNameInput);
        contactPhoneInput = findViewById(R.id.contactPhoneInput);
        savedContactsContainer = findViewById(R.id.savedContactsContainer);
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

        if (contactStorageManager.isDuplicateNumber(phone)) {
            Toast.makeText(this, R.string.duplicate_contact, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean saved = contactStorageManager.saveContact(name, phone);

        if (!saved) {
            Toast.makeText(this, R.string.enter_contact_details, Toast.LENGTH_SHORT).show();
            return;
        }

        contactNameInput.setText("");
        contactPhoneInput.setText("");

        loadSavedContacts();

        Toast.makeText(this, R.string.contact_saved, Toast.LENGTH_SHORT).show();
    }

    private void loadSavedContacts() {

        savedContactsContainer.removeAllViews();

        List<String> contacts = contactStorageManager.getSavedContactLines();

        if (contacts.isEmpty()) {

            TextView emptyText = new TextView(this);
            emptyText.setText(R.string.no_contacts_saved);
            emptyText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            emptyText.setTextSize(16);

            savedContactsContainer.addView(emptyText);

            return;
        }

        for (String contactLine : contacts) {
            addContactRow(contactLine);
        }
    }

    private void addContactRow(String contactLine) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 12, 0, 12);

        TextView contactText = new TextView(this);
        contactText.setText(contactLine);
        contactText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        contactText.setTextSize(18);

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        contactText.setLayoutParams(textParams);

        TextView deleteButton = new TextView(this);
        deleteButton.setText("🗑");
        deleteButton.setTextSize(24);
        deleteButton.setGravity(Gravity.CENTER);
        deleteButton.setContentDescription(getString(R.string.delete_contact));

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        deleteButton.setLayoutParams(deleteParams);

        deleteButton.setOnClickListener(view -> {
            contactStorageManager.deleteContact(contactLine);
            loadSavedContacts();
        });

        row.addView(contactText);
        row.addView(deleteButton);

        savedContactsContainer.addView(row);
    }
}
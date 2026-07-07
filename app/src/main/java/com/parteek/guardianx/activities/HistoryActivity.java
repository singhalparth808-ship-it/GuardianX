package com.parteek.guardianx.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parteek.guardianx.R;
import com.parteek.guardianx.managers.AlertHistoryManager;

/**
 * Displays saved SOS alert history.
 *
 * Version: v0.7
 */
public class HistoryActivity extends AppCompatActivity {

    private TextView historyText;
    private Button clearHistoryButton;

    private AlertHistoryManager alertHistoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        alertHistoryManager = new AlertHistoryManager(this);

        initializeViews();
        loadHistory();
        setupClearButton();
    }

    private void initializeViews() {
        historyText = findViewById(R.id.historyText);
        clearHistoryButton = findViewById(R.id.clearHistoryButton);
    }

    private void setupClearButton() {
        clearHistoryButton.setOnClickListener(view -> {
            alertHistoryManager.clearHistory();
            loadHistory();

            Toast.makeText(
                    this,
                    R.string.history_cleared,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void loadHistory() {

        String history = alertHistoryManager.getAlertHistory();

        if (history.isEmpty()) {
            historyText.setText(R.string.no_alert_history);
        } else {
            historyText.setText(history);
        }
    }
}
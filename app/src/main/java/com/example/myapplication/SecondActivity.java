package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button submitButton = findViewById(R.id.submitButton);
        Button backButton = findViewById(R.id.backButton);
        Button exitButton = findViewById(R.id.exitButton);

        // Navigate to Thank You screen
        submitButton.setOnClickListener(v -> navigateToThankYouScreen());

        // Navigate back to the previous screen
        backButton.setOnClickListener(v -> finish());

        // Exit confirmation dialog
        exitButton.setOnClickListener(v -> showExitConfirmationDialog());
    }

    private void navigateToThankYouScreen() {
        // Navigate to the Thank You screen (assume the activity is named ThankYouActivity)
        Intent intent = new Intent(this, SummaryActivity.class);
        startActivity(intent);
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_confirmation_title)
                .setMessage(R.string.exit_confirmation_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> finishAffinity())
                .setNegativeButton(R.string.no, null)
                .show();
    }
}

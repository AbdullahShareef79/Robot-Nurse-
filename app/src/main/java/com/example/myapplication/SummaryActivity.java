package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;

public class SummaryActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "SummaryActivity";
    private QiContext qiContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Button exitButton = findViewById(R.id.buttonExit);

        // Exit the app when the Exit button is clicked
        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Close the app completely
        });

        // Register the QiSDK
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister the QiSDK
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        Log.d(TAG, "Robot focus gained.");
        sayThankYou();
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        Log.d(TAG, "Robot focus lost.");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e(TAG, "Robot focus refused: " + reason);
    }

    private void sayThankYou() {
        if (qiContext != null) {
            String thankYouMessage = "Herzlichen Dank! Ich werde Ihre Daten an unser Team weiterleiten. Bitte haben Sie noch ein wenig Geduld. Sie werden in Kürze angerufen. Ich wünsche Ihnen einen schönen Tag.";

            // Use SayBuilder to make the robot speak
            Future<Void> sayFuture = SayBuilder.with(qiContext)
                    .withText(thankYouMessage)
                    .build()
                    .async()
                    .run();

            // Log the outcome of the speech
            sayFuture.thenConsume(future -> {
                if (future.isSuccess()) {
                    Log.d(TAG, "Thank you message delivered successfully.");
                } else {
                    Log.e(TAG, "Failed to deliver thank you message: ", future.getError());
                }
            });
        }
    }
}

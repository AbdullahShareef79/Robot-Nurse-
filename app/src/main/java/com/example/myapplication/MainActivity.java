package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatVariable;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Topic;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "MainActivity";

    private QiContext qiContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button exitButton = findViewById(R.id.exitButton);

        startButton.setOnClickListener(v -> {
            // Navigate to SecondActivity
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });

        exitButton.setOnClickListener(v -> {
            finish(); // Close the app
        });

        // Register the QiSDK
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister QiSDK
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        Log.d(TAG, "Robot focus gained.");
        introduceRobot(); // Make Pepper speak the introduction when robot focus is gained
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

    // Method to introduce the robot with speech
    private void introduceRobot() {
        if (qiContext != null) {
            String introduction = "Hallo, ich bin Pepper. Ich werde Ihnen bei diesem Screening helfen. Bitte klicken Sie auf Starten, um zu beginnen.";

            // Use SayBuilder to make the robot say the introduction
            Future<Void> sayFuture = SayBuilder.with(qiContext)
                    .withText(introduction)
                    .build()
                    .async()
                    .run();

            // Log success or failure of the speech
            sayFuture.thenConsume(future -> {
                if (future.isSuccess()) {
                    Log.d(TAG, "Introduction completed.");
                    // After introduction, you can proceed with chat or any other actions
                    startChatbot();  // Optional: Start the chatbot if needed
                } else {
                    Log.e(TAG, "Failed to deliver introduction: ", future.getError());
                }
            });
        }
    }

    // Initialize the chatbot and set up the chat topic and variables (Optional)
    private void startChatbot() {
        // Create a chat topic
        Topic topic = TopicBuilder.with(qiContext)
                .withResource(R.raw.dialog) // Make sure to update to your actual topic resource file
                .build();

        // Create a new QiChatbot
        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext)
                .withTopic(topic)
                .build();

        // Create a new Chat action
        Chat chatAction = ChatBuilder.with(qiContext)
                .withChatbot(qiChatbot)
                .build();

        // Set up a listener for a chat variable (for example, Name)
        QiChatVariable nameVariable = qiChatbot.variable("Name");
        nameVariable.addOnValueChangedListener(currentValue -> Log.i(TAG, "Chat var Name: " + currentValue));

        // Start the chat action asynchronously (Optional)
        chatAction.async().run();
    }
}

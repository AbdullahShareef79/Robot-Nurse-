package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Topic;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "MainActivity";

    private QiContext qiContext;
    private QiChatbot qiChatbot;



    private final String[] questions = {
            "What is your name?",
            "How old are you?",
            "Do you have any allergies?",
            "What is your favorite color?"
    };
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startChatbot());

        // Register the robot lifecycle callbacks
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister the robot lifecycle callbacks
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        Log.d(TAG, "Robot focus gained.");
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

    private void startChatbot() {
        if (qiContext != null) {
            // Run the resource-building in a separate thread
            new Thread(() -> {
                try {
                    // Build the topic using the dialog resource
                    Topic topic = TopicBuilder.with(qiContext).withResource(R.raw.dialog).build();
                    runOnUiThread(() -> initializeChatbot(topic));
                } catch (Exception e) {
                    Log.e(TAG, "Error building topic: " + e.getMessage());
                }
            }).start();
        }
    }

    private void initializeChatbot(Topic topic) {
        new Thread(() -> {
            try {
                qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();
                Log.d(TAG, "Chatbot initialized.");
                Chat chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).build();
                Log.d(TAG, "Chat started.");
                chat.async().run();
            } catch (Exception e) {
                Log.e(TAG, "Error initializing chatbot: " + e.getMessage());
            }
        }).start();
    }

    private void introduceRobot() {
        Log.d(TAG, "Starting introduction...");
        String introduction = "Hi, I am Pepper. Let's start a screening process.";
        qiChatbot.variable("question").async().setValue(introduction);
        try {
            Thread.sleep(3000); // Sleep for 3 seconds before starting the questions
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        askNextQuestion();
    }

    private void askNextQuestion() {
        if (currentQuestionIndex < questions.length) {
            // Set the next question in the "question" variable
            qiChatbot.variable("question").async().setValue(questions[currentQuestionIndex]);

            // Move to the next question after receiving the answer
            currentQuestionIndex++;
        } else {
            Log.i(TAG, "All questions asked. Chat completed.");
            // Optionally reset or perform an action when all questions are done
            currentQuestionIndex = 0;
        }
    }
}

package com.example.temimap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnBeWithMeStatusChangedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.map.MapModel;
import com.robotemi.sdk.map.OnLoadMapStatusChangedListener;
import com.robotemi.sdk.navigation.model.Position;
import com.robotemi.sdk.permission.Permission;
import com.robotemi.sdk.navigation.model.Position;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        Robot.TtsListener,
        OnBeWithMeStatusChangedListener,
        OnGoToLocationStatusChangedListener,
        OnLocationsUpdatedListener,
        OnLoadMapStatusChangedListener{
    private String speakText = "嗨，我是Temi 很高興為您服務。";
    private Robot robot;
    private Button chargeStation_btn;
    private Button livingRoom_btn;
    private Button sofa_btn;
    private Button followMode_btn;
    private TextView currentPosition;
    private float x, y, yaw = (float) 0.0;

    @Override
    protected void onStop() {
        super.onStop();
        robot.removeOnBeWithMeStatusChangedListener(this);
        robot.removeOnGoToLocationStatusChangedListener(this);
        robot.removeTtsListener(this);
        robot.removeOnLocationsUpdateListener(this);
        robot.stopMovement();
        if (robot.checkSelfPermission(Permission.FACE_RECOGNITION) == Permission.GRANTED) {
            robot.stopFaceRecognition();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        robot.addOnBeWithMeStatusChangedListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);
        robot.addTtsListener(this);
        robot.addOnLocationsUpdatedListener(this);
        robot.showTopBar();
    }
    @Override
    protected void onDestroy() {
        robot.removeOnLoadMapStatusChangedListener(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chargeStation_btn = findViewById(R.id.charge_btn);
        livingRoom_btn = findViewById(R.id.living_btn);
        sofa_btn = findViewById(R.id.btn_sofa);
        followMode_btn = findViewById(R.id.follow_btn);
        currentPosition = findViewById(R.id.posision_textview);
        robot.speak(TtsRequest.create(speakText, true));
        chargeStation_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            x = (float) 0.019;
            y = (float) -0.0108;
            yaw = (float) -0.2502;
            robot.goToPosition(new Position(x, y, yaw, -4));
            }
        });
        livingRoom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = (float) 4.0991;
                y = (float) 0.8331;
                yaw = (float) 1.2515;
                robot.goToPosition(new Position(x, y, yaw, 4));
                robot.speak(TtsRequest.create("前往客廳", true));
            }
        });
        sofa_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = (float) 4.1883;
                y = (float) -2.9526;
                yaw = (float) 0.3929;
                robot.goToPosition(new Position(x, y, yaw, -21));
                robot.speak(TtsRequest.create("前往沙發", true));
            }
        });
        followMode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robot.beWithMe();
                hideKeyboard();
                robot.speak(TtsRequest.create("跟隨模式", true));
            }
        });


    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onTtsStatusChanged(@NotNull TtsRequest ttsRequest) {
        // Do whatever you like upon the status changing. after the robot finishes speaking
    }

    @Override
    public void onBeWithMeStatusChanged(@NotNull String status) {
        //  When status changes to "lock" the robot recognizes the user and begin to follow.
        switch (status) {
            case OnBeWithMeStatusChangedListener.ABORT:
                // do something i.e. speak
                robot.speak(TtsRequest.create("Abort", false));
                break;

            case OnBeWithMeStatusChangedListener.CALCULATING:
                robot.speak(TtsRequest.create("Calculating", false));
                break;

            case OnBeWithMeStatusChangedListener.LOCK:
                robot.speak(TtsRequest.create("Lock", false));
                break;

            case OnBeWithMeStatusChangedListener.SEARCH:
                robot.speak(TtsRequest.create("search", false));
                break;

            case OnBeWithMeStatusChangedListener.START:
                robot.speak(TtsRequest.create("Start", false));
                break;

            case OnBeWithMeStatusChangedListener.TRACK:
                robot.speak(TtsRequest.create("Track", false));
                break;
        }
    }

    @Override
    public void onGoToLocationStatusChanged(@NotNull String location, String status, int descriptionId, @NotNull String description) {
        //printLog("GoToStatusChanged", "status=" + status + ", descriptionId=" + descriptionId + ", description=" + description);
        robot.speak(TtsRequest.create(description, false));
        switch (status) {
            case OnGoToLocationStatusChangedListener.START:
                robot.speak(TtsRequest.create("Starting", false));
                break;

            case OnGoToLocationStatusChangedListener.CALCULATING:
                robot.speak(TtsRequest.create("Calculating", false));
                break;

            case OnGoToLocationStatusChangedListener.GOING:
                robot.speak(TtsRequest.create("Going", false));
                break;

            case OnGoToLocationStatusChangedListener.COMPLETE:
                robot.speak(TtsRequest.create("Completed", false));
                break;

            case OnGoToLocationStatusChangedListener.ABORT:
                robot.speak(TtsRequest.create("Cancelled", false));
                break;
        }
    }

    @Override
    public void onLocationsUpdated(@NotNull List<String> locations) {
        //Saving or deleting a location will update the list.
        Toast.makeText(this, "Locations updated :\n" + locations, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadMapStatusChanged(int status) {
        //printLog("load map status: " + status);
    }

}
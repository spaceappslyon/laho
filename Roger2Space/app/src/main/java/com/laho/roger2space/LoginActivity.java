package com.laho.roger2space;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;

public class LoginActivity extends FragmentActivity implements
        View.OnClickListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private PlusClient plusClient;
    private ConnectionResult mConnectionResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize the Google+ client object
        plusClient = new PlusClient.Builder(getApplicationContext(), this, this)
                .setActions("http://schemas.google.com/AddActivity")
                .build();


        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadProgress(true);
                    plusClient.connect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        plusClient.disconnect();
    }





    @Override
    public void onConnected(Bundle connectionHint)
    {
        try{
            String name = plusClient.getCurrentPerson().getName().getGivenName();
            String avatarUrl = "";
            if (plusClient.getCurrentPerson().getImage().hasUrl())
            {
                avatarUrl = plusClient.getCurrentPerson().getImage().getUrl();
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("pictureUrl", avatarUrl);
            startActivity(intent);
            this.finish();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDisconnected() {
        loadProgress(false);
        Log.d("DISCONNECTED", "disconnected");
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            plusClient.connect();
        }
    }

    public void loadProgress(Boolean isLoading){
        Log.e("YEAH","1");
        try {
            SignInButton log = (SignInButton) findViewById(R.id.google_sign_in_button);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            View textViewConnection = (View) findViewById(R.id.textViewConnection);
            if (isLoading) {
                log.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                textViewConnection.setVisibility(View.VISIBLE);
                Log.e("YEAH","2");
            } else {
                log.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                textViewConnection.setVisibility(View.INVISIBLE);
                Log.e("YEAH","3");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                plusClient.connect();
            }
        }
        loadProgress(false);
    }

    @Override
    public void onClick(View v) {

    }
}
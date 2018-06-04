package com.twilio.voice.quickstart;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "VoiceActivity";

    private LinearLayout mPhoneLayout;
    private LinearLayout mCodeLayout;

    private EditText mPhoneText;
    private EditText mCodeText;

    private ProgressBar mPhoneBar;
    private ProgressBar mCodeBar;

    private Button mVerifyBtn;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mPhoneLayout = (LinearLayout) findViewById(R.id.phoneHLayout);
        mCodeLayout = (LinearLayout) findViewById(R.id.codeHLayout);

        mPhoneText = (EditText) findViewById(R.id.phoneInput);
        mCodeText = (EditText) findViewById(R.id.codeInput);

        mPhoneBar = (ProgressBar) findViewById(R.id.phoneProgressBar);
        mCodeBar = (ProgressBar) findViewById(R.id.codeProgressBar);

        mVerifyBtn = (Button) findViewById(R.id.verifyButton);

        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhoneBar.setVisibility(View.VISIBLE);
                mPhoneText.setEnabled(false);
                mVerifyBtn.setEnabled(false);

                String phoneNumber = mPhoneText.getText().toString();


                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG,"onVerificationCompleted");
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        Log.w(TAG, "onVerificationFailed", e);

                    }
                };

                PhoneAuthProvider.getInstance()
                        .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, AuthActivity.this, mCallbacks );
            }
        });

    }
}

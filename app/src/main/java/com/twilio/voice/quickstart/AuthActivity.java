package com.twilio.voice.quickstart;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    private LinearLayout mPhoneLayout;
    private LinearLayout mCodeLayout;

    private EditText mPhoneText;
    private EditText mCodeText;

    private TextView mErrorMsg;

    private ProgressBar mPhoneBar;
    private ProgressBar mCodeBar;

    private Button mVerifyBtn;
    private String mVerifyType = "phone";

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


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

        mErrorMsg = (TextView) findViewById(R.id.errorMsg);

        mAuth = FirebaseAuth.getInstance();



        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mVerifyType == "phone") {
                    mPhoneBar.setVisibility(View.VISIBLE);
                    mPhoneText.setEnabled(false);
                    mVerifyBtn.setEnabled(false);

                    String phoneNumber = mPhoneText.getText().toString();

                    PhoneAuthProvider.getInstance()
                            .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, AuthActivity.this, mCallbacks );
                } else {
                    mVerifyBtn.setEnabled(false);
                    mCodeBar.setVisibility(View.VISIBLE);

                    String verificationCode = mCodeText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG,"onVerificationCompleted");
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);
                mErrorMsg.setText("Is phone number correct ?");
                mErrorMsg.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                mVerifyType = "code";

                mPhoneBar.setVisibility(View.INVISIBLE);
                mCodeLayout.setVisibility(View.VISIBLE);

                mVerifyBtn.setText("Verify code");
                mVerifyBtn.setEnabled(true);
                // ...
            }
        };

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            Intent voiceIntent = new Intent(AuthActivity.this, VoiceActivity.class);
                            startActivity(voiceIntent);
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            mErrorMsg.setText("There was some error with log in");
                            mErrorMsg.setVisibility(View.VISIBLE);
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                            }
                        }
                    }
                });
    }


}

package com.stued.StuEd.Login_and_signup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.stued.StuEd.Model_Classes.Users;
import com.stued.StuEd.R;
import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.Student_ui.dashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class signup2 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";
    private EditText mPhonenoField;
    private EditText motp;
    private Button start;
    private Button verify;
    private Button resend;
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    boolean mVerficationInProgress = false;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabse;
    private boolean doubleBackToExitPressedOnce = false;
    private int flag = 0;
    Dialog dialog12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabse = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(signup2.this)).getString("collegeName")).child("Users");
        mPhonenoField = findViewById(R.id.editText);
        motp = findViewById(R.id.editText2);
        start = findViewById(R.id.mstart);
        verify = findViewById(R.id.mverify);
        resend = findViewById(R.id.mresend);
        start.setOnClickListener(this);
        verify.setOnClickListener(this);
        resend.setOnClickListener(this);
        resend.setClickable(false);
        dialog12 = new Dialog(signup2.this);
        dialog12.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog12.setContentView(R.layout.loading_fragment);
        dialog12.setCanceledOnTouchOutside(false);
        dialog12.cancel();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential Credential) {
                Log.d(TAG, "onVerificationCompleted:" + Credential);
                mVerficationInProgress = false;
                linkuser(Credential);
                addphoneno(mPhonenoField.getText().toString());
                enableEdittext();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                enableEdittext();
                Log.w(TAG, "onVerificationFailed:", e);
                mVerficationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhonenoField.setError("Invalid Phone Number");
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "Quota Exceeded", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG, "onCodeSent" + s);
                Toast.makeText(signup2.this, "Code Sent", Toast.LENGTH_LONG).show();
                mVerificationId = s;
                mResendToken = forceResendingToken;
                enableEdittext();
            }
        };

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Toast.makeText(signup2.this, "Enter phone no. to continue", Toast.LENGTH_SHORT).show();

        super.onBackPressed();  // optional depending on your needs
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableEdittext();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mVerficationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhonenoField.getText().toString());
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhonenoField.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber) || !Patterns.PHONE.matcher(phoneNumber).matches()) {
            mPhonenoField.setError("Invalid phone number");
            return false;
        }
        return true;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerficationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerficationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        disableEdittext();
        if (verificationId == null || code == null) {
            Toast.makeText(this, "Send the OTP", Toast.LENGTH_SHORT).show();
            enableEdittext();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        linkuser(credential);

    }

    private void startPhoneNumberVerification(String phoneNumber) {
        disableEdittext();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        mVerficationInProgress = true;

    }

    private void resendVerificationCode(String phoneno, PhoneAuthProvider.ForceResendingToken Token) {
        disableEdittext();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneno,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                Token);
        Toast.makeText(signup2.this, "Code Sent", Toast.LENGTH_SHORT).show();
    }

    public void linkuser(PhoneAuthCredential credential) {
        disableEdittext();
        final Intent intent8 = new Intent(this, dashboard.class);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            addphoneno(mPhonenoField.getText().toString());
                            FirebaseUser user = task.getResult().getUser();
                            enableEdittext();
                            startActivity(intent8);

                        } else {
                            enableEdittext();
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                motp.setError("Invalid code.");
                                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void addphoneno(final String phoneno) {
        DatabaseReference addphone = FirebaseDatabase.getInstance()
                .getReference((new TinyDBorderID(this)).getString("collegeName"))
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        addphone.child("PhoneNo").setValue(phoneno);
    }

    private void enableEdittext() {
        dialog12.cancel();
        dialog12.setCanceledOnTouchOutside(false);
        motp.setEnabled(true);
        mPhonenoField.setEnabled(true);

    }

    private void disableEdittext() {
        dialog12.show();
        dialog12.setCanceledOnTouchOutside(false);
        motp.setEnabled(false);
        mPhonenoField.setEnabled(false);
    }

    void sendOTP() {
        disableEdittext();
        mDatabse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    if (mPhonenoField.getText().toString().equals(user.PhoneNo)) {
                        Toast.makeText(signup2.this, "This phone number is already linked with another account,Kindly use another phone number!!", Toast.LENGTH_LONG).show();
                        enableEdittext();
                        return;
                    }
                }
                resend.setClickable(true);
                startPhoneNumberVerification(mPhonenoField.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.mstart:
                if (!validatePhoneNumber()) {
                    return;
                } else {
                   // startPhoneNumberVerification(mPhonenoField.getText().toString());
                    sendOTP();
                }
                break;
            case R.id.mverify:
                String code = motp.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    motp.setError("Cannot be empty");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.mresend:
                if (!validatePhoneNumber()) return;
                resendVerificationCode(mPhonenoField.getText().toString(), mResendToken);
                break;
        }
    }
}

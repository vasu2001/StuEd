package com.stued.StuEd.Login_and_signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.concurrent.TimeUnit;

public class signup2 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG="PhoneAuthActivity";
    private EditText mPhonenoField;
    private EditText motp;
    private Button start;
    private Button verify;
    private Button resend;
    private static final String KEY_VERIFY_IN_PROGRESS="key_verify_in_progress";
    boolean mVerficationInProgress=false;
    private ProgressBar progressBar;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    //private FirebaseDatabase mDatabse;
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        if(savedInstanceState!=null)
        {
            onRestoreInstanceState(savedInstanceState);
        }
        progressBar=findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();
        //mDatabse=FirebaseDatabase.getInstance();
        mPhonenoField=findViewById(R.id.editText);
        motp=findViewById(R.id.editText2);
        start=findViewById(R.id.mstart);
        verify=findViewById(R.id.mverify);
        resend=findViewById(R.id.mresend);
        start.setOnClickListener(this);
        verify.setOnClickListener(this);
        resend.setOnClickListener(this);

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential Credential) {
                Log.d(TAG,"onVerificationCompleted:"+Credential);
                mVerficationInProgress=false;
                linkuser(Credential);
                addphoneno(mPhonenoField.getText().toString());
                progressBar.setVisibility(View.INVISIBLE);
                motp.setFocusable(true);
                mPhonenoField.setFocusable(true);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                motp.setFocusable(true);
                mPhonenoField.setFocusable(true);
                Log.w(TAG,"onVerificationFailed:",e);
                mVerficationInProgress=false;
                if(e instanceof FirebaseAuthInvalidCredentialsException)
                {
                    mPhonenoField.setError("Invalid Phone Number");
                }
                else if(e instanceof FirebaseAuthInvalidCredentialsException)
                {
                    Toast.makeText(getApplicationContext(),"Quota Exceeded",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG,"onCodeSent"+ s);
                Toast.makeText(signup2.this,"Code Sent",Toast.LENGTH_LONG).show();
                mVerificationId=s;
                mResendToken=forceResendingToken;
                progressBar.setVisibility(View.INVISIBLE);
                motp.setFocusable(true);
                mPhonenoField.setFocusable(true);
            }
        };

    }
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
        Toast.makeText(signup2.this,"Enter phone no. to continue",Toast.LENGTH_SHORT).show();

        super.onBackPressed();  // optional depending on your needs
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
        motp.setFocusable(true);
        mPhonenoField.setFocusable(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mVerficationInProgress && validatePhoneNumber())
        {
            startPhoneNumberVerification(mPhonenoField.getText().toString());
        }
    }

    private  boolean validatePhoneNumber()
    {
        String phoneNumber=mPhonenoField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber))
        {
            mPhonenoField.setError("Invalid phone number");
            return false;
        }
        return true;
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putBoolean(KEY_VERIFY_IN_PROGRESS,mVerficationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerficationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        progressBar.setVisibility(View.VISIBLE);
        motp.setFocusable(false);
        mPhonenoField.setFocusable(false);
        if(verificationId==null || code==null){
            Toast.makeText(this,"Send the OTP",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            motp.setFocusable(true);
            mPhonenoField.setFocusable(true);
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        linkuser(credential);

    }
    private void startPhoneNumberVerification(String phoneNumber)
    {
        progressBar.setVisibility(View.VISIBLE);
        motp.setFocusable(false);
        mPhonenoField.setFocusable(false);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        mVerficationInProgress= true;

    }

    private void resendVerificationCode(String phoneno,PhoneAuthProvider.ForceResendingToken Token)
    {
        progressBar.setVisibility(View.VISIBLE);
        motp.setFocusable(false);
        mPhonenoField.setFocusable(false);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneno,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                Token);
        Toast.makeText(signup2.this,"Code Sent",Toast.LENGTH_LONG).show();
    }

    public void linkuser(PhoneAuthCredential credential)
    {
        progressBar.setVisibility(View.VISIBLE);
        motp.setFocusable(false);
        mPhonenoField.setFocusable(false);
        final Intent intent8=new Intent(this, dashboard.class);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            addphoneno(mPhonenoField.getText().toString());
                            FirebaseUser user = task.getResult().getUser();
                            progressBar.setVisibility(View.INVISIBLE);
                            motp.setFocusable(true);
                            mPhonenoField.setFocusable(true);
                            startActivity(intent8);

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            motp.setFocusable(true);
                            mPhonenoField.setFocusable(true);
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                motp.setError("Invalid code.");
                                Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

public void addphoneno(final String phoneno)
{
    DatabaseReference addphone=FirebaseDatabase.getInstance()
            .getReference((new TinyDBorderID(this)).getString("collegeName"))
            .child("Users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    addphone.child("PhoneNo").setValue(phoneno);
}

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.mstart:
                if(!validatePhoneNumber())
                {
                    return;
                }
                startPhoneNumberVerification(mPhonenoField.getText().toString());
                break;
            case R.id.mverify:
                String code=motp.getText().toString();
                if(TextUtils.isEmpty(code))
                {
                    motp.setError("Cannot be empty");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId,code);
                break;
            case R.id.mresend:
                resendVerificationCode(mPhonenoField.getText().toString(),mResendToken);
                break;
        }
    }
}

package com.stued.StuEd.Login_and_signup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.Model_Classes.Users;
import com.stued.StuEd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class signup1 extends AppCompatActivity {
    Intent intent;
    private EditText inputEmail, inputPassword,inputUsername,inputRetypepass;

    private String userId;
    private String email;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);
        inputEmail = (EditText) findViewById(R.id.editText5);
        inputPassword = (EditText) findViewById(R.id.editText3);
        inputRetypepass=(EditText)findViewById(R.id.retypeass);
        inputUsername = (EditText) findViewById(R.id.username);
        mDatabase=FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dialog=new Dialog(signup1.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_fragment);
        dialog.cancel();
        final Spinner spinner=findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getBaseContext(),R.array.collegename,R.layout.spinneritem);
        adapter.setDropDownViewResource(R.layout.spinneritemdropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinner1;
                spinner1 = spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
      enableEdittext();
    }
    private void enableEdittext()
    {
        dialog.cancel();
        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
        inputRetypepass.setEnabled(true);
        inputUsername.setEnabled(true);
    }
    private void disableEdittext()
    {
       dialog.show();
        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
        inputRetypepass.setEnabled(false);
        inputUsername.setEnabled(false);
    }

    public void next2(View view)
    {
        final String emailInput = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString();
        String retypepass=inputRetypepass.getText().toString();
        final String username = inputUsername.getText().toString();
        final String phoneno="";
        final String TeacherAc="";
        disableEdittext();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
            enableEdittext();
            return;
        }

        if (TextUtils.isEmpty(emailInput)&& Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Toast.makeText(getApplicationContext(), "Enter valid email address!", Toast.LENGTH_SHORT).show();
            enableEdittext();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            enableEdittext();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 8 characters!", Toast.LENGTH_SHORT).show();
            enableEdittext();
            return;
        }
        if(password.equals(retypepass)==false) {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            enableEdittext();
                return;
        }

        //create user
        mAuth.createUserWithEmailAndPassword(emailInput, password)
                .addOnCompleteListener(signup1.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      disableEdittext();

                        if (!task.isSuccessful()) {
                            Toast.makeText(signup1.this,task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("MyTag", task.getException().toString());
                          enableEdittext();


                        } else {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {

                                                Users users=new Users(
                                                username,
                                                emailInput,
                                                phoneno,
                                                TeacherAc
                                        );

                                        mDatabase.getReference((new TinyDBorderID(signup1.this)).getString("collegeName")).child("Users")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .setValue(users)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(signup1.this,"Registered Successfully.Please check your email for verification",Toast.LENGTH_LONG).show();
                                                        Intent intent22=new Intent(signup1.this, MainActivity.class);
                                                        ///intent22.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent22);
                                                        finish();
                                                    }
                                                });
                                    }

                                }
                            });



                        }
                    }
                });


    }


//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String text= adapterView.getItemAtPosition(i).toString();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }
}

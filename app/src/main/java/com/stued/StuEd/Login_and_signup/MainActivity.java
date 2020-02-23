package com.stued.StuEd.Login_and_signup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.Model_Classes.Users;
import com.stued.StuEd.R;
import com.stued.StuEd.Student_ui.dashboard;
import com.stued.StuEd.Tutor_ui.TutorDashboard;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Intent intent5, intent6;
    private EditText inputEmail, inputPassword;
    TextView forgetpassw;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSiginInClient;
    private CallbackManager mCallbackManager;
    private DatabaseReference userLogin;
    private FirebaseAuth mAuth5;
    ProgressBar progressBar5;
    String answer, op;
    private FirebaseDatabase mDatabase;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        answer = "";
        op = "yes";
        TinyDBorderID tinyDBorderID = new TinyDBorderID(this);
        tinyDBorderID.putString("collegeName", "JIIT");
        inputEmail = findViewById(R.id.email5);
        inputPassword = findViewById(R.id.password5);
        forgetpassw = findViewById(R.id.forgetpass);
        mDatabase = FirebaseDatabase.getInstance();
        forgetpassw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, forgetPassword.class);
                startActivity(intent);
            }
        });

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_fragment);
        dialog.setCanceledOnTouchOutside(false);
        dialog.cancel();
        //  progressBar5=findViewById(R.id.progressBar5);
        final Spinner spinner = findViewById(R.id.spinner4);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.collegename, R.layout.spinneritem);
        adapter.setDropDownViewResource(R.layout.spinneritemdropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinner1;
                spinner1 = spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mAuth5 = FirebaseAuth.getInstance();
        userLogin = FirebaseDatabase.getInstance().getReference(tinyDBorderID.getString("collegeName")).child("Users");
        String s = "656376251395-5jpuvti5ag4dv65mcjfclsb21q0abddh.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(s)
                .requestEmail()
                .build();
        mGoogleSiginInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.gsignin);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInToGoogle();
            }
        });
        checkcurrentuser();
    }

    public void signInToGoogle() {
        Intent signInIntent = mGoogleSiginInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google Sign in succeeded", Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign in failed " + e, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        disableEdittext();
        final Intent intent = new Intent(this, signup2.class);
        final String personName = acct.getDisplayName();
        final String personEmail = acct.getEmail();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth5.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            enableEdittext();
                            boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (newuser) {
                                FirebaseUser user = mAuth5.getCurrentUser();
                                Log.d(TAG, "signin with credentials:success: current user:" + user.getEmail());
                                Toast.makeText(MainActivity.this, "Firebase Authentication Succeeded", Toast.LENGTH_LONG).show();
                                mAuth5 = FirebaseAuth.getInstance();
                                FirebaseUser newUser = mAuth5.getCurrentUser();
                                String phoneno = "";
                                try {
                                    phoneno = newUser.getPhoneNumber();

                                } catch (NullPointerException e) {
                                    phoneno = "";
                                }

                                if(phoneno==null)phoneno="";

                                String TeacherAc = "";
                                Users users = new Users
                                        (
                                                personName,
                                                personEmail,
                                                phoneno,
                                                TeacherAc
                                        );

                                mDatabase.getReference((new TinyDBorderID(MainActivity.this)).getString("collegeName")).child("Users")
                                        .child(mAuth5.getCurrentUser().getUid())
                                        .setValue(users)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(MainActivity.this, "Registered Successfully!", Toast.LENGTH_LONG).show();
                                                Intent intent22;
                                                intent22 = new Intent(MainActivity.this, signup2.class);
                                                startActivity(intent22);
                                                finish();

                                            }
                                        });
                            } else {
                                final FirebaseUser user = mAuth5.getCurrentUser();
                                userLogin.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String userType = dataSnapshot.child("TeacherAc").getValue(String.class);
                                        String userno = dataSnapshot.child("PhoneNo").getValue(String.class);
                                        if (userno.equals("")) {
                                            Toast.makeText(MainActivity.this, "Kindly Add Phone number to continue", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MainActivity.this, signup2.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            if (userType.equals("yes")) {
                                                Intent finalIntent = new Intent(MainActivity.this, TutorDashboard.class);
                                                finalIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                finalIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(finalIntent);
                                                finish();
                                            } else {
                                                Intent finalIntent2 = new Intent(MainActivity.this, dashboard.class);
                                                finalIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                finalIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(finalIntent2);
                                                finish();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            enableEdittext();
                            Log.w(TAG, "siginWithCredentials: failure", task.getException());
                            Toast.makeText(MainActivity.this, "Firebase Authentication failed:", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog.cancel();
    }

    public void checkcurrentuser() {

        disableEdittext();
        //check current user
        final FirebaseUser user = mAuth5.getCurrentUser();


        if (user != null && mAuth5.getCurrentUser().isEmailVerified()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent homeIntent = new Intent(MainActivity.this, Splashscreen.class);
                    startActivity(homeIntent);
                    //homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // User is signed in
                    userLogin.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String userType = dataSnapshot.child("TeacherAc").getValue(String.class);
                            String userno = dataSnapshot.child("PhoneNo").getValue(String.class);
                            FirebaseUser newUser = mAuth5.getCurrentUser();

                            if (userno.equals("")) {
                                Toast.makeText(MainActivity.this, "Kindly Add Phone number to continue", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, signup2.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                if (userType.equals("yes")) {
                                    Intent finalIntent = new Intent(MainActivity.this, TutorDashboard.class);
                                    finalIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finalIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(finalIntent);
                                    finish();
                                } else {
                                    Intent finalIntent2 = new Intent(MainActivity.this, dashboard.class);
                                    finalIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finalIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(finalIntent2);
                                    finish();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }, 0);


        } else {
            // User is signed out
            Log.d("status=", "onAuthStateChanged:signed_out");
            enableEdittext();
        }


    }


    public void signup(View view) {
        intent6 = new Intent(getApplicationContext(), signup1.class);
        startActivity(intent6);
    }

    public void login(View view) {
        disableEdittext();
        String email44 = inputEmail.getText().toString().trim();
        final String password44 = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email44)) {

            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            enableEdittext();

            return;
        }

        if (TextUtils.isEmpty(password44)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            enableEdittext();
            return;
        }

        //authenticate user
        mAuth5.signInWithEmailAndPassword(email44, password44)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mAuth5.getCurrentUser().isEmailVerified()) {
                                //  Intent finalIntent= new Intent(MainActivity.this,dashboard.class);
                                userLogin.child(mAuth5.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String userType = dataSnapshot.child("TeacherAc").getValue(String.class);
                                        String phoneno = dataSnapshot.child("PhoneNo").getValue(String.class);
                                        if (phoneno.equals("")) {
                                            Toast.makeText(MainActivity.this, "Kindly Add Phone number to continue", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MainActivity.this, signup2.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            if (userType.equals("yes")) {
                                                Intent finalIntent = new Intent(MainActivity.this, TutorDashboard.class);
                                                finalIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(finalIntent);
                                                finish();
                                            } else {
                                                Intent finalIntent2 = new Intent(MainActivity.this, dashboard.class);
                                                finalIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(finalIntent2);
                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(MainActivity.this, "Please verify your email Address", Toast.LENGTH_LONG).show();
                                enableEdittext();
                            }

                        } else {
                            // there was an error
                            Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            Log.e("MyTag", task.getException().toString());
                            enableEdittext();
                        }
                    }
                });
    }


    private void enableEdittext() {
        dialog.dismiss();
        dialog.setCanceledOnTouchOutside(false);
        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
    }

    private void disableEdittext() {

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dialog.dismiss();
    }
}

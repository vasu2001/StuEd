 package com.stued.StuEd.Student_ui;

 import android.app.Dialog;
 import android.content.Intent;
 import android.net.Uri;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.view.Window;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.fragment.app.Fragment;

 import com.stued.StuEd.Login_and_signup.MainActivity;
 import com.stued.StuEd.Login_and_signup.signup2;
 import com.stued.StuEd.Model_Classes.TinyDBorderID;
 import com.stued.StuEd.R;
 import com.google.android.gms.auth.api.signin.GoogleSignIn;
 import com.google.android.gms.auth.api.signin.GoogleSignInClient;
 import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.Task;
 import com.google.firebase.auth.AuthResult;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.auth.UserInfo;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;

 import java.util.List;


 public class SettingsFragment extends Fragment {

private Button changePhoneNo,changeusername,signout;
private FirebaseAuth auth;
private FirebaseUser user;
private DatabaseReference reference;
     private static final String TAG="GoogleActivity";
     private GoogleSignInClient mGoogleSiginInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.tutor_settings_fragment,container,false);
        changePhoneNo=view.findViewById(R.id.changePhoneNo2);
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users");
        changeusername=view.findViewById(R.id.button4);
        signout=view.findViewById(R.id.signout);


        String s="656376251395-5jpuvti5ag4dv65mcjfclsb21q0abddh.apps.googleusercontent.com";
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(s)
                .requestEmail()
                .build();

        mGoogleSiginInClient= GoogleSignIn.getClient(getActivity(),gso);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auth.signOut();
                        mGoogleSiginInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.w(TAG,"Signed out of Google");
                                Toast.makeText(getActivity(),"Signout success",Toast.LENGTH_LONG).show();
                                Intent intent3=new Intent(getActivity(), MainActivity.class);
                                intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent3);

                            }
                        });
                        Toast.makeText(getActivity(),"Signout success",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                });

            }
        });
        changeusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(getView().getContext());

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_username);
                dialog.show();
                final EditText newusername = dialog.findViewById(R.id.newUsername);
                final ImageView saveButton = dialog.findViewById(R.id.save22);
                final ImageView cancelButton = dialog.findViewById(R.id.cancel22);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reference.child(auth.getCurrentUser().getUid()).child("Username").setValue(newusername.getText().toString());
                        dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });


        changePhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = auth.getCurrentUser();
                List<? extends UserInfo> providerData = user.getProviderData();
                for (UserInfo userInfo : providerData ) {

                    String providerId = userInfo.getProviderId();
                    Log.d(TAG, "providerId = " + providerId);
                    if (providerId.equals("phone")) {
                        user.unlink(providerId)
                                .addOnCompleteListener(getActivity(),
                                        new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Handle error
                                                    reference.child(auth.getCurrentUser().getUid()).child("PhoneNo").setValue("");
                                                    Toast.makeText(getActivity(),"unlinked",Toast.LENGTH_LONG).show();
                                                    Intent intent=new Intent(getActivity(),signup2.class);
                                                    startActivity(intent);
                                                }
                                                else
                                                {
                                                    Toast.makeText(getActivity(),task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                    }

                }
            }
        });

        Button buttonTnc = (Button)view.findViewById(R.id.tnc);

        buttonTnc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://stuedstartup.wordpress.com/terms-and-conditons/"));
                startActivity(viewIntent);
            }
        });

        Button buttonCu = (Button)view.findViewById(R.id.cu);

        buttonCu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://stuedstartup.wordpress.com/about/"));
                startActivity(viewIntent);
            }
        });

        Button buttonFaq = (Button)view.findViewById(R.id.faq);

        buttonFaq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://stuedstartup.wordpress.com/faqs/"));
                startActivity(viewIntent);
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void setValues()
    {

    }

    public void init()
    {

    }

}

package com.stued.StuEd.Login_and_signup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.stued.StuEd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgetPassword extends AppCompatActivity {
    private  EditText email;
    private Button send;
    private FirebaseAuth firebaseAuth;
    private Dialog dialog;

    private static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        email=findViewById(R.id.email2222);
        send=findViewById(R.id.resetPassword);
        firebaseAuth=FirebaseAuth.getInstance();
        dialog=new Dialog(forgetPassword.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_fragment);
        dialog.setCanceledOnTouchOutside(false);
        dialog.dismiss();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                if(!isValidEmail(email.getText())){
                    dialog.dismiss();
                    Toast.makeText(view.getContext(),"Enter valid email",Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.sendPasswordResetEmail(email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(forgetPassword.this,"Password sent to your Email",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(forgetPassword.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(forgetPassword.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                         }
                });
            }
        });
    }
}

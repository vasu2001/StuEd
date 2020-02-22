package com.stued.StuEd.Student_ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    private TextView usernameAc,phoneNumberAc,emailAC,accountTag;
    private   DatabaseReference userData;
    private RatingBar ratingBar;
    View v;
    private ImageView logout,editpic,profile;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    //FirebaseStorage storage;
    StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.account,container,false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v=view;
        init();
        setValues();

        editpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profile.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public void setValues()
    {

        mAuth=FirebaseAuth.getInstance();
        userData = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users");
        emailAC.setText(mAuth.getCurrentUser().getEmail());
        emailAC.setSelected(true);

        Glide.with(v.getContext())
                .load(storageReference.child("profileImages").child(mAuth.getCurrentUser().getUid()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.usermaleicon)
                .into(profile);

       userData.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                   String s=dataSnapshot.child("Username").getValue().toString();
                    //String q=mAuth.getCurrentUser().getDisplayName();
                    String p=dataSnapshot.child("PhoneNo").getValue().toString();
                    usernameAc.setText(s);
                    phoneNumberAc.setText(p);
                    if(dataSnapshot.child("TeacherAc").getValue(String.class).equals("yes")){
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingBar.setStepSize((float)0.1);
                        float rating=0;
                        if(dataSnapshot.child("noOfRating").getValue(Integer.class)!=0)rating=dataSnapshot.child("rating").getValue(Float.class)/dataSnapshot.child("noOfRating").getValue(Integer.class);
                        ratingBar.setRating(rating);
                        accountTag.setText("Teacher");
                    }
                }
                else
                {
                    usernameAc.setText("nousername");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.w("info:","Error:"+databaseError.toString());
            }
        });
    }

    public void init()
    {
        usernameAc=v.findViewById(R.id.usernameac);
        phoneNumberAc = v.findViewById(R.id.phoneNumberac);
        emailAC = v.findViewById(R.id.emailAc);
        ratingBar = v.findViewById(R.id.ratingBar1);
        accountTag=v.findViewById(R.id.accountTag);
        editpic=v.findViewById(R.id.editprofilepic);
        profile=v.findViewById(R.id.profile_picture);
        storageReference =FirebaseStorage.getInstance().getReference();


    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("profileImages/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(v.getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(v.getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}

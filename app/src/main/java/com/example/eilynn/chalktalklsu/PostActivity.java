package com.example.eilynn.chalktalklsu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.media.MediaRouter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private EditText textDesc;
    private Button subButtn;
    private StorageReference storage;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //initializing objects
        subButtn = (Button) findViewById(R.id.subButton);
        textDesc = (EditText) findViewById(R.id.postText);
        storage = FirebaseStorage.getInstance().getReference();
        databaseRef = database.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        //get image from gallery
        imageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        // Post to Firebase
        subButtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(PostActivity.this, "Posting..", Toast.LENGTH_LONG).show();
                final String postDesc = textDesc.getText().toString().trim();
                //check for empty fields
                if(!TextUtils.isEmpty(postDesc)){
                    final StorageReference filePath = storage.child("post_images").child(uri.getLastPathSegment());
                    filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downUri = task.getResult();
                                final String downloadUrl = downUri.toString();
                                Toast.makeText(PostActivity.this, "Image store successfully", Toast.LENGTH_LONG).show();
                                final DatabaseReference newPost = databaseRef.push();
                                // add posts to database
                                mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        newPost.child("desc").setValue(postDesc);
                                        newPost.child("imageURL").setValue(downloadUrl);
                                        newPost.child("uid").setValue(mCurrentUser.getUid());
                                        newPost.child("username").setValue(dataSnapshot.child("Username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(PostActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            imageButton.setImageURI(uri);
        }
    }
}



package com.example.eilynn.chalktalklsu;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

public class myProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 2;
    private Button changeNameBtn, changeMajorBtn, changeYearBtn, changeBioBtn, changePic;
    private ImageButton profilePic;
    private EditText enterName, textMajor, textYear, textBio;
    private TextView displayName, viewMajor, viewYear;
    private Uri uri = null;
    private DatabaseReference dbRef;
    private StorageReference storage;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        changeNameBtn = findViewById(R.id.btn_ChangeDisplayName);
        displayName = findViewById(R.id.viewName);
        enterName = findViewById(R.id.editTextDisplayName);
        changePic = findViewById(R.id.profilepicbtn);
        changeMajorBtn = findViewById(R.id.changemajorbtn);
        changeYearBtn = findViewById(R.id.changeyearbtn);
        changeBioBtn = findViewById(R.id.changebiobtn);
        profilePic = findViewById(R.id.profilepicview);
        textMajor = findViewById(R.id.majortext);
        textYear = findViewById(R.id.yeartext);
        textBio = findViewById(R.id.biotext);
        viewMajor = findViewById(R.id.majorView);
        viewYear = findViewById(R.id.yearview);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        currentUser = auth.getCurrentUser();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Username").getValue().toString();
                displayName.setText(name);
                String major = dataSnapshot.child("Major").getValue().toString();
                viewMajor.setText(major);
                String year = dataSnapshot.child("Year").getValue().toString();
                viewYear.setText(year);
                String bio = dataSnapshot.child("Bio").getValue().toString();
                textBio.setText(bio);
                String pic = dataSnapshot.child("ProfilePicURL").getValue().toString();
                Picasso.get().load(pic).placeholder(R.mipmap.ic_launcher_foreground).into(profilePic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(myProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        changeNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newName = enterName.getText().toString();
                try{
                    databaseUsers.child("Username").setValue(newName);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                displayName.setText("");
            }
        });
        changeMajorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newMajor = textMajor.getText().toString();
                try{
                    databaseUsers.child("Major").setValue(newMajor);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                displayName.setText("");
            }
        });
        changeYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newYear = textYear.getText().toString();
                try{
                    databaseUsers.child("Year").setValue(newYear);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                displayName.setText("");
            }
        });
        changeBioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newBio = textBio.getText().toString();
                try{
                    databaseUsers.child("Bio").setValue(newBio);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                displayName.setText("");
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StorageReference filepath = storage.child("profile_pics").child(uri.getLastPathSegment());
                filepath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downUri = task.getResult();
                            final String downloadUrl = downUri.toString();
                            databaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try{
                                        databaseUsers.child("ProfilePicURL").setValue(downloadUrl);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            profilePic.setImageURI(uri);
        }
    }

}

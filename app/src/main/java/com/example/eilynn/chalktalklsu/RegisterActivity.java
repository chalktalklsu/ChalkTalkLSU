package com.example.eilynn.chalktalklsu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button regButton;
    private EditText usernameText, emailText, passwordText;
    private FirebaseAuth auth;
    private DatabaseReference dB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regButton = findViewById(R.id.submit);
        usernameText = findViewById(R.id.textUsername);
        emailText = findViewById(R.id.textEmail);
        passwordText = findViewById(R.id.textPass);
        auth = FirebaseAuth.getInstance();
        dB = FirebaseDatabase.getInstance().getReference().child("Users");
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameText.getText().toString();
                final String email = emailText.getText().toString();
                final String pass = passwordText.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String userid = auth.getCurrentUser().getUid();
                            DatabaseReference currentUser = dB.child(userid);
                            currentUser.child("Username").setValue(username);
                            currentUser.child("Major").setValue("Undecided");
                            currentUser.child("Year").setValue("None");
                            currentUser.child("Bio").setValue("Add a bio");
                            currentUser.child("ProfilePicURL").setValue("https://firebasestorage.googleapis.com/v0/b/chalktalk-1abad.appspot.com/o/profile_pics%2Fic_launcher_foreground.png?alt=media&token=b3574797-c133-4800-99d9-801980988ecd");
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                }
            }
        });
    }
}

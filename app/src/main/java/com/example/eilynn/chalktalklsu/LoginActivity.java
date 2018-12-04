package com.example.eilynn.chalktalklsu;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private EditText userEmail, userPassword;
    private FirebaseAuth auth;
    private DatabaseReference dB;
    private Button loginButton, regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userEmail = findViewById(R.id.email);
        userPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.email_sign_in_button);
        regBtn = findViewById(R.id.toregbutton);
        auth = FirebaseAuth.getInstance();
        dB = FirebaseDatabase.getInstance().getReference().child("Users");
        regBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String pass = userPassword.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "User information invalid.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please enter both email and password.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
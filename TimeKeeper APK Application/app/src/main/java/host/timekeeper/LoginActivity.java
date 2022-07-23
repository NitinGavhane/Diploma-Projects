package host.timekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Context ctx;
    private FirebaseAuth mAuth;
    private Intent i;
    private EditText email,password;
    private LinearLayout progressV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;
        mAuth = FirebaseAuth.getInstance();
        isLoggedIn();

        email = findViewById(R.id.emailETAL);
        password = findViewById(R.id.passETAL);
        progressV = findViewById(R.id.progressLLAL);
    }

    private void isLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            i = new Intent(ctx, ProfileActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void registerPage(View view) {
        i = new Intent(ctx,RegisterActivity.class);
        startActivity(i);
        finish();
    }

    public void pwdReset(View view) {
        String mail = email.getText().toString();

        if(!Helpers.isValidEmail(mail)){
            email.setError("Invalid Email Address");
            return;
        }

        progressV.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(mail)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressV.setVisibility(View.INVISIBLE);
                Toast.makeText(ctx,"You will receive an email with password reset link in a few minutes.",Toast.LENGTH_SHORT)
                        .show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressV.setVisibility(View.INVISIBLE);
                Toast.makeText(ctx,"Error : "+e.getMessage(),Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void attemptLogin(View view) {
        String mail = email.getText().toString();
        String pwd = password.getText().toString();

        boolean err = false;

        if(!Helpers.isValidEmail(mail)){
            email.setError("Invalid Email Address");
            err = true;
        }

        if(!Helpers.isValidPass(pwd)){
            password.setError("Password should be at least 6 characters");
            err = true;
        }

        if(err) return;

        progressV.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(mail,pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressV.setVisibility(View.INVISIBLE);
                        isLoggedIn();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressV.setVisibility(View.INVISIBLE);
                        Toast.makeText(ctx,"Login Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

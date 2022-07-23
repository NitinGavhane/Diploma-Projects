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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private Context ctx;
    private FirebaseAuth mAuth;
    private Intent i;
    private EditText nameET, emailET, phoneET, passET, cpassET;
    private LinearLayout progressV;
    private User user;

    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ctx = this;
        mAuth = FirebaseAuth.getInstance();

        nameET = findViewById(R.id.nameETAR);
        emailET = findViewById(R.id.emailETAR);
        phoneET = findViewById(R.id.phoneETAR);
        passET = findViewById(R.id.passETAR);
        cpassET = findViewById(R.id.cpassETAR);
        progressV = findViewById(R.id.progressLLAR);

        user = User.getInstance();
    }


    private void isLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            i = new Intent(ctx, ProfileActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void loginPage(View view) {
        i = new Intent(ctx, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void createUser() {
        userRef = FirebaseFirestore.getInstance().collection("User").document(user.id);

        userRef.set(user.data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ctx,"Registration Successful",Toast.LENGTH_SHORT).show();
                        isLoggedIn();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void attemptRegistration(View view) {

        final String name = nameET.getText().toString();
        final String email = emailET.getText().toString();
        final String phone = phoneET.getText().toString();
        final String pwd = passET.getText().toString();
        final String cpwd = passET.getText().toString();

        boolean err = false;

        if (!Helpers.isValidName(name)) {
            nameET.setError("Name is required");
            err = true;
        }

        if (!Helpers.isValidEmail(email)) {
            emailET.setError("Invalid Email Address");
            err = true;
        }

        if (!Helpers.isValidPhone(phone)) {
            phoneET.setError("Invalid phone number, Enter a 10 digit phone number.");
            err = true;
        }

        if (!Helpers.isValidPass(pwd)) {
            passET.setError("Password should be at least 6 characters");
            err = true;
        }

        if (!pwd.equals(cpwd)) {
            cpassET.setError("Passwords don't match.");
            err = true;
        }

        if (err) return;

        progressV.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        user.id = mAuth.getCurrentUser().getUid();
                        user.setUser(name,phone);
                        createUser();
                        progressV.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ctx,"Registration Failed : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        progressV.setVisibility(View.INVISIBLE);
                    }
                });
    }

}

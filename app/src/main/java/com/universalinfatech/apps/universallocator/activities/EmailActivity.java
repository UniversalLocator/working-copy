
package com.universalinfatech.apps.universallocator.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.universalinfatech.apps.universallocator.R;

import java.util.List;

public class EmailActivity extends AppCompatActivity {

    private Context context;
    private FirebaseAuth mAuth;
    private EditText editTextemail,editTextpwd;
    private Button btnSubmit;
    private TextView _tvemaillink;
    String emailLink;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        context = this;
        editTextemail = (EditText) findViewById(R.id.emailid);
        _tvemaillink = (TextView) findViewById( R.id.emaillink );
        editTextpwd = (EditText) findViewById(R.id.emailpwd);
        btnSubmit = (Button) findViewById(R.id.btnsubmit);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();



        if (getIntent().getData() != null) {
            // intent is not null and your key is not null
            emailLink = getIntent().getData().toString();
        }

// Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            String email = "sumaiyaahmed28@gmail.com";

            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "signing in with email link", Toast.LENGTH_SHORT).show();
                                AuthResult result = task.getResult();
                                Toast.makeText(context, (CharSequence) result.getUser(), Toast.LENGTH_SHORT).show();
                                // You can access the new user via result.getUser()
                                // Additional user info profile *not* available via:
                                // result.getAdditionalUserInfo().getProfile() == null
                                // You can check if the user is new or existing:
                                // result.getAdditionalUserInfo().isNewUser()
                            } else {
                                Toast.makeText(context, "Error signing in with email link", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _emailid = editTextemail.getText().toString();
                String _password = editTextpwd.getText().toString();

                if(_emailid.isEmpty()){
                    editTextemail.setError("Enter OTP");

                }else {

                }
            }
        });
        final ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://universallocator-6bc9c.firebaseapp.com")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.universalinfatech.apps.universallocator",
                                true, /* installIfNotAvailable */
                                "15"    /* minimumVersion */)
                        .build();
        _tvemaillink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String _emailid = editTextemail.getText().toString();
                auth.sendSignInLinkToEmail(_emailid, actionCodeSettings)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Email sent !", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(context, "Email not sent !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

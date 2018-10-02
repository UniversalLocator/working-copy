package com.universalinfatech.apps.universallocator.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.universalinfatech.apps.universallocator.R;
import com.universalinfatech.apps.universallocator.others.SmsListener;
import com.universalinfatech.apps.universallocator.others.SmsReceiver;

import java.util.concurrent.TimeUnit;

//import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener{

    private Context context;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
   // private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String mUid;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private TextView _tv;
    String otp_generated;
    private EditText editTextph,editTextotp;
    private Button btnMobile,btnSubmit,btnEmail;
    private LinearLayout linearOption, linearMobile;
    private SignInButton btnGoogle;
    private TextView forgotPassword, btnfb;
    //private LoginButton btnFacebook;
    ProgressDialog progressDialog;
    private static final int RC_SIGN_IN = 9001;
    public static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;
        btnMobile = (Button) findViewById(R.id.btnMobile);
        btnEmail = (Button) findViewById(R.id.btnEmail);
        editTextph = (EditText) findViewById(R.id.phoneno);
        _tv = (TextView) findViewById( R.id.textView1 );
        editTextotp = (EditText) findViewById(R.id.otptxt);
        btnSubmit = (Button) findViewById(R.id.submit);
        linearOption = (LinearLayout) findViewById(R.id.linearOptions);
        linearMobile = (LinearLayout) findViewById(R.id.linearMobile);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        btnGoogle = (SignInButton) findViewById(R.id.sign_in_button);
        //btnFacebook = (LoginButton) findViewById(R.id.button_facebook_login);
        btnfb = (TextView) findViewById(R.id.btnfb);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webapi_string))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearOption.setVisibility(View.GONE);
                linearMobile.setVisibility(View.VISIBLE);

            }
        });
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EmailActivity.class);
                startActivity(intent);
                finish();

            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });
        btnfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //btnFacebook.performClick();

            }
        });
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.

                //mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]

                    Snackbar.make(findViewById(android.R.id.content), "Invalid Phone Number",
                            Snackbar.LENGTH_SHORT).show();
                    //mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]

                    Snackbar.make(findViewById(android.R.id.content), "An error occured while signing in.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                //updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Log.d(TAG, "onCodeSent:" + verificationId);
                progressDialog.dismiss();
                mResendToken = token;
                mVerificationId = verificationId;

                btnSubmit.setText("Verify");
                editTextph.setEnabled(false);
                editTextotp.setVisibility(View.VISIBLE);
                _tv.setVisibility(View.VISIBLE);
                // Save verification ID and resending token so we can use them later
                new CountDownTimer(60000, 1000) { // adjust the milli seconds here

                    public void onTick(long millisUntilFinished) {
                        _tv.setText(""+String.format("00:%d",
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    }

                    public void onFinish() {
                        _tv.setText("Resend");
                    }
                }.start();
                // [START_EXCLUDE]
                // Update UI
                //updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnSubmit.getText().toString().equalsIgnoreCase("Submit")) {

                    if (validate()) {
                        startPhoneNumberVerification(editTextph.getText().toString());
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Verifing...");
                        progressDialog.show();

                    } else {
                        //do nothing
                    }
                }else{
                    String code = editTextotp.getText().toString();

                    if(code.isEmpty()){
                        editTextotp.setError("Enter OTP");

                    }else {
                        //editTextotp.setError("");
                        verifyPhoneNumberWithCode(mVerificationId, code);

                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Verifing...");
                        progressDialog.show();

                    }
                }
            }
        });

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                editTextotp.setText(messageText);
            }
        });
        _tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    resendVerificationCode(editTextph.getText().toString(),
                            mResendToken);
                } catch (Exception e) {
                }
                if (editTextotp.getText().toString().equals(otp_generated)) {
                    Toast.makeText(context, "OTP Verified Successfully !", Toast.LENGTH_SHORT).show();
                }
            }
            });

    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(context, "Oops! something went wrong.." ,Toast.LENGTH_LONG).show();
            }
        } else{
            //mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(context, user.getUid(),
                                    Toast.LENGTH_SHORT).show();


                        } else {
                            googleSignout();
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }


    public boolean validate(){
        boolean flag;
        String mobileno = editTextph.getText().toString();
        if(mobileno.isEmpty()){
            flag = false;
            editTextph.setError("Enter mobile number");
        }else if(mobileno.length() < 10){
            flag = false;
            editTextph.setError("Enter a valid mobile number");
        }else{
            flag = true;
            //editTextph.setError("");
        }
        return flag;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = task.getResult().getUser();

                            mUid = user.getUid();
                            //Toast.makeText(context,"1",Toast.LENGTH_LONG).show();
                            if(user.getEmail() == null){
                                Toast.makeText(context,"User signed up successfully",Toast.LENGTH_LONG).show();
//                                SharedPreferences pref = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
//                                SharedPreferences.Editor editor = pref.edit();
//                                editor.putString(USERIDPREF, user.getUid());
//
//                                editor.commit();
//                                editor.apply();
                                progressDialog.dismiss();
                                Intent intent = new Intent(context,MainActivity.class);

                                intent.putExtra("credential",credential);
                                intent.putExtra("uid",mUid);
                                intent.putExtra("mobileno",editTextph.getText().toString());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }else{

                                progressDialog.dismiss();
                                Snackbar.make(findViewById(android.R.id.content), "User Already Exists",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                            // [START_EXCLUDE]
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            // [START_EXCLUDE silent]
                            // txtOTP.setError("Invalid code.");
                            // [END_EXCLUDE]
                            progressDialog.dismiss();
                            Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            //}
                            // [START_EXCLUDE silent]
                            // Update UI

                            // [END_EXCLUDE]
                        }
                    }
                });


    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public static void googleSignout(){

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                        //Toast.makeText(context, "successfully signedout", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

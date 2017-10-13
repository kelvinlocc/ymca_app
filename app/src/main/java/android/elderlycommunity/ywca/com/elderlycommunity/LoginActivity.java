package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.MainActivity.MainActivity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public final static String EMAIL = "@ywca.community.com";
    EditText inputId, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputId = (EditText) findViewById(R.id.inputId);
        inputPassword = (EditText) findViewById(R.id.inputPassword);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(App.TAG, "onAuthStateChanged:signed_in:" + user.getUid()
                    + "\ndisplay name: " + user.getDisplayName()
                    + "\nPhotoUrl (Uri object): " + user.getPhotoUrl());
                } else {
                    // User is signed out
                    Log.d(App.TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void Login(View v){
        String email = inputId.getText().toString() + EMAIL;
        String pw = inputPassword.getText().toString();

        if(email.isEmpty() || pw.isEmpty()){
            Toast.makeText(LoginActivity.this, "登入失敗，請檢查id/password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Checking your information");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        Log.d(App.TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUser.getToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {

                                                String token = App.getInstance().getStoredToken();
                                                if(token == null){
                                                    FirebaseAuth.getInstance().signOut();
                                                    App.getInstance().toastServerError();
                                                    return;
                                                }
                                                String userId = mUser.getEmail().replace(EMAIL, "").toUpperCase();
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                db.child("Users").child(userId).child("token").setValue(token);

                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                // must invoke finish()
                                                finish();
                                            } else {
                                                FirebaseAuth.getInstance().signOut();
                                                App.getInstance().toastServerError();
                                            }
                                        }
                                    });
                        }
                        else {
                            if(App.getInstance().isOnline()){
                                Log.w(App.TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "登入失敗，請檢查id/password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void showLoginGuide(View v){
        startActivity(new Intent(LoginActivity.this, LoginGuideActivity.class));
    }
}

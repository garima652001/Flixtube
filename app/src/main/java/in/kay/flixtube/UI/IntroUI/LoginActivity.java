package in.kay.flixtube.UI.IntroUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pixplicity.easyprefs.library.Prefs;
import com.sdsmdg.tastytoast.TastyToast;

import in.kay.flixtube.R;
import in.kay.flixtube.UI.HomeUI.MainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    EditText etpassword, etemail;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        etemail = findViewById(R.id.et_email);
        etpassword = findViewById(R.id.et_password);
        CheckUserLogin();
        findViewById(R.id.tv_signup).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_forgot).setOnClickListener(this);
    }

    private void CheckUserLogin() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            GotoHome();
        }
    }

    private void GotoHome() {
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void Login() {
        email = etemail.getText().toString();
        password = etpassword.getText().toString();

        if (email.isEmpty()) {
            etemail.setError("Email cannot be empty");
            etemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("Please enter a valid email id");
            etemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etpassword.setError("Password cannot be empty");
            etpassword.requestFocus();
            return;
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            TastyToast.makeText(LoginActivity.this,"Welcome",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                            GotoHome();
                        } else {
                            TastyToast.makeText(LoginActivity.this,"Your mail isn't verified !!",TastyToast.LENGTH_LONG,TastyToast.WARNING);
                        }
                    } else {
                        TastyToast.makeText(LoginActivity.this,task.getException().getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }
            });

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_signup:
                Intent intent=new Intent(LoginActivity.this, SignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btn_login:
                Login();
                break;

            case R.id.tv_forgot:
                String email = etemail.getText().toString();
                if (email.isEmpty()) {
                    etemail.setError("Email cannot be empty");
                    etemail.requestFocus();
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Reset link has been sent", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


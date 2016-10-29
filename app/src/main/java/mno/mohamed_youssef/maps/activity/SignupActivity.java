package mno.mohamed_youssef.maps.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import mno.mohamed_youssef.maps.R;
import mno.mohamed_youssef.maps.notification.GcmIntentService;
import mno.mohamed_youssef.maps.tasks.LocationTask;
import mno.mohamed_youssef.maps.tasks.SingupTask;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private SingupTask singupTask;
    private SharedPreferences sharedPreferences;

    private int delayedTime =1000;
    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);


        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        singupTask = new SingupTask(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        if (singupTask.getStatus() == AsyncTask.Status.FINISHED) {
            singupTask = new SingupTask(getApplication());
        }
        if (singupTask.getStatus() != AsyncTask.Status.RUNNING) {
            singupTask.execute(name,email,password);
        }


        // TODO: Implement your own signup logic here.


                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        if (sharedPreferences.getInt("id", 0) != 0) {
                                            onSignupSuccess();
                                            progressDialog.dismiss();
                                        } else {
                                            onSignupFailed();
                                            progressDialog.dismiss();


                                        }


                                    }

                                }, 3000);

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        sharedPreferences.edit().putString("name", _nameText.getText().toString()).commit();
        sharedPreferences.edit().putString("password",_passwordText.getText().toString()).commit();
        sharedPreferences.edit().putString("email", _emailText.getText().toString()).commit();
        startActivity(new Intent(this,WelecomeActivity.class));

        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
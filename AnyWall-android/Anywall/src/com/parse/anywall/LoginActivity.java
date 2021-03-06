package com.parse.anywall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends Activity {
  // UI references.
  private EditText usernameEditText;
  private EditText passwordEditText;

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    String username = null;
    Bundle extras = getIntent().getExtras();

    // Set up the login form.
    usernameEditText = (EditText) findViewById(R.id.username);
    passwordEditText = (EditText) findViewById(R.id.password_edit_text);
    passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == R.id.edittext_action_login ||
            actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
          login();
          return true;
        }
        return false;
      }
    });

    // Check if we have a reasonable extras Bundle, because I guess sometimes it can be null... -.-
    if (extras != null) {
      // Check if we can prefill the username
      if (extras.containsKey("EXTRA_USER_USERNAME") ) {
        // We tried to set a key

        username = extras.getString("EXTRA_USER_USERNAME", null);
        if (username != null) {
          // The extra value is not null. Prefill username
          usernameEditText.setText(username);
        }
      }
    }

    // Set up the submit button click handler
    Button actionButton = (Button) findViewById(R.id.action_button);
    actionButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        login();
      }
    });

    // Set up the forgot password button click handler
    Button forgotPasswordButton = (Button) findViewById(R.id.forgot_password_button);
    forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        forgotPassword();
      }
    });

    // Set up the forgot username button click handler
    Button forgotUsernameButton = (Button) findViewById(R.id.forgot_username_button);
    forgotUsernameButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        forgotUsername();
      }
    });
  }

  private void forgotPassword() {
    // Start an intent for the dispatch activity
    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);

  }

  private void forgotUsername() {
    // Start an intent for the dispatch activity
    Intent intent = new Intent(LoginActivity.this, ForgotUsernameActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  private void login() {
    String username = usernameEditText.getText().toString().trim();
    String password = passwordEditText.getText().toString().trim();

    // Validate the log in data
    boolean validationError = false;
    StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
    if (username.length() == 0) {
      validationError = true;
      validationErrorMessage.append(getString(R.string.error_blank_username));
    }
    if (password.length() == 0) {
      if (validationError) {
        validationErrorMessage.append(getString(R.string.error_join));
      }
      validationError = true;
      validationErrorMessage.append(getString(R.string.error_blank_password));
    }
    validationErrorMessage.append(getString(R.string.error_end));

    // If there is a validation error, display the error
    if (validationError) {
      Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
          .show();
      return;
    }

    // Set up a progress dialog
    final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
    dialog.setMessage(getString(R.string.progress_login));
    dialog.show();
    // Call the Parse login method
    ParseUser.logInInBackground(username, password, new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        dialog.dismiss();
        if (e != null) {
          // Show the error message
          Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
          // Start an intent for the dispatch activity
          Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        }
      }
    });
  }
}

package thomson.laurel.beth.thymesaver.UI;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import thomson.laurel.beth.thymesaver.Database.Firebase.PantryManagerRepository;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.TopLevel.MainActivity;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class LauncherActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN_INITIAL = 100;
    private static final int RC_SIGN_IN_SECONDARY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if the user is already logged in, we don't want to launch the sign in flow
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            onSignInSuccess();
        }
        else {
            signIn(true);
        }
        setTheme(R.style.AppTheme);
    }

    private void signIn(boolean isInitial) {
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.layout_auth_method_picker)
                .setEmailButtonId(R.id.email_signin_button)
                .setAnonymousButtonId(R.id.guest_signin_button)
                .build();


        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

        int signInType = isInitial ? RC_SIGN_IN_INITIAL : RC_SIGN_IN_SECONDARY;

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .enableAnonymousUsersAutoUpgrade()
                        .setTheme(R.style.AuthTheme)
                        .setAuthMethodPickerLayout(customLayout)
                        .build(),
                signInType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_INITIAL) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                onSignInSuccess();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // We don't want to cancel sign in, so we'll just retry if they hit the
                // back button
                signIn(true);
            }
        }
        else if (requestCode == RC_SIGN_IN_SECONDARY) {
            //restart application
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    private void onSignInSuccess() {
        PantryManagerRepository repository = PantryManagerRepository.getInstance();
        repository.initializePreferredPantry(new Callback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String err) {

            }
        });
    }
}

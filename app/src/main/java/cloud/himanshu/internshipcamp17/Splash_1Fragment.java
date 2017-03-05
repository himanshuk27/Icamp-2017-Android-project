package cloud.himanshu.internshipcamp17;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class Splash_1Fragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private Button email;
    private SignInButton google;
    private static final int RC_SIGN_IN = 007;
    private String gemail;
    private ProgressDialog progressDialog;

    private GoogleApiClient mGoogleApiClient;

    public Splash_1Fragment() {
        // Required empty public constructor
    }
    public static Splash_1Fragment newInstance() {
        Splash_1Fragment fragment = new Splash_1Fragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash_1, container, false);
        email = (Button)rootView.findViewById(R.id.but_email);
        google = (SignInButton) rootView.findViewById(R.id.but_google);
        TextView tv = (TextView)google.getChildAt(0);
        tv.setText("Continue with Google");
        progressDialog = new ProgressDialog(getContext());

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SplashActivity)getActivity()).changeFragment(2);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void startMain() {
        Intent intent = new Intent(getContext(), DashBoardActivity.class);
        intent.putExtra("Type", "Google");
        startActivity(intent);
        getActivity().finish();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            gemail = acct.getEmail();
            new InitGLogin().execute();
        } else {
            // Signed out, show unauthenticated UI.
            ((SplashActivity)getActivity()).showSnackbar("Signin Failed!");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public class InitGLogin extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/validate_login.php?gemail="
                    +gemail;
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            SharedPreferences pref = getContext().getSharedPreferences("Cur_User", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            progressDialog.dismiss();
            if(result!=null) {
                if (result.equals("Success!\n")) {
                    editor.putBoolean("LoggedIn", true);
                    editor.putString("Type", "Google");
                    editor.putString("Email", gemail);
                    editor.putBoolean("Paid", true);
                    editor.putBoolean("Verified", true);

                    editor.apply();

                    startMain();


                }

                else if (result.equals("Email Pending!\n")) {
                    editor.putBoolean("LoggedIn", true);
                    editor.putString("Type", "Google");
                    editor.putString("Email", gemail);
                    editor.putBoolean("Paid", false);
                    editor.putBoolean("Verified", false);
                    editor.apply();

                    Intent intent = new Intent(getContext(), Email_VerifyActivity.class);
                    intent.putExtra("Email", gemail);
                    startActivity(intent);
                    getActivity().finish();
                }

                else if (result.equals("Payment Pending!\n")) {
                    editor.putBoolean("LoggedIn", true);
                    editor.putString("Type", "Google");
                    editor.putString("Email", gemail);
                    editor.putBoolean("Paid", false);
                    editor.putBoolean("Verified", true);
                    editor.apply();

                    startActivity(new Intent(getContext(), PaymentActivity.class));
                    getActivity().finish();
                }

                else {
                    Intent intent = new Intent(getContext(), Student_RegActivity.class);
                    intent.putExtra("Type", "Google");
                    intent.putExtra("Email", gemail);
                    startActivity(intent);
                }
            }

            else{
                ((SplashActivity)getActivity()).showSnackbar("Error reaching servers...");
            }
        }
    }
}

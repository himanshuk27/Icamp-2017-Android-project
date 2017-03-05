package cloud.himanshu.internshipcamp17;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Email_VerifyActivity extends AppCompatActivity {
    private Button_Lato_Light  resend, checkstatus;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private String jsonUrl;
    private String myJSON;
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email__verify);
        resend = (Button_Lato_Light) findViewById(R.id.startup);
        checkstatus = (Button_Lato_Light)findViewById(R.id.next);
        progressDialog = new ProgressDialog(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
        progressBar = (ProgressBar)findViewById(R.id.prog);
        pref = getSharedPreferences("Cur_User", MODE_PRIVATE);
        if(!pref.getBoolean("LoggedIn", false)){
            Toast.makeText(this, "Unauthorized Access", Toast.LENGTH_SHORT).show();
            logOut();
        }

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new ResendMail().execute();

            }
        });

        checkstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckStatus().execute();
                checkstatus.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });



    }

    @Override
    protected void onDestroy() {
        logOut();
        super.onDestroy();
    }

    public void logOut() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cur_User", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LoggedIn", false);
        editor.apply();
    }

    public class CheckStatus extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/resetpass.php?email_ver_status="
                    +getIntent().getStringExtra("Email");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {


                if (result.equals("No\n")) {
                    progressBar.setVisibility(View.GONE);
                    checkstatus.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Na ho pai!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                else if(result.equals("Yes\n")){
                    SharedPreferences pref = getSharedPreferences("Cur_User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("LoggedIn", true);
                    editor.putString("Type", "Normal");
                    editor.putString("Email", getIntent().getStringExtra("Email"));
                    editor.putBoolean("Verified", true);

                    editor.apply();

                    startActivity(new Intent(Email_VerifyActivity.this, PaymentActivity.class));
                }

                else{
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, result, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }

            else{
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Unable to reach servers...", Snackbar.LENGTH_LONG);
                snackbar.show();
            }


        }
    }

    public class ResendMail extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/misc_queries.php?resend_email="
                    +getIntent().getStringExtra("Email");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if (result.equals("Email Sent\n")) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Email Sent!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                else{
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, result, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            else{
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Unable to reach servers...", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }



}

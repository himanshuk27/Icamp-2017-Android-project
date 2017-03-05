package cloud.himanshu.internshipcamp17;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class Student_RegActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog progressDialog;
    private AuthCallback authCallback;
    private String type;

    private EditText_Lato_Light st_name, st_college, st_branch, st_roll;
    private RadioButton st_year_value, kiitradio, otherradio;
    private RadioGroup st_year, col_radio;
    private Button Next;
    private TextInputLayout ti_college;
    private SharedPreferences sharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__reg);

        Digits.logout();

        progressDialog = new ProgressDialog(this);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);

        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                Student.setContact(phoneNumber.substring(3));
                new RegProcess().execute();
            }

            @Override
            public void failure(DigitsException error) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Mobile verification failed! please try again.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        };

        Next = (Button)findViewById(R.id.next);

        st_name = (EditText_Lato_Light)findViewById(R.id.st_name);
        st_branch = (EditText_Lato_Light)findViewById(R.id.st_branch);
        st_roll = (EditText_Lato_Light)findViewById(R.id.st_roll);
        st_college = (EditText_Lato_Light)findViewById(R.id.st_college);
        col_radio = (RadioGroup)findViewById(R.id.col_radio);
        kiitradio = (RadioButton)findViewById(R.id.kiitradio);
        otherradio = (RadioButton)findViewById(R.id.otherradio);
        st_year = (RadioGroup)findViewById(R.id.st_year);
        ti_college = (TextInputLayout)findViewById(R.id.ti_college);
        type = getIntent().getStringExtra("Type");
        Student.newInstance();

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st_year_value = (RadioButton)findViewById(st_year.getCheckedRadioButtonId());
                if(checkData()==0)
                    prepareData();
                else
                    Toast.makeText(Student_RegActivity.this, "All Fields are Required!!", Toast.LENGTH_SHORT).show();

            }
        });

        col_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rb=(RadioButton)findViewById(checkedId);
                if(rb.getText().equals("KIIT University")){
                    ti_college.setVisibility(View.GONE);
                }
                else{
                    ti_college.setVisibility(View.VISIBLE);
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
        sharedPreferences = getSharedPreferences("Cur_User", MODE_PRIVATE);

    }

    private int checkData() {
        if(st_name.getText().toString().isEmpty() || st_name.getText().toString().equals(" "))
            return 1;
        if(st_branch.getText().toString().isEmpty() || st_branch.getText().toString().equals(" "))
            return 1;
        if(st_roll.getText().toString().isEmpty() || st_roll.getText().toString().equals(" "))
            return 1;
        if(st_year.getCheckedRadioButtonId() == -1)
            return 1;
        if(col_radio.getCheckedRadioButtonId() == -1)
            return 1;
        if(ti_college.getVisibility()==View.VISIBLE) {
            if (st_college.getText().toString().isEmpty() || st_college.getText().toString().equals(" "))
                return 1;
        }
        return 0;
    }

    void prepareData() {
        Student.setName(st_name.getText().toString());
        Student.setBranch(st_branch.getText().toString());
        Student.setUniv_roll(st_roll.getText().toString());
        Student.setYear(st_year_value.getText().toString());
        Student.setEmail(getIntent().getStringExtra("Email"));
        Student.setPassword(getIntent().getStringExtra("Password"));
        if(ti_college.getVisibility()==View.VISIBLE)
            Student.setUniv_name(st_college.getText().toString());
        else
            Student.setUniv_name("KIIT");
       // mobileverify();
        new RegProcess().execute();
    }


    public void mobileverify(){

        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(authCallback);

        Digits.authenticate(authConfigBuilder.build());
    }


    public class RegProcess extends AsyncTask<Void, Void, String> {

        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/stu_submit.php?Name="
            +Student.getName()+"&Contact="
            +Student.getContact()+"&Univ_name="
            +Student.getUniv_name()+"&Univ_roll="
            +Student.getUniv_roll()+"&Branch="
            +Student.getBranch()+"&Year="
            +Student.getYear()+"&email="
            +Student.getEmail()+"&password="
            +Student.getPassword()+"&Type="
            +type+"&Source=App";
            jsonUrl = jsonUrl.replaceAll(" ", "%20");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            Digits.logout();
            if(result!=null) {
                if (result.equals("User already exists!\n")) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "User already exists! Duplicate Email/Roll not allowed.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (result.equals("Registration Succesful!\n")) {

                    final SharedPreferences pref = getSharedPreferences("Cur_User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putBoolean("LoggedIn", true);
                    editor.putString("Email", Student.getEmail());
                    editor.putBoolean("Paid", false);
                    editor.putBoolean("Verified", false);
                    editor.apply();

                    progressDialog.dismiss();

                    if(type.equals("Google"))
                        startActivity(new Intent(Student_RegActivity.this, PaymentActivity.class));
                    else{
                      /*  Intent intent = new Intent(Student_RegActivity.this, Email_VerifyActivity.class);
                        intent.putExtra("Email", Student.getEmail());
                        startActivity(intent); */
                        editor.putBoolean("LoggedIn", true);
                        editor.putString("Type", "Google");
                        editor.putString("Email", Student.getEmail());
                        editor.putBoolean("Paid", true);
                        editor.putBoolean("Verified", true);

                        editor.apply();

                        Intent intent = new Intent(Student_RegActivity.this, DashBoardActivity.class);
                        intent.putExtra("Type", "Google");
                        startActivity(intent);
                    }
                    finish();
                } else {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, result, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
            else{
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Unable to reach Servers, Please try again Later.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        logOut();
        super.onDestroy();
    }

    public void logOut() {
        if(type.equals("Google")){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

}

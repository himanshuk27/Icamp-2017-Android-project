package cloud.himanshu.internshipcamp17;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class Splash_2Fragment extends Fragment {

    private Button_Lato_Light Register, Login;
    private ProgressDialog progressDialog;
    private EditText_Lato_Light Email, Password;
    private SharedPreferences sharedPreferences;
    private TextView_Lato_Light forgot;

    public Splash_2Fragment() {}

    public static Splash_2Fragment newInstance() {
        Splash_2Fragment fragment = new Splash_2Fragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash_2, container, false);

        Register = (Button_Lato_Light)rootView.findViewById(R.id.st_reg);
        Login = (Button_Lato_Light)rootView.findViewById(R.id.st_login);
        Email = (EditText_Lato_Light)rootView.findViewById(R.id.st_email_login);
        Password = (EditText_Lato_Light)rootView.findViewById(R.id.st_password_login);
        forgot = (TextView_Lato_Light)rootView.findViewById(R.id.forgotpass);

        progressDialog = new ProgressDialog(getContext());

        sharedPreferences = getContext().getSharedPreferences("Cur_User", MODE_PRIVATE);
        if(!sharedPreferences.getString("Email", "Default").equals("Default"))
            Email.setText(sharedPreferences.getString("Email", "Default"));

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Password.getWindowToken(), 0);  //hiding keyboard
                if(isValidEmail(Email.getText().toString())){
                    if(Password.length()>=5){
                        Intent intent = new Intent(getContext(), Student_RegActivity.class);
                        intent.putExtra("Type", "Email");
                        intent.putExtra("Email", Email.getText().toString());
                        intent.putExtra("Password", Password.getText().toString());
                        startActivity(intent);
                    }
                    else
                       ((SplashActivity)getActivity()).showSnackbar("Minimum Password length should be 5!");
                }
                else
                    ((SplashActivity)getActivity()).showSnackbar("Enter a valid email");


            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Password.getWindowToken(), 0);  //hiding keyboard
                if(isValidEmail(Email.getText().toString())){
                    progressDialog.setMessage("please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new InitLogin().execute();
                }else{
                    ((SplashActivity)getActivity()).showSnackbar("Enter a valid Email!");
                }

                // startActivity(new Intent(getBaseContext(), DashBoardActivity.class));
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(Email.getText().toString()))
                    ((SplashActivity)getActivity()).showForgotDialog(Email.getText().toString());
                else
                    ((SplashActivity)getActivity()).showForgotDialog();
            }
        });

        return rootView;
    }

    public void startMain() {
        Intent intent = new Intent(getContext(), DashBoardActivity.class);
        intent.putExtra("Type", "Normal");
        startActivity(intent);
        getActivity().finish();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public class InitLogin extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/validate_login.php?email="
                    +Email.getText().toString()+"&password="
                    +Password.getText().toString();
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
                    editor.putString("Type", "Normal");
                    editor.putString("Email", Email.getText().toString());
                    editor.putBoolean("Paid", true);
                    editor.putBoolean("Verified", true);
                    editor.apply();

                    startMain();
                }

                else if (result.equals("Email Pending!\n")) {
                    editor.putBoolean("LoggedIn", true);
                    editor.putString("Type", "Normal");
                    editor.putString("Email", Email.getText().toString());
                    editor.putBoolean("Paid", false);
                    editor.putBoolean("Verified", false);
                    editor.apply();

                    Intent intent = new Intent(getContext(), Email_VerifyActivity.class);
                    intent.putExtra("Email", Email.getText().toString());
                    startActivity(intent);
                    getActivity().finish();
                }

                else if (result.equals("Payment Pending!\n")) {
                    editor.putBoolean("LoggedIn", true);
                    editor.putString("Type", "Normal");
                    editor.putString("Email", Email.getText().toString());
                    editor.putBoolean("Paid", false);
                    editor.putBoolean("Verified", true);
                    editor.apply();
                    startActivity(new Intent(getContext(), PaymentActivity.class));
                    getActivity().finish();
                }

                else {
                    ((SplashActivity)getActivity()).showSnackbar("Wrong Email/Password!");
                    ((SplashActivity)getActivity()).shakeAnim();
                }
            }

            else{
                ((SplashActivity)getActivity()).showSnackbar("An error occured, Please try again Later.");
            }
        }
    }
}

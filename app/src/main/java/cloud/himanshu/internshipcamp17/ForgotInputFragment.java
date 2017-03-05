package cloud.himanshu.internshipcamp17;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;


public class ForgotInputFragment extends Fragment {

    private EditText_Lato_Light email;
    private Button_Lato_Light submit;
    private ProgressBar progressBar;
    public ForgotInputFragment() {}

    public static ForgotInputFragment newInstance() {
        ForgotInputFragment fragment = new ForgotInputFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forgot_input, container, false);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar4);
        email = (EditText_Lato_Light)rootView.findViewById(R.id.inputEmail);
        submit = (Button_Lato_Light)rootView.findViewById(R.id.subEmail);

        if(getArguments()!=null)
            email.setText(getArguments().getString("Email"));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(email.getText().toString())==true){
                    new checkEmail().execute();
                }
                else
                    Toast.makeText(getContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public class checkEmail extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            submit.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/resetpass.php?email_ver_status="
                    +email.getText().toString();
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            submit.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if(result!=null) {
               if(result.equals("Yes\n")){
                   ((ForgotPassDialog)getParentFragment()).setEmail(email.getText().toString());
                  ((ForgotPassDialog)getParentFragment()).changeFragment(2);
               }
                else if(result.equals("No\n")){
                   Toast.makeText(getContext(), "This email is not verified!", Toast.LENGTH_SHORT).show();
               }
               else{
                   Toast.makeText(getContext(), "This email is not Registered!", Toast.LENGTH_SHORT).show();
               }

            }
            else{
                Toast.makeText(getContext(), "Error reaching servers...", Toast.LENGTH_SHORT).show();
            }

        }
    }
}

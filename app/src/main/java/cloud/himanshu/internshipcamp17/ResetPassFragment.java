package cloud.himanshu.internshipcamp17;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ResetPassFragment extends Fragment {

    EditText_Lato_Light pass1, pass2;
    Button_Lato_Light submit;
    ProgressBar progressBar;

    public ResetPassFragment() {}

    public static ResetPassFragment newInstance(String email) {
        ResetPassFragment fragment = new ResetPassFragment();
        Bundle args = new Bundle();
        args.putString("Email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reset_pass, container, false);
        pass1 = (EditText_Lato_Light)rootView.findViewById(R.id.pass1);
        pass2 = (EditText_Lato_Light)rootView.findViewById(R.id.pass2);
        submit = (Button_Lato_Light)rootView.findViewById(R.id.pass_submit);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressPass);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkData()==0){
                    if(passMatch()==true){
                        if(checkLength()==true){
                            new ResetPassword().execute();
                        }
                        else
                            Toast.makeText(getContext(), "Minimum length required is 5", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getContext(), "Password didn't match!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(), "Password cant be blank!", Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }

    public boolean passMatch() {
        if(pass1.getText().toString().equals(pass2.getText().toString()))
            return true;
        else
            return false;
    }

    public int checkData() {
        if(pass1.getText().toString().isEmpty() || pass1.getText().toString().equals(" "))
            return 1;
        if(pass2.getText().toString().isEmpty() || pass2.getText().toString().equals(" "))
            return 1;

        return 0;
    }

    public boolean checkLength(){
        if(pass1.getText().toString().length()<5)
            return false;
        else
            return true;
    }

    public class ResetPassword extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            submit.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/resetpass.php?email="
                    +getArguments().getString("Email")+"&resetPass="+pass1.getText().toString();
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
                if(result.equals("Success!\n")){
                    ((ForgotPassDialog)getParentFragment()).dismiss();
                    Toast.makeText(getContext(), "Password Reset!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                }

            }
            else{
                Toast.makeText(getContext(), "Error reaching servers...", Toast.LENGTH_SHORT).show();
            }

        }
    }
}

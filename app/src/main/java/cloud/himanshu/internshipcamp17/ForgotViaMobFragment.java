package cloud.himanshu.internshipcamp17;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class ForgotViaMobFragment extends Fragment {

    private ProgressBar main, sub;
    private TextView_Lato_Light resultlabel, sentlabel;
    private TextView_Lato_Font resend;
    private EditText_Lato_Light pininput;
    LinearLayout ll;
    String SecureCode;

    public ForgotViaMobFragment() {}

    public static ForgotViaMobFragment newInstance(String email) {
        ForgotViaMobFragment fragment = new ForgotViaMobFragment();
        Bundle args = new Bundle();
        args.putString("Email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forgot_via_mob, container, false);
        main = (ProgressBar)rootView.findViewById(R.id.resetMobProg);
        sub = (ProgressBar)rootView.findViewById(R.id.progressBarsms);
        ll = (LinearLayout)rootView.findViewById(R.id.mob_input_layout);
        resultlabel = (TextView_Lato_Light)rootView.findViewById(R.id.dispResult);
        sentlabel = (TextView_Lato_Light)rootView.findViewById(R.id.textView11);
        resend = (TextView_Lato_Font) rootView.findViewById(R.id.resend);
        pininput = (EditText_Lato_Light)rootView.findViewById(R.id.reset_mob);

        new SendSMS().execute();

        pininput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(pininput.getText().toString().equals(SecureCode))
                        ((ForgotPassDialog)getParentFragment()).changeFragment(5);
                    else{
                        resultlabel.setVisibility(View.VISIBLE);
                        resultlabel.setText("Incorrect Pin!");
                    }
                }
                return false;
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResendSMS().execute();
            }
        });


        return rootView;
    }

    public class SendSMS extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/resetpass.php?email="
                    +getArguments().getString("Email")+"&via=SMS";
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if(result.equals("Failed!\n")){
                    Toast.makeText(getContext(), "Unknown Error!", Toast.LENGTH_SHORT).show();
                }
                else{
                    main.setVisibility(View.GONE);
                    ll.setVisibility(View.VISIBLE);

                    result.replaceAll("\n", "");   //removing newline
                    String[] separated = result.split(",");  //splitin by space
                    sentlabel.setText("Reset pin is sent to your mobile "+maskMobile(separated[0]));
                    SecureCode = separated[1];
                    SecureCode = SecureCode.trim(); //remove any whitespaces
                }

            }
            else{
                Toast.makeText(getContext(), "Error reaching servers...", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class ResendSMS extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            resultlabel.setVisibility(View.GONE);
            resend.setVisibility(View.GONE);
            sub.setVisibility(View.VISIBLE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/resetpass.php?email="
                    +getArguments().getString("Email")+"&via=SMS";
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                sub.setVisibility(View.GONE);
                resend.setVisibility(View.VISIBLE);
                resultlabel.setVisibility(View.VISIBLE);
                resultlabel.setText("Reset pin sent");

                result.replaceAll("\n", "");   //removing newline
                String[] separated = result.split(",");  //splitin by space
                SecureCode = separated[1];
                SecureCode = SecureCode.trim(); //remove any whitespaces
            }
            else{
                Toast.makeText(getContext(), "Error reaching servers...", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String maskMobile(String no){
        String mask = "********"+no.substring(no.length()-2);
        return mask;
    }

}

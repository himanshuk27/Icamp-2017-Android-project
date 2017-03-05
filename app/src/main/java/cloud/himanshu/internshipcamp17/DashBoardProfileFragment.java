package cloud.himanshu.internshipcamp17;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class DashBoardProfileFragment extends Fragment {
    private EditText SName,College, Branch, Roll, Year;
    private TextView_Lato_Font Email, Mob;
    private Button resume, Save;
    private int Resume_up = 0;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private ProgressDialog pd;
    private TextView_Lato_Light verify,  changeResume;
    private AuthCallback authCallback;

    private EditText editText;

    public static final String UPLOAD_URL = "https://himanshuk27.000webhostapp.com/app_parse/upload_resume.php";


    //Pdf request code
    private int PICK_PDF_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;


    //Uri to store the pdf uri
    private Uri filePath;

    public DashBoardProfileFragment() {

    }

    public static DashBoardProfileFragment newInstance() {
        DashBoardProfileFragment fragment = new DashBoardProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_dash_board_profile, container, false);
        linearLayout = (LinearLayout)rootView.findViewById(R.id.profLayout);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBarProf);
        SName = (EditText) rootView.findViewById(R.id.dash_name);
        College = (EditText)rootView.findViewById(R.id.dash_college);
        Branch = (EditText)rootView.findViewById(R.id.dash_branch);
        Roll = (EditText)rootView.findViewById(R.id.dash_roll);
        Year = (EditText)rootView.findViewById(R.id.dash_year);
        Email = (TextView_Lato_Font)rootView.findViewById(R.id.dash_email);
        Mob = (TextView_Lato_Font) rootView.findViewById(R.id.dash_mobile);
        resume  = (Button) rootView.findViewById(R.id.dash_resume);
        Save = (Button)rootView.findViewById(R.id.save_but);
        verify = (TextView_Lato_Light)rootView.findViewById(R.id.mobVerify);
        changeResume = (TextView_Lato_Light)rootView.findViewById(R.id.changeResume);



        new FetchProfile().execute();

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verify.getText().toString().equals("Verify")){
                    showSnackBar("Please verify your mob no");
                }
                else{
                    requestStoragePermission();
                    showFileChooser();
                }

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData()==0){
                    if(Mob.getText().length()==10)
                        new UpdateProfile().execute();
                    else
                        showSnackBar("Invalid Mobile No!");
                }

                else
                    showSnackBar("Fields can't be blank!");
            }
        });

        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                new SetMobile(phoneNumber.substring(3)).execute();
                Mob.setText(phoneNumber.substring(3));
                verify.setText("Change");
            }

            @Override
            public void failure(DigitsException error) {
                showSnackBar("Mobile verification failed! please try again.");
            }
        };

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verify.getText().toString().equals("Verify")){
                    AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                            .withAuthCallBack(authCallback)
                            .withPhoneNumber("+91"+Mob.getText().toString());

                    Digits.authenticate(authConfigBuilder.build());
                }
                else{
                    AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                            .withAuthCallBack(authCallback);

                    Digits.authenticate(authConfigBuilder.build());
                }
            }
        });

        pd = new ProgressDialog(getContext());

        return rootView;
    }

    public class FetchProfile extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        private SharedPreferences sharedPreferences;

        @Override
        protected void onPreExecute() {
            sharedPreferences = getContext().getSharedPreferences("Cur_User", Context.MODE_PRIVATE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/misc_queries.php?profile="
                    +sharedPreferences.getString("Email", "Default");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    SName.setText(jsonArray.getJSONObject(0).getString("Name"));
                    College.setText(jsonArray.getJSONObject(0).getString("Univ_name"));
                    if(jsonArray.getJSONObject(0).getString("Univ_name").equals("KIIT"))
                        College.setEnabled(false);
                    Branch.setText(jsonArray.getJSONObject(0).getString("Branch"));
                    Roll.setText(jsonArray.getJSONObject(0).getString("Univ_roll"));
                    Year.setText(jsonArray.getJSONObject(0).getString("Year"));
                    Email.setText(jsonArray.getJSONObject(0).getString("email"));
                    Mob.setText(jsonArray.getJSONObject(0).getString("Contact"));

                    if(jsonArray.getJSONObject(0).getString("resume_uploaded").equals("1")){
                        final String URL = "https://interncamp.ecell.org.in/Icamp17/"
                                +jsonArray.getJSONObject(0).getString("resume_link");
                        Resume_up = 1;
                        resume.setText("Download Resume");
                        resume.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri intentUri = Uri.parse(URL);
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setType("application/pdf");
                                intent.setData(intentUri);
                                startActivity(intent);
                            }
                        });
                        changeResume.setVisibility(View.VISIBLE);
                        changeResume.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestStoragePermission();
                                showFileChooser();
                            }
                        });
                    }

                    if(jsonArray.getJSONObject(0).getString("Mob_verified").equals("0")){
                        verify.setText("Verify");
                    }

                    //  progressBar.setVisibility(View.GONE);
                    //  linearLayout.setVisibility(View.VISIBLE);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
                ((DashBoardActivity)getActivity()).showSnackbar("Error reaching servers...");

        }
    }

    public class UpdateProfile extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        private SharedPreferences sharedPreferences;

        @Override
        protected void onPreExecute() {
            pd.setMessage("please wait...");
            pd.setCancelable(false);
            pd.show();
            sharedPreferences = getContext().getSharedPreferences("Cur_User", Context.MODE_PRIVATE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/stu_submit.php?update=true&Name="
                    +SName.getText()+"&Univ_name="+College.getText()
                    +"&Branch="+Branch.getText()+"&Univ_roll="
                    +Roll.getText()+"&Year="+Year.getText()
                    +"&Contact="+Mob.getText().toString()+"&email="
                    +sharedPreferences.getString("Email", "Default");
            jsonUrl = jsonUrl.replaceAll(" ", "%20");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if(pd.isShowing())
                    pd.dismiss();
                showSnackBar(result);
            }

            else
                ((DashBoardActivity)getActivity()).showSnackbar("Error reaching servers...");

        }
    }

    public class SetMobile extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        private String mobNo;

        SetMobile(String mobNo){
            this.mobNo = mobNo;
        }

        @Override
        protected void onPreExecute() {
            pd.setMessage("please wait...");
            pd.setCancelable(false);
            pd.show();
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/updateMobVerify.php?update="
                    +Email.getText().toString()
                    +"&mob="+mobNo;
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
            if(result!=null){
                if(pd.isShowing())
                    pd.dismiss();
                showSnackBar(result);
            }

            else
                ((DashBoardActivity)getActivity()).showSnackbar("Error reaching servers...");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadResume() {
        //getting name for the image
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Cur_User", Context.MODE_PRIVATE);

        //getting the actual path of the image
        String path = FilePath.getPath(getContext(), filePath);

        if (path == null) {
            showSnackBar("Please move your .pdf file to internal storage and retry");
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                pd.setMessage("uploading...");
                pd.setCancelable(false);
                pd.show();

                //Creating a multi part request
                new MultipartUploadRequest(getContext(), uploadId, UPLOAD_URL)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("user_email", sharedPreferences.getString("Email", "Default")) //Adding text parameter to the request
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(Context context, UploadInfo uploadInfo) {

                            }

                            @Override
                            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                if(pd.isShowing())
                                    pd.dismiss();
                                showSnackBar("Resume Uploaded Succesfully!");
                                new FetchProfile().execute();

                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {

                            }
                        })
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                showSnackBar(exc.getMessage());
            }
        }
    }


    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
    }

    //handling the pdf chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uploadResume();
            }
            else
                showSnackBar("This feature requires android Kitkat or higher");
        }
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                showSnackBar("Permission granted now you can read the storage");
            } else {
                //Displaying another toast if permission is not granted
                showSnackBar("Oops you just denied the permission");
            }
        }
    }


    public int checkData(){

        if(SName.getText().toString().isEmpty() ||SName.getText().toString().equals(" "))
            return 1;

        if(SName.getText().toString().isEmpty() ||SName.getText().toString().equals(" "))
            return 1;
        if(College.getText().toString().isEmpty() ||College.getText().toString().equals(" "))
            return 1;
        if(Branch.getText().toString().isEmpty() ||Branch.getText().toString().equals(" "))
            return 1;
        if(Roll.getText().toString().isEmpty() ||Roll.getText().toString().equals(" "))
            return 1;
        if(Year.getText().toString().isEmpty() ||Year.getText().toString().equals(" "))
            return 1;
        if(Mob.getText().toString().isEmpty() ||Mob.getText().toString().equals(" "))
            return 1;


        return 0;
    }

    public void showSnackBar(String str){
        ((DashBoardActivity)getActivity()).showSnackbar(str);
    }

}

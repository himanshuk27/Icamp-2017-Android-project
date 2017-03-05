package cloud.himanshu.internshipcamp17;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.instamojo.android.Instamojo;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {
    private Button_Lato_Light Login;
    private Button_Lato_Light Instamojo;
    private String PayAmount;

    private static final HashMap<String, String> env_options = new HashMap<>();

    private ProgressDialog dialog;
    private String currentEnv = null;
    private String accessToken = null;
    private CoordinatorLayout coordinatorLayout;
    private TextView_Lato_Light atr, may;

    private SharedPreferences sharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private String SName,College, Branch, Roll, Year, Mob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Login =(Button_Lato_Light)findViewById(R.id.back_login);
        Instamojo = (Button_Lato_Light)findViewById(R.id.pay_insta);
        sharedPreferences = getSharedPreferences("Cur_User", MODE_PRIVATE);
        atr = (TextView_Lato_Light)findViewById(R.id.atrCont);
        may = (TextView_Lato_Light)findViewById(R.id.mayCont);
        if(!sharedPreferences.getBoolean("LoggedIn", false)){
            Toast.makeText(this, "Unauthorized Access", Toast.LENGTH_SHORT).show();
            logOut();
        }
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.paymentSnackbar);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
                startActivity(new Intent(PaymentActivity.this, SplashActivity.class));
                finish();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();


        currentEnv = "https://www.instamojo.com/";

        com.instamojo.android.Instamojo.setBaseUrl(currentEnv);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("please wait...");
        dialog.setCancelable(false);

        Instamojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
                alertDialogBuilder.setMessage("Proceed with Payment?");
                alertDialogBuilder.setTitle("Payment Confirmation");
                        alertDialogBuilder.setPositiveButton("Proceed",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        new FetchProfile().execute();
                                    }
                                });

                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        atr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied no", "+917077102465");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PaymentActivity.this, "Phn no copied", Toast.LENGTH_SHORT).show();
            }
        });
        may.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied no", "+919861658537");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PaymentActivity.this, "Phn no copied", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOut();
    }

    public void makeSnackbar(String string){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, string, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public class FetchProfile extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        private SharedPreferences sharedPreferences;

        @Override
        protected void onPreExecute() {
            dialog.show();
            sharedPreferences = getBaseContext().getSharedPreferences("Cur_User", Context.MODE_PRIVATE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/misc_queries.php?payprofile="
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
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                    try {
                        SName = jsonArray.getJSONObject(0).getString("Name");
                        Mob = jsonArray.getJSONObject(0).getString("Contact");
                        PayAmount = jsonArray.getJSONObject(0).getString("PayAmount");
                        fetchTokenAndTransactionID();
                    } catch (JSONException e) {
                        dialog.dismiss();
                        makeSnackbar("Payment is currently unavailable, we'll be back shortly.");

                    }
                } catch (JSONException e) {
                    makeSnackbar("Payment is currently unavailable, we'll be back shortly.");
                    dialog.dismiss();
                }


            }

            else{
                makeSnackbar("Unable to reach servers...");
            }

        }

    }

    public class UpdatePaymentStatus extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl, amount, Trans_id;
        private SharedPreferences sharedPreferences;

        UpdatePaymentStatus(String amount, String Trans_id){
            this.amount = amount;
            this.Trans_id = Trans_id;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            sharedPreferences = getBaseContext().getSharedPreferences("Cur_User", Context.MODE_PRIVATE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/misc_queries.php?setPayment="
                    +amount+"&email="
                    +sharedPreferences.getString("Email", "Default")
                    +"&trans_id="+Trans_id;
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if(result.equals("Payment Successful\n")){
                    makeSnackbar("Payment Succesful");
                    SharedPreferences pref = getSharedPreferences("Cur_User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("LoggedIn", true);
                    editor.putString("Email", sharedPreferences.getString("Email", "Default"));
                    editor.putBoolean("Paid", true);
                    editor.putBoolean("Verified", true);
                    editor.apply();
                    startActivity(new Intent(PaymentActivity.this, DashBoardActivity.class));
                    finish();
                }
                else
                    makeSnackbar(result);
            }
            else{
                makeSnackbar("Unable to reach servers...");
            }
            }

    }

    private void fetchOrder(String accessToken, String orderID){
        // Good time to show dialog
        Request request = new Request(accessToken, orderID, new OrderRequestCallBack() {
            @Override
            public void onFinish(final Order order, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                makeSnackbar("No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                makeSnackbar("Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                makeSnackbar("Access token is invalid or expired. Please Update the token!!");
                            } else {
                                makeSnackbar(error.toString());
                            }
                            return;
                        }

                        startPreCreatedUI(order);
                    }
                });

            }
        });

        request.execute();
    }

    private void createOrder(String accessToken, String transactionID) {
        String name = SName;
        final String email = sharedPreferences.getString("Email", "default");
        String phone = Mob;
        String amount = PayAmount;
        String description = "Icamp Payment (App)";

        //Create the Order
        Order order = new Order(accessToken, transactionID, name, email, phone, amount, description);

        //set webhook
        order.setWebhook("https://himanshuk27.000webhostapp.com/Icamp17/payment/webhook.php");

        //Validate the Order
        if (!order.isValid()) {
            //oops order validation failed. Pinpoint the issue(s).

            if (!order.isValidName()) {
                makeSnackbar("Buyer name is invalid");
            }

            if (!order.isValidEmail()) {
                makeSnackbar("Buyer email is invalid");
            }

            if (!order.isValidPhone()) {
                makeSnackbar("Buyer phone is invalid");
            }

            if (!order.isValidAmount()) {
                makeSnackbar("Amount is invalid or has more than two decimal places");
            }

            if (!order.isValidDescription()) {
                makeSnackbar("Description is invalid");
            }

            if (!order.isValidTransactionID()) {
                makeSnackbar("Transaction is Invalid");
            }

            if (!order.isValidRedirectURL()) {
                makeSnackbar("Redirection URL is invalid");
            }

            if (!order.isValidWebhook()) {
                makeSnackbar("Webhook URL is invalid");
            }

            return;
        }

        //Validation is successful. Proceed
        dialog.show();
        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(final Order order, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                makeSnackbar("No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                makeSnackbar("Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                makeSnackbar("Access token is invalid or expired. Please Update the token!!");
                            } else if (error instanceof Errors.ValidationError) {
                                // Cast object to validation to pinpoint the issue
                                Errors.ValidationError validationError = (Errors.ValidationError) error;

                                if (!validationError.isValidTransactionID()) {
                                    makeSnackbar("Transaction ID is not Unique");
                                    return;
                                }

                                if (!validationError.isValidRedirectURL()) {
                                    makeSnackbar("Redirect url is invalid");
                                    return;
                                }

                                if (!validationError.isValidWebhook()) {
                                    makeSnackbar("Webhook url is invalid");
                                    return;
                                }

                                if (!validationError.isValidPhone()) {
                                    makeSnackbar("Buyer's Phone Number is invalid/empty");
                                    return;
                                }

                                if (!validationError.isValidEmail()) {
                                    makeSnackbar("Buyer's Email is invalid/empty");
                                    return;
                                }

                                if (!validationError.isValidAmount()) {
                                    makeSnackbar("Amount is either less than Rs.9 or has more than two decimal places");
                                    return;
                                }

                                if (!validationError.isValidName()) {
                                    makeSnackbar("Buyer's Name is required");
                                    return;
                                }
                            } else {
                                makeSnackbar(error.getMessage());
                            }
                            return;
                        }

                        startPreCreatedUI(order);
                    }
                });
            }
        });

        request.execute();
    }

    private void startPreCreatedUI(Order order) {
        //Using Pre created UI
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    /**
     *
     * Fetch Access token and unique transactionID from developers server
     */
    private void fetchTokenAndTransactionID() {
        if (!dialog.isShowing()) {
            dialog.show();
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("env", currentEnv.toLowerCase())
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://himanshuk27.000webhostapp.com/app_parse/paymentgateway.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        makeSnackbar("Failed to fetch the Order Tokens");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString;
                String errorMessage = null;
                String transactionID = null;
                responseString = response.body().string();
                response.body().close();
                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    if (responseObject.has("error")) {
                        errorMessage = responseObject.getString("error");
                    } else {
                        accessToken = responseObject.getString("access_token");
                        transactionID = responseObject.getString("transaction_id");
                    }
                } catch (JSONException e) {
                    errorMessage = "Failed to fetch Order tokens";
                }

                final String finalErrorMessage = errorMessage;
                final String finalTransactionID = transactionID;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (finalErrorMessage != null) {
                            makeSnackbar(finalErrorMessage);
                            return;
                        }

                        createOrder(accessToken, finalTransactionID);
                    }
                });

            }
        });

    }

    /**
     * Will check for the transaction status of a particular Transaction
     *
     * @param transactionID Unique identifier of a transaction ID
     */
    private void checkPaymentStatus(final String transactionID, final String orderID) {
        if (accessToken == null || (transactionID == null && orderID == null)) {
            return;
        }

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        makeSnackbar("checking transaction status");
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder builder = getHttpURLBuilder();
        builder.addPathSegment("status");
        if (transactionID != null){
            builder.addQueryParameter("transaction_id", transactionID);
        } else {
            builder.addQueryParameter("id", orderID);
        }
        builder.addQueryParameter("env", currentEnv.toLowerCase());
        HttpUrl url = builder.build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        makeSnackbar("Failed to fetch the Transaction status");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                response.body().close();
                String status = null;
                String paymentID = null;
                String amount = null;
                String errorMessage = null;

                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    JSONObject payment = responseObject.getJSONArray("payments").getJSONObject(0);
                    status = payment.getString("status");
                    paymentID = payment.getString("id");
                    amount = responseObject.getString("amount");

                } catch (JSONException e) {
                    errorMessage = "Failed to fetch the Transaction status";
                }

                final String finalStatus = status;
                final String finalErrorMessage = errorMessage;
                final String finalPaymentID = paymentID;
                final String finalAmount = amount;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (finalStatus == null) {
                            makeSnackbar(finalErrorMessage);
                            return;
                        }

                        if (!finalStatus.equalsIgnoreCase("successful")) {
                            makeSnackbar("Transaction still pending");
                            return;
                        }

                        new UpdatePaymentStatus(finalAmount, transactionID).execute();
                    }
                });
            }
        });

    }

    /*

    private void refundTheAmount(String transactionID, String amount) {
        if (accessToken == null || transactionID == null || amount == null) {
            return;
        }

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        makeSnackbar("Initiating a refund for - " + amount);
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("env", currentEnv.toLowerCase())
                .add("transaction_id", transactionID)
                .add("amount", amount)
                .add("type", "PTH")
                .add("body", "Refund the Amount")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://interncamp.ecell.org.in/app_parse/paymentgateway.php")
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        makeSnackbar("Failed to Initiate a refund");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        String message;

                        if (response.isSuccessful()) {
                            message = "Refund intiated successfully";
                        } else {
                            message = "Failed to Initiate a refund";
                        }

                        makeSnackbar(message);
                    }
                });
            }
        });
    }

    */

    private HttpUrl.Builder getHttpURLBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("sample-sdk-server.instamojo.com");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (transactionID != null || paymentID != null) {
                checkPaymentStatus(transactionID, orderID);
            } else {
                makeSnackbar("Oops!! Payment was cancelled");
            }
        }
    }

    public void logOut() {SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LoggedIn", false);
        editor.commit();
        if(sharedPreferences.getString("Type", "Default").equals("Google")){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }


}

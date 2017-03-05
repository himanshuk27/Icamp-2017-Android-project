package cloud.himanshu.internshipcamp17;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private SharedPreferences pref1;
    private SharedPreferences.Editor editor;
    private ImageView logo;
    private Animation anim;
    private CoordinatorLayout coordinatorLayout;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private ForgotPassDialog dialog;
    private String Version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = (ImageView)findViewById(R.id.logo);
        pref1 = getSharedPreferences("Cur_User", MODE_PRIVATE);
        editor = pref1.edit();
        anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.loginSnackbarLayout);

        logo.startAnimation(anim);

        Boolean LoggedIn = pref1.getBoolean("LoggedIn", false);
        Boolean Verified = pref1.getBoolean("Verified", false);
        Boolean Paid = pref1.getBoolean("Paid", false);

       if(LoggedIn) {    //if user is logged in


            if(!Verified) {
                new EmailCheck().execute();
            }
            else if(!Paid)
                new PaymentCheck().execute();

           else{
               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                      startMain();
                   }
               }, SPLASH_DISPLAY_LENGTH);
           }

        }

        else {                                                   //if no user is logged in
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    PackageInfo pInfo = null;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        Version = pInfo.versionName;
                        new AppUpdateCheck().execute();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                anim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.translate);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fm = getSupportFragmentManager();
                            ft = fm.beginTransaction();
                            ft.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out_left);
                            ft.add(R.id.splash_container, Splash_1Fragment.newInstance(), "Frag1");
                            ft.addToBackStack(null);
                            ft.commit();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                logo.startAnimation(anim);
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

    }

   @Override
    public void onBackPressed() {
       Fragment f = getSupportFragmentManager().findFragmentByTag("Frag1");
       Fragment f2 = getSupportFragmentManager().findFragmentByTag("Frag2");

       if(f.isVisible())
           finish();
       else if(f2.isVisible())
           changeFragment(1);
    }



    public void changeFragment(int chc){
        if(chc==1){
            ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.splash_container, Splash_1Fragment.newInstance(), "Frag1");
            ft.addToBackStack(null);
            ft.commit();
        }
        else if(chc==2){
            ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.splash_container, Splash_2Fragment.newInstance(), "Frag2");
            ft.addToBackStack(null);
            ft.commit();
        }

    }

    public void showSnackbar(String str){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, str, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public class EmailCheck extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;

        @Override
        protected void onPreExecute() {
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/misc_queries.php?email_ver_status="
                    + pref1.getString("Email", "Default");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if (result.equals("1\n")) {
                    editor.putBoolean("Verified", true);
                    editor.commit();
                    new PaymentCheck().execute();

                } else if (result.equals("0\n")) {
                    startMain();
                }
            }
            else{
                Toast.makeText(SplashActivity.this, "Unable to reach servers...", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class PaymentCheck extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;

        @Override
        protected void onPreExecute() {
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/misc_queries.php?payment_status="
                    + pref1.getString("Email", "Default");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if (result.equals("400\n")) {
                    editor.putBoolean("Paid", true);
                    editor.commit();
                    startMain();

                } else {
                    editor.putBoolean("Paid", false);
                    editor.commit();
                    startMain();
                }
            }
            else{
                Toast.makeText(SplashActivity.this, "Unable to reach servers...", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class AppUpdateCheck extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;

        @Override
        protected void onPreExecute() {
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/checkappupdate.php?check="
                    + Version;
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if (result.equals("No\n")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
                    alertDialogBuilder.setTitle("Icamp 2017");
                    alertDialogBuilder.setMessage("A new version is available");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setNeutralButton("Update",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("market://details?id=com.kodexlabs.internshipcamp2017"));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    alertDialogBuilder.show();
                }
            }
            else{
                Toast.makeText(SplashActivity.this, "Unable to reach servers...", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void startMain() {
        Intent intent = new Intent(SplashActivity.this, DashBoardActivity.class);
        intent.putExtra("Type", pref1.getString("Type", "Default"));
        startActivity(intent);
        finish();
    }

    public void showForgotDialog(String email) {
        dialog = new ForgotPassDialog();
        Bundle args = new Bundle();
        args.putString("Email", email);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Forgot_Password");
    }

    public void showForgotDialog() {
        dialog = new ForgotPassDialog();
        dialog.show(getSupportFragmentManager(), "Forgot_Password");
    }

    public void shakeAnim() {
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.splash_container);
        Animation shake = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.shake);
        frameLayout.startAnimation(shake);
    }



}

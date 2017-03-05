package cloud.himanshu.internshipcamp17;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.lang.reflect.Field;

//TODO: revamp reg process,  firebase, webview,
//TODO: profile change mobile no
//TODO: share, procedure, t&c, faq's, contact us, about us

public class DashBoardActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;
    private BottomNavigationView bottomNavigation;
    private SharedPreferences pref;
    private CoordinatorLayout coordinatorLayout;
    private GoogleApiClient mGoogleApiClient;
    private String Version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Version = pInfo.versionName;
            new AppUpdateCheck().execute();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(getIntent().getStringExtra("Type").equals("Google")){
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mGoogleApiClient.connect();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        pref = getSharedPreferences("Cur_User", MODE_PRIVATE);
        setSupportActionBar(toolbar);
        setTitle("Internship Camp 2017");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.dashboardSnackbarLayout);
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);
        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigation.inflateMenu(R.menu.bottom_nav);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.db_home:
                        fragmentChange(0);
                        break;
                    case R.id.db_startups:
                        fragmentChange(1);
                        break;
                    case R.id.db_profile:
                        fragmentChange(2);
                        break;
                    case R.id.db_messages:
                        fragmentChange(3);
                        break;
                }
                return true;
            }
        });disableShiftMode(bottomNavigation);

        if(!pref.getBoolean("LoggedIn", false) || !pref.getBoolean("Verified", false)
                || !pref.getBoolean("Paid", false)){
            Toast.makeText(this, "Unauthorized Access Main", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("LoggedIn", false);
            editor.apply();
            startActivity(new Intent(getBaseContext(), SplashActivity.class));
            finish();
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
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DashBoardActivity.this);
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
                //Toast.makeText(DashBoardActivity.this, "Unable to reach servers...", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logOut();
        }
        if (id == R.id.action_contactus) {
            startActivity(new Intent(this, ContactUsActivity.class));
        }
       /* if (id == R.id.action_procedure) {
            startActivity(new Intent(DashBoardActivity.this, ProcedureActivity.class));
        }
        if (id == R.id.action_share) {
            return true;
        }
        if (id == R.id.action_tnc) {
            startActivity(new Intent(DashBoardActivity.this, TnCActivity.class));
        }
        if (id == R.id.action_faq) {
            startActivity(new Intent(DashBoardActivity.this, FaqActivity.class));
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(DashBoardActivity.this, AboutUsActivity.class));
        } */

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("LoggedIn", false);
        editor.apply();
        if(getIntent().getStringExtra("Type").equals("Google")){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            startActivity(new Intent(getBaseContext(), SplashActivity.class));
                            finish();
                        }
                    });
        }
        else{
            startActivity(new Intent(getBaseContext(), SplashActivity.class));
            finish();
        }
    }

    public void fragmentChange(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return DashBoardStartupsFragment.newInstance();
                case 1:
                    return DashBoardHomeFragment.newInstance();
                case 2:
                    return DashBoardProfileFragment.newInstance();
                case 3:
                    return DashBoardMessagesFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    public void showSnackbar(String str){
        str = str.replaceAll("\n", "");  //removing new line
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, str, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}

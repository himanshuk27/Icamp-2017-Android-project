package cloud.himanshu.internshipcamp17;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StartupDetailActivity extends AppCompatActivity {

    private TextView Name, Website, Stipend, Desc, Location, Skills, duration;
    private FloatingActionButton Apply, Remove;
    private SharedPreferences sharedPreferences;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progress;
    private LinearLayout ll;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_detail);

        progress = (ProgressBar)findViewById(R.id.startup_progress);
        ll = (LinearLayout)findViewById(R.id.startup_layout);

        Name = (TextView) findViewById(R.id.start_name);
        Website = (TextView)findViewById(R.id.start_website);
        Stipend =(TextView)findViewById(R.id.start_stipend);
        Desc = (TextView)findViewById(R.id.start_desc);
        Location = (TextView)findViewById(R.id.start_location);
        Skills = (TextView)findViewById(R.id.start_skills);
        duration = (TextView)findViewById(R.id.start_duration);
        Apply = (FloatingActionButton)findViewById(R.id.apply_but);
        Remove = (FloatingActionButton)findViewById(R.id.apply_but);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.detail_content);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);

        new FetchStartupProf().execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class RemoveStartup extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            sharedPreferences = getSharedPreferences("Cur_User", MODE_PRIVATE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/applyforstartup.php?remove=true&email="
                    +sharedPreferences.getString("Email", "Default")+"&start_name="
                    +getIntent().getStringExtra("Title");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            if(result!=null){
                result = result.replaceAll("\n", "");
                Toast.makeText(StartupDetailActivity.this, result, Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Unable to reach servers...", Snackbar.LENGTH_LONG);
                snackbar.show();
            }


        }
    }

    public class ApplyForStartup extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            sharedPreferences = getSharedPreferences("Cur_User", MODE_PRIVATE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/applyforstartup.php?user_email="
                    +sharedPreferences.getString("Email", "Default")+"&startup="
                    +getIntent().getStringExtra("Title");
            jsonUrl = jsonUrl.replaceAll(" ", "%20");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            if(result!=null){
                result = result.replaceAll("\n", "");
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, result, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            else{
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Unable to reach servers...", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }

    public class FetchStartupProf extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;
        @Override
        protected void onPreExecute() {
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/misc_queries.php?startup_profile="
                    +getIntent().getStringExtra("Title");
            jsonUrl = jsonUrl.replaceAll(" ", "%20").replaceAll("&", "%26");
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
            return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            progress.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
            if(result!=null){
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Name.setText(jsonArray.getJSONObject(0).getString("Name"));
                    Website.setText(jsonArray.getJSONObject(0).getString("website"));
                    if(jsonArray.getJSONObject(0).getString("Stipend").equals("No"))
                        Stipend.setText("No Stipend");
                    else
                        Stipend.setText("Stipend: "+jsonArray.getJSONObject(0).getString("Stipend_amt"));
                    Location.setText(jsonArray.getJSONObject(0).getString("Location"));
                    Desc.setText(jsonArray.getJSONObject(0).getString("Description"));
                    Skills.setText(jsonArray.getJSONObject(0).getString("Skills"));
                    duration.setText(jsonArray.getJSONObject(0).getString("Duration")+" months");

                    if(getIntent().getStringExtra("refer").equals("Home")){
                        Remove.setImageResource(R.drawable.delete);
                        Remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new RemoveStartup().execute();
                            }
                        });
                    }
                    else{
                        Apply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ApplyForStartup().execute();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

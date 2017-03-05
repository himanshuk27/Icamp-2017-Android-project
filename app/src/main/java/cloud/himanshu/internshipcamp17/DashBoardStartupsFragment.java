package cloud.himanshu.internshipcamp17;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DashBoardStartupsFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerAdapter myAdapter;
    private ArrayList<JSONObject> startList = new ArrayList<>();
    private ProgressBar progressBar;
    public DashBoardStartupsFragment() {

    }

    public static DashBoardStartupsFragment newInstance() {
        DashBoardStartupsFragment fragment = new DashBoardStartupsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dash_board_startups, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycerView);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar3);
        linearLayoutManager = new LinearLayoutManager(getContext());
        myAdapter = new RecyclerAdapter(startList, getContext(), "Startup");
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        new FetchStartups().execute();
        return rootView;
    }

    public class FetchStartups extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;

        @Override
        protected void onPreExecute() {
            if(progressBar.getVisibility()!=View.GONE)
                progressBar.setVisibility(View.VISIBLE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/fetch_startups.php?fetch=true";
            jsonApi = new JsonApi(jsonUrl);
        }

        @Override
        protected String doInBackground(Void... params) {
           return jsonApi.execute();
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if(result!=null){
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        startList.add(jsonObject);
                        myAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            else {
                ((DashBoardActivity)getActivity()).showSnackbar("Error reaching servers...");
            }



        }
    }




}

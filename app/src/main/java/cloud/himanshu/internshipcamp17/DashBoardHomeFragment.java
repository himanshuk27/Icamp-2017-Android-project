package cloud.himanshu.internshipcamp17;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

public class DashBoardHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerAdapter myAdapter;
    private ArrayList<JSONObject> startList = new ArrayList<>();
    private ProgressBar progressBar;
    private ImageView textView;

    public DashBoardHomeFragment() {}

    public static DashBoardHomeFragment newInstance() {
        DashBoardHomeFragment fragment = new DashBoardHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //new FetchAppliedStartups().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        new FetchAppliedStartups().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_board_home, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycerView2);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar2);
        textView =  (ImageView)rootView.findViewById(R.id.no_startup);
        linearLayoutManager = new LinearLayoutManager(getContext());
        myAdapter = new RecyclerAdapter(startList, getContext(), "Home");
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        return rootView;
    }

    public class FetchAppliedStartups extends AsyncTask<Void, Void, String> {
        private JsonApi jsonApi;
        private String jsonUrl;

        @Override
        protected void onPreExecute() {
            if(progressBar.getVisibility()!=View.GONE)
                progressBar.setVisibility(View.VISIBLE);

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Cur_User", Context.MODE_PRIVATE);
            jsonUrl = "https://himanshuk27.000webhostapp.com/app_parse/misc_queries.php?applied="
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
                progressBar.setVisibility(View.GONE);
                startList.clear();
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if(result.equals("[]\n"))
                        textView.setVisibility(View.VISIBLE);
                    else{
                        textView.setVisibility(View.GONE);
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            startList.add(jsonObject);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                myAdapter.notifyDataSetChanged();
            }

            else{
                ((DashBoardActivity)getActivity()).showSnackbar("Error reaching servers...");
            }


        }
    }

}

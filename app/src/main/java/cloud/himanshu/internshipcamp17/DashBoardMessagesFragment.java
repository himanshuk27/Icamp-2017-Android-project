package cloud.himanshu.internshipcamp17;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static cloud.himanshu.internshipcamp17.R.id.parent;


public class DashBoardMessagesFragment extends Fragment {
    private String[] msgArray;
    private SharedPreferences sharedPreferences;
    private TextView Blank;
    public DashBoardMessagesFragment() {

    }
    public static DashBoardMessagesFragment newInstance() {
        DashBoardMessagesFragment fragment = new DashBoardMessagesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_dash_board_messages, container, false);
        sharedPreferences = getContext().getSharedPreferences("Cur_User", MODE_PRIVATE);
        Blank = (TextView)rootView.findViewById(R.id.blank);
        ListView listView = (ListView)rootView.findViewById(R.id.msgList);

        if(sharedPreferences.getInt("MsgCount", 0)!=0){
            Blank.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            int count = sharedPreferences.getInt("MsgCount", 0);
            msgArray = new String[count];
            for(int i=1; i<=count; i++)
                msgArray[i-1] = sharedPreferences.getString("TitleMsgNo"+i, "default");

            List<String> list = Arrays.asList(msgArray);
            Collections.reverse(list);
            msgArray = (String[]) list.toArray();

            ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                    R.layout.msgrow, msgArray);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    String title= (String)parent.getAdapter().getItem(position);
                    position++;
                    String msg = sharedPreferences.getString("MsgMsgNo"+position, "Default");
                    Intent intent = new Intent(getContext(), NotificationViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", title);
                    bundle.putString("msg", msg);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }


        return rootView;
    }

}

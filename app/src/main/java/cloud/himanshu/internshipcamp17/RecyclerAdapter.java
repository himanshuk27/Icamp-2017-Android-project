package cloud.himanshu.internshipcamp17;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Himanshu on 05-Mar-17.
 */

public class RecyclerAdapter extends RecyclerView
        .Adapter<RecyclerAdapter
        .DataObjectHolder> {
    private ArrayList<JSONObject> mDataset;
    private Context context;
    private String Mode;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView_Lato_Light label, skill0, skill1, skill2, skill3, conf;
        TextView_Lato_Light duration, location, stipend;
        View colorBar, Dot;

        public DataObjectHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            label = (TextView_Lato_Light) itemView.findViewById(R.id.title);
            colorBar = (View) itemView.findViewById(R.id.colorBar);
            skill0 = (TextView_Lato_Light)itemView.findViewById(R.id.skill0);
            skill1 = (TextView_Lato_Light)itemView.findViewById(R.id.skill1);
            skill2 = (TextView_Lato_Light)itemView.findViewById(R.id.skill2);
            skill3 = (TextView_Lato_Light)itemView.findViewById(R.id.skill3);
            duration = (TextView_Lato_Light)itemView.findViewById(R.id.duration);
            location = (TextView_Lato_Light)itemView.findViewById(R.id.location);
            stipend = (TextView_Lato_Light)itemView.findViewById(R.id.stipend);
            conf = (TextView_Lato_Light)itemView.findViewById(R.id.conf);
            Dot = (View) itemView.findViewById(R.id.dot);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), StartupDetailActivity.class);
            if(Dot.getVisibility()==View.VISIBLE)
                intent.putExtra("refer", "Home");
            else
                intent.putExtra("refer", "Startup");
            intent.putExtra("Title", label.getText().toString());
            v.getContext().startActivity(intent);
        }
    }

    public RecyclerAdapter(ArrayList<JSONObject> myDataset, Context context, String Mode) {
        mDataset = myDataset;
        this.context = context;
        this.Mode = Mode;
    }

    private int getMatColor()
    {
        int returnColor = Color.BLACK;
        int arrayId = context.getResources().getIdentifier("mdcolor", "array", context.getPackageName());

        if (arrayId != 0)
        {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.startup_row_layout, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        try {
            holder.label.setText(mDataset.get(position).getString("Name"));
            holder.location.setText(mDataset.get(position).getString("Location"));
            if(mDataset.get(position).getString("Stipend").equals("Yes")) {
                if (mDataset.get(position).getString("Stipend_amt").equals(""))
                    holder.stipend.setText("Stipend: Yes");
                else
                    holder.stipend.setText("Stipend: " + mDataset.get(position).getString("Stipend_amt"));
            }

            else
                holder.stipend.setText("No Stipend");
            if(mDataset.get(position).getString("Duration").equals(""))
                holder.duration.setVisibility(View.GONE);
            else
                holder.duration.setText(mDataset.get(position).getString("Duration")+" Months");


            holder.colorBar.setBackgroundColor(getMatColor());

            String[] skills = mDataset.get(position).getString("Skills").split("\\s*,\\s*");
            try {
                holder.skill0.setText(skills[0]); holder.skill0.setVisibility(View.VISIBLE);
                holder.skill1.setText(skills[1]); holder.skill1.setVisibility(View.VISIBLE);
                holder.skill2.setText(skills[2]); holder.skill2.setVisibility(View.VISIBLE);
                holder.skill3.setText(skills[3]); holder.skill3.setVisibility(View.VISIBLE);
            }
            catch (ArrayIndexOutOfBoundsException e){

            }

            if(Mode.equals("Home")){
                holder.Dot.setVisibility(View.VISIBLE);
                holder.conf.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

package cloud.himanshu.internshipcamp17;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotificationViewActivity extends AppCompatActivity {

    private TextView_Lato_Font title;
    private TextView_Lato_Light msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        title = (TextView_Lato_Font)findViewById(R.id.title);
        msg = (TextView_Lato_Light)findViewById(R.id.msg);


        title.setText(getIntent().getExtras().getString("title"));
        msg.setText(getIntent().getExtras().getString("msg"));
    }
}

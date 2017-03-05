package cloud.himanshu.internshipcamp17;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Himanshu on 10-Mar-17.
 */

public class MyInstanceIdSer extends FirebaseInstanceIdService {
    private static final String REC_TOKEN = "REC_TOKEN";
    @Override
    public void onTokenRefresh() {
        String rec_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REC_TOKEN, rec_token);
    }
}

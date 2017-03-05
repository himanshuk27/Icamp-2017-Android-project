package cloud.himanshu.internshipcamp17;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.google.firebase.FirebaseApp;
import com.instamojo.android.Instamojo;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Himanshu on 05-Mar-17.
 */

public class MyApp extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "BuP3YcWf1y37VBoILkiV0onpT";
    private static final String TWITTER_SECRET = "Kzpf2JccqTogxUkvoEy71oDRax6T2pqrsWxvC60WxBXpYmTM3S";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        Instamojo.initialize(this);
        FirebaseApp.initializeApp(this);
    }
}

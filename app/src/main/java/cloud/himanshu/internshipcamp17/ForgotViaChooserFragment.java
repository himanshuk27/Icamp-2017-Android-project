package cloud.himanshu.internshipcamp17;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;


public class ForgotViaChooserFragment extends Fragment {

    private RadioGroup radioGroup;
    private RadioButton email, mob;
    private Button next;

    public ForgotViaChooserFragment() {}

    public static ForgotViaChooserFragment newInstance(String email) {
        ForgotViaChooserFragment fragment = new ForgotViaChooserFragment();
        Bundle args = new Bundle();
        args.putString("Email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forgot_via_chooser, container, false);

        radioGroup = (RadioGroup)rootView.findViewById(R.id.forgotChooser);
        email = (RadioButton)rootView.findViewById(R.id.radio_email);
        mob = (RadioButton)rootView.findViewById(R.id.radio_mob);
        next = (Button)rootView.findViewById(R.id.but_next);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Light.ttf");
        email.setTypeface(tf);
        mob.setTypeface(tf);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.radio_email)
                  ((ForgotPassDialog)getParentFragment()).changeFragment(3);
                else if(radioGroup.getCheckedRadioButtonId()==R.id.radio_mob)
                    ((ForgotPassDialog)getParentFragment()).changeFragment(4);
            }
        });

        return rootView;
    }

}

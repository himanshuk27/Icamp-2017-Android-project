package cloud.himanshu.internshipcamp17;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class ForgotPassDialog extends DialogFragment {
    private FragmentManager fm;
    private FragmentTransaction ft;
    private String Email;

    public ForgotPassDialog(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.forgot_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        fm = getChildFragmentManager();
        ft = fm.beginTransaction();
        ForgotInputFragment forgotInputFragment = ForgotInputFragment.newInstance();
        if(getArguments()!=null)
            forgotInputFragment.setArguments(getArguments());
        ft.add(R.id.forgot_container, forgotInputFragment, "ForgotInput");
        ft.addToBackStack(null);
        ft.commit();

        return  rootView;
    }

    public void setEmail(String Email){
        this.Email = Email;
    }

    public void changeFragment(int frag){
        fm = getChildFragmentManager();
        ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        if(frag==1) {
            ft.replace(R.id.forgot_container, ForgotInputFragment.newInstance(), "ForgotInput");
            ft.addToBackStack(null);
            ft.commit();
        }

        else if(frag==2){
            ft.replace(R.id.forgot_container, ForgotViaChooserFragment.newInstance(Email), "ForgotChooser");
            ft.addToBackStack(null);
            ft.commit();
        }
        else if(frag==3){
            setCancelable(false);
            ft.replace(R.id.forgot_container, ForgotViaEmailFragment.newInstance(Email), "ForgotEmail");
            ft.addToBackStack(null);
            ft.commit();
        }
        else if(frag==4){
            setCancelable(false);
            ft.replace(R.id.forgot_container, ForgotViaMobFragment.newInstance(Email), "ForgotMob");
            ft.addToBackStack(null);
            ft.commit();
        }
        else if(frag==5){
            setCancelable(false);
            ft.replace(R.id.forgot_container, ResetPassFragment.newInstance(Email), "ForgotMob");
            ft.addToBackStack(null);
            ft.commit();
        }
    }

}

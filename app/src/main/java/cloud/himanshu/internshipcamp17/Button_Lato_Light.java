package cloud.himanshu.internshipcamp17;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Himanshu on 11-Mar-17.
 */

public class Button_Lato_Light extends Button {
    public Button_Lato_Light(Context context) {
        super(context);
        init();
    }

    public Button_Lato_Light(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Button_Lato_Light(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Light.ttf");
            setTypeface(tf);
        }
    }
}

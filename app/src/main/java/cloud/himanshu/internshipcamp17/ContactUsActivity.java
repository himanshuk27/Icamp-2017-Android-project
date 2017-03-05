package cloud.himanshu.internshipcamp17;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ContactUsActivity extends AppCompatActivity {

    private TextView_Lato_Font atr, may;
    private TextView_Lato_Light website, email;
    private ImageView fbButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        atr = (TextView_Lato_Font)findViewById(R.id.cont1);
        may = (TextView_Lato_Font)findViewById(R.id.cont2);
        email = (TextView_Lato_Light)findViewById(R.id.email);
        website = (TextView_Lato_Light)findViewById(R.id.website);
        fbButton = (ImageView)findViewById(R.id.fbPage);

        atr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied no", "+917077102465");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ContactUsActivity.this, "Phn no copied", Toast.LENGTH_SHORT).show();
            }
        });

        may.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied no", "+919861658537");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ContactUsActivity.this, "Phn no copied", Toast.LENGTH_SHORT).show();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","ecell.kiit@kiit.ac.in", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Icamp 2017");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://interncamp.ecell.org.in";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/344507538960505"));
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/kiitecell")));
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

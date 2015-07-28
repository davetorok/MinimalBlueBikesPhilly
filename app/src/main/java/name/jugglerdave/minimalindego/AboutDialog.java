package name.jugglerdave.minimalindego;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by dtorok on 7/25/2015.
 */
public class AboutDialog extends Dialog {

    public AboutDialog(Context context)
    {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_dialog_layout);
        //todo set version number from package
        TextView tv = (TextView)findViewById(R.id.versionnum);
        tv.setText(R.string.string_versionText);


        Button dismiss_button = (Button) findViewById(R.id.okButton);
        dismiss_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

package com.dtu.mark.webview_background;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    public static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
    }

    public static void setTextViewText(String str) {
        textView.setText(str);
    }

    public void onClickStartService(View view) {
        startService(new Intent(this, WebViewHandler.class));
    }
}

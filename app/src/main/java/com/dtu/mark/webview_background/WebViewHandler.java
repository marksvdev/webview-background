package com.dtu.mark.webview_background;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyAsyncHandler;
import com.jockeyjs.JockeyCallback;
import com.jockeyjs.JockeyHandler;
import com.jockeyjs.JockeyImpl;
import com.jockeyjs.JockeyService;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mark on 18/09/14.
 */
public class WebViewHandler extends Service {

    String counter = "0";

    WebView wv;

    private Jockey jockey;

    private static final int UPDATE_INTERVAL = 1000;

    private static final String TAG = "WebWebHandler";

    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Reference: http://stackoverflow.com/questions/18865035/android-using-webview-outside-an-activity-context
         *
         * Initialize webview
         */

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;

        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        wv = new WebView(this);
        wv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        view.addView(wv);


        windowManager.addView(view, params);

        /**
         * Initialize JockeyJS
         */

        jockey = JockeyImpl.getDefault();

        jockey.configure(wv);

        jockey.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("webViewClient", "page finished loading!");
            }
        });


        setJockeyEvents();

        wv.loadUrl("file:///android_asset/index.html");

        /**
         * Initialize handler calls
         */

        mHandler.removeCallbacks(readAndSet);
        mHandler.post(readAndSet);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        return 0;
    }

    private Runnable readAndSet = new Runnable() {
        @Override
        public void run() {
            getCounter();
            MainActivity.setTextViewText(counter);

            mHandler.removeCallbacks(readAndSet);
            mHandler.postDelayed(readAndSet, UPDATE_INTERVAL);
        }
    };

    public void getCounter() {
        jockey.send("get-counter", wv);
        Log.d(TAG, "get count request sent");
    }

    private void setJockeyEvents() {
        jockey.on("set-counter", new JockeyHandler() {

            @Override
            protected void doPerform(Map<Object, Object> payload) {
                counter = payload.get("counter").toString();
                Log.d(TAG, "updated counter received: " + payload.get("counter"));
            }
        });

        jockey.on("log", new JockeyAsyncHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Log.d(TAG, "JOCKEY SAYS HI!");
            }
        });
    }


}

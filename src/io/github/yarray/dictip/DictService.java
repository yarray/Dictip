package io.github.yarray.dictip;

import openones.stardictcore.StarDict;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class DictService extends Service {
    private StarDict _dict;
    private Calendar _last = Calendar.getInstance();
    static private boolean _running = false;
    final private String TAG = "service";
    private int _currentPriority = -1;

    OnPrimaryClipChangedListener _clipListener = new OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = clipboard.getPrimaryClip();
            if (data == null) {
                return;
            }
            ClipData.Item item = data.getItemAt(0);
            if (item == null) {
                return;
            }
            CharSequence text = data.getItemAt(0).getText();
            Calendar now = Calendar.getInstance();
            if (text != null && now.getTimeInMillis() - _last.getTimeInMillis() > 1000) {
                _last = now;
                showTip(getTrans(text.toString()));
            }
        }
    };

    private String getTrans(String word) {
        String target = word;
        String res = _dict.lookupWord(target);
        if (res.equals("not found")) {
            Stemmer stemmer = new Stemmer();
            for (char c : word.toCharArray()) {
                stemmer.add(c);
            }
            stemmer.stem();
            target = stemmer.toString();
            res = _dict.lookupWord(target);
        }
        res = target + "\n" + res;
        // Here is a special treatment for the Langdao English-Chinese Dictionary
        int trimPoint = res.lastIndexOf("相关词组");
        if (trimPoint > 0) {
            return res.substring(0, trimPoint).trim();
        } else {
            return res;
        }
    }

    public void showTip(String text) {
        Context applicationContext = getApplicationContext();
        if (applicationContext == null) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.tip_toast, null);
        if (layout == null) {
            return;
        }

        TextView content = (TextView) layout.findViewById(R.id.text);
        content.setText(text);

        final Toast toast = new Toast(applicationContext);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        Log.i(TAG, "show toast");
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 2000);
    }

    private void toggleMonitoring(boolean on) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (on) {
            clipboard.removePrimaryClipChangedListener(_clipListener);
            clipboard.addPrimaryClipChangedListener(_clipListener);
        } else {
            clipboard.removePrimaryClipChangedListener(_clipListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        Log.i(TAG, "intent come in");
        if (!_running) {
            Log.i(TAG, "init");
            if (_dict == null) {
                String dictPath = intent.getExtras().getString("DICT_PATH");
                _dict = new StarDict(dictPath == null
                        ? getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE).getString("DICT_PATH", "")
                        : dictPath);
            }
        }

        Log.i(TAG, "toggle");
        boolean on = intent.getExtras().getBoolean("ON");

        int priority = intent.getExtras().getInt("PRIORITY");

        if (on || priority >= _currentPriority) {
            toggleMonitoring(on);
            Intent toggled = new Intent();
            toggled.setAction(Constants.TOGGLED_ACTION);
            toggled.putExtra("on", on);
            sendBroadcast(toggled);

            if (!on) {
                _currentPriority = -1;
            } else {
                _currentPriority = Math.max(priority, _currentPriority);
            }
        }

        _running = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        _running = false;
        super.onDestroy();
    }
}
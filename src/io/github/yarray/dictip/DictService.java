package io.github.yarray.dictip;

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

import org.jactiveresource.Inflector;

import java.util.Calendar;

import openones.stardictcore.StarDict;


public class DictService extends Service {
    private StarDict _dict;
    private Calendar _last = Calendar.getInstance();
    final private String TAG = "service";

    private StarDict _globalDict;
    private boolean _globalOn = false;

    private Preferences _pref = new Preferences(this);

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
            target= Inflector.singularize(word);
            res = _dict.lookupWord(target);
        }
        if (res.equals("not found")) {
            Stemmer stemmer = new Stemmer();
            for (char c : word.toCharArray()) {
                stemmer.add(c);
            }
            stemmer.stem();
            target = stemmer.toString();
            res = _dict.lookupWord(target);
        }
        res = target.toLowerCase() + "\n" + res;
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

        String dictName = intent.getExtras().getString("DICT_NAME");
        if (dictName != null) {
            _dict = new StarDict(_pref.getDictPath(dictName));
        }
        else if (_dict == null) {
            Log.i(TAG, "init");
            _dict = new StarDict(_pref.getSelectedDictPath());
        }

        Log.i(TAG, "toggle");

        boolean global = intent.getExtras().getBoolean("GLOBAL");
        boolean on = intent.getExtras().getBoolean("ON");

        if (global) {
            _globalDict = _dict;
            _globalOn = on;
            _pref.selectDict(dictName);
        }

        toggleMonitoring(on);

        if (!global && !on) {
            _dict = _globalDict;
            toggleMonitoring(_globalOn);
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
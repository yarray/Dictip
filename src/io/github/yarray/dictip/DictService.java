package io.github.yarray.dictip;

import openones.stardictcore.StarDict;
import android.app.Service;
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

public class DictService extends Service {
	private StarDict _dict;
	final private String TAG = "service";

	OnPrimaryClipChangedListener _clipListener = new OnPrimaryClipChangedListener() {
		@Override
		public void onPrimaryClipChanged() {
			final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			CharSequence text = clipboard.getPrimaryClip().getItemAt(0)
					.getText();
			if (text != null) {
				showTip(getTrans(text.toString()));
			}
		}
	};

	private String getTrans(String word) {
		String target = word;
		String res = _dict.lookupWord(target);
		if (res == "not found") {
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
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.tip_toast, null);

		TextView content = (TextView) layout.findViewById(R.id.text);
		content.setText(text);

		final Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
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
		Log.i(TAG, "intent come in");
		if (intent.getAction() == Constants.INIT_ACTION) {
			Log.i(TAG, "init");
			_dict = new StarDict(intent.getExtras().getString("DICT_PATH"));
		} else if (intent.getAction() == Constants.TOGGLE_ACTION) {
			Log.i(TAG, "toggle");
			boolean on = intent.getExtras().getBoolean("ON");
			toggleMonitoring(on);
			Intent toggled = new Intent();
			toggled.setAction(Constants.TOGGLED_ACTION);
			toggled.putExtra("on", on);
			sendBroadcast(toggled);
		}
		return super.onStartCommand(intent, flags, startId);
	}
}

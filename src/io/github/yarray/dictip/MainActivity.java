package io.github.yarray.dictip;

import io.github.yarray.dictip.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
//import android.widget.Toast;

public class MainActivity extends Activity {
	private boolean _on;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String dir = getFilesDir().getAbsolutePath();
		String dictPath = copyAssets("dict.dict", dir);
		copyAssets("dict.ifo", dir);
		copyAssets("dict.idx", dir);

		Intent initIntent = new Intent(this, DictService.class);
		initIntent.setAction(Constants.INIT_ACTION);
		initIntent.putExtra("DICT_PATH", dictPath);
		startService(initIntent);
		
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context ctx, Intent intent) {
				updateToggle(findViewById(R.id.button1), intent.getExtras().getBoolean("on"));
			}
		}, new IntentFilter(Constants.TOGGLED_ACTION));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void toggle(View btn) {
		sendToggle(updateToggle(btn, !_on));
	}
	
	public void sendToggle(boolean on) {
		Intent intent = new Intent(this, DictService.class);
		intent.setAction(Constants.TOGGLE_ACTION);
		intent.putExtra("ON", on);
		startService(intent);
	}

	private boolean updateToggle(View btn, boolean on) {
		_on = on;
		if (on) {
			btn.setBackground(getResources().getDrawable(
					R.drawable.roundedbutton_on));
		} else {
			btn.setBackground(getResources().getDrawable(
					R.drawable.roundedbutton));
		}
//		Toast.makeText(getApplicationContext(), "Dictip " + (on ? "on" : "off"),
//				Toast.LENGTH_SHORT).show();
		return on;
	}

	private String copyAssets(String filename, String path) {
		AssetManager assets = getAssets();
		File test = new File(filename);
		if (test.exists()) {
			return test.getAbsolutePath();
		}
		InputStream in = null;
		OutputStream out = null;
		File outFile = null;
		try {
			in = assets.open(filename);
			outFile = new File(path, filename);
			out = new FileOutputStream(outFile);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return outFile.getAbsolutePath();
		} catch (IOException e) {
			Log.e("main", "Failed to copy asset file: " + filename, e);
			return "";
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
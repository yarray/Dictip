package io.github.yarray.dictip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ToggleReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		intent.setClass(context, DictService.class);
		context.startService(intent);
	}
}
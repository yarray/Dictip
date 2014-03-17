package io.github.yarray.dictip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serve = new Intent(context, DictService.class);
        context.startService(serve);
    }
}

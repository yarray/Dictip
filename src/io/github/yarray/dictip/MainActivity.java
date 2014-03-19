package io.github.yarray.dictip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
//import android.widget.Toast;

public class MainActivity extends Activity {
    private boolean _on;
    private String[] _dictList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _dictList = new Initializer(this).init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Initializer(this).init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        PackageManager packageManager = getPackageManager();
        assert packageManager != null;
        MenuItem startOnBootCheckbox = menu.findItem(R.id.start_on_boot);
        assert startOnBootCheckbox != null;
        startOnBootCheckbox.setChecked(
                packageManager.getComponentEnabledSetting(new ComponentName(this, BootReceiver.class))
                        == PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_on_boot:
                item.setChecked(!item.isChecked());
                PackageManager packageManager = getPackageManager();
                assert packageManager != null;
                packageManager.setComponentEnabledSetting(new ComponentName(this, BootReceiver.class),
                        (item.isChecked() ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED),
                        PackageManager.DONT_KILL_APP);

                Toast.makeText(this, item.isChecked() ? "start on boot" : "not start on boot",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.select_dict:
                AlertDialog.Builder availableDictList = new AlertDialog.Builder(this);
                availableDictList.setTitle("Select a dictionary");
                availableDictList.setItems(_dictList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendToggle(_on, _dictList[which]);
                    }
                });
                availableDictList.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void toggle(View btn) {
        sendToggle(updateToggle(btn, !_on), null);
    }

    public void sendToggle(boolean on, String dictName) {
        Intent intent = new Intent(this, DictService.class);
        intent.setAction(Constants.TOGGLE_ACTION);
        intent.putExtra("ON", on);
        intent.putExtra("GLOBAL", true);
        if (dictName != null) {
            intent.putExtra("DICT_NAME", dictName);
        }
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
}
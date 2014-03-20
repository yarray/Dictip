package io.github.yarray.dictip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

class Preferences {
    Context _context;

    public Preferences(Context context) {
        _context = context;
    }

    private SharedPreferences getPref() {
        return _context.getSharedPreferences(_context.getString(R.string.pref_file), Context.MODE_MULTI_PROCESS);
    }

    private void setString(String key, String value) {
        getPref().edit().putString(key, value).commit();
    }

    public String getDefaultDictName() {
        return "dict";
    }

    public void setDictHome(String dictHome) {
        setString("DICT_HOME", dictHome);
    }

    public String getDictHome() {
        return getPref().getString("DICT_HOME", Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    public String getDictPath(String dictName) {
        return getDictHome() + "/" + dictName + ".dict";
    }

    public String getSelectedDictPath() {
        return getDictPath(getSelectedDictName());
    }

    public String getSelectedDictName() {
        return getPref().getString("DICT_NAME", getDefaultDictName());
    }

    public void selectDict(String dictName) {
        setString("DICT_NAME", dictName);
    }
}

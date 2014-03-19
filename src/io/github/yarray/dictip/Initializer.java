package io.github.yarray.dictip;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


class Initializer {
    Context _context;
    private final String _dir;

    public Initializer(Context context) {
        _context = context;
        File filesDir = _context.getExternalFilesDir(null);
        assert filesDir != null;
        _dir = filesDir.getAbsolutePath();
        Log.i("init", _dir);
    }

    public void init() {
        // init the default dictionary
        String dictName = "dict";
        copyAssets(dictName + ".dict", _dir);
        copyAssets(dictName + ".ifo", _dir);
        copyAssets(dictName + ".idx", _dir);

        getPref().edit().putString("DICT_PATH",
                getPref().getString("DICT_PATH", getDictPath(dictName))).commit();
    }

    public void init(String dictName) {
        getPref().edit().putString("DICT_PATH", getDictPath(dictName)).commit();
    }

    private String getDictPath(String dictName) {
        return _dir + "/" + dictName + ".dict";
    }

    private SharedPreferences getPref() {
        return _context.getSharedPreferences(_context.getString(R.string.pref_file), Context.MODE_PRIVATE);
    }

    private String copyAssets(String filename, String path) {
        AssetManager assets = _context.getAssets();
        File test = new File(path, filename);
        if (test.exists()) {
            return test.getAbsolutePath();
        }
        Log.i("main", "real copy");
        InputStream in;
        OutputStream out;
        File outFile;
        try {
            in = assets.open(filename);
            outFile = new File(path, filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
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

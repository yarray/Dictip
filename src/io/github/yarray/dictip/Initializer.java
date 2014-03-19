package io.github.yarray.dictip;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Initializer {
    Context _context;
    private final String _dir;
    private Preferences _pref;
    private Pattern _dictPattern = Pattern.compile("(.*?)\\.dict$");
    private Pattern _dzPattern = Pattern.compile("(.*\\.dict\\.?)\\.dz$");

    public Initializer(Context context) {
        _context = context;
        File filesDir = _context.getExternalFilesDir(null);
        assert filesDir != null;
        _dir = filesDir.getAbsolutePath();
        _pref = new Preferences(_context);
        _pref.setDictHome(_dir);
        Log.i("init", _dir);
    }

    public String[] init() {
        // init the default dictionary
        String dictName = _pref.getDefaultDictName();
        copyAssets(dictName + ".dict", _dir);
        copyAssets(dictName + ".ifo", _dir);
        copyAssets(dictName + ".idx", _dir);

        return initUserDictionaries();
    }

    public String[] initUserDictionaries() {
        File[] files = new File(_dir).listFiles();
        if (files == null) {
            return new String[] {};
        }
        for (File f: files) {
            Matcher match = _dzPattern.matcher(f.getAbsolutePath());
            if (match.find() && !new File(match.group(1)).exists()) {
                try {
                    Utils.decompressGzip(f.getAbsolutePath(), match.group(1));
                } catch (IOException e) {
                    Log.e("init", "decompress dict.dz error", e);
                }
            }
        }

        ArrayList<String> dictList = new ArrayList<String>();
        for (File f: files) {
            Matcher match = _dictPattern.matcher(f.getAbsolutePath());

            if (match.find()) {
                dictList.add(new File(match.group(1)).getName());
            }
        }
        return dictList.toArray(new String[dictList.size()]);
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

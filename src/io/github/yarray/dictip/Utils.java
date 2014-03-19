package io.github.yarray.dictip;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

class Utils {
    public static void decompressGzip(String input, String output) throws IOException {
        byte[] buffer = new byte[1024];

        try {
            GZIPInputStream gzStream = new GZIPInputStream(new FileInputStream(input));
            FileOutputStream outputStream = new FileOutputStream(output);
            int bytesRead;
            while ((bytesRead = gzStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            gzStream.close();
            outputStream.close();

            System.out.println("The file was decompressed successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

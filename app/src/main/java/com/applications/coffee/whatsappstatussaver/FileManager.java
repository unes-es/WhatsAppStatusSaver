package com.applications.coffee.whatsappstatussaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.applications.coffee.whatsappstatussaver.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileManager {

    static private int savedFilesCpt = 0;
    public final static List<String> whatsAppFiles = new ArrayList<>();
    public final static List<String> savedFiles = new ArrayList<>();

    public final static String SAVED_STATUS_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp Status/";
    public final static String WHATSAPP_STATUS_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/.Statuses/";
    public static SharedPreferences preferences;

    public static void saveToGallery(String filePath){
        File f = new File(SAVED_STATUS_DIR);
        f.mkdir();
        try{
            copyFileFast(new File(filePath),new File(SAVED_STATUS_DIR + "SAVED_STATUS_"+savedFilesCpt+getExtensionFromPath(filePath)));
        }
        catch (IOException x) {
            Log.d("tag", "saveToGallery: "+x.getMessage());
        }
        savedFilesCpt++;
    }
    public static void saveToGallery(Set<Integer> selectedFiles){
        Log.d("tag", "saveToGallery: "+selectedFiles.size());
        savedFilesCpt = preferences.getInt("savedFilesCpt",0);
        for (Integer file:selectedFiles) {
            saveToGallery(whatsAppFiles.get(file));
        }
        preferences.edit().putInt("savedFilesCpt",savedFilesCpt).apply();
    }

    public static void fetchFilesFromDir(List<String> files,String dir){
        File f = new File(dir);
        files.clear();
        if(f.exists()) {
            for (File file : f.listFiles()) {
                if (file.isFile() && !file.getName().equals(".nomedia")) {
                    files.add(file.getAbsolutePath());
                }
            }
        }
    }

    public static void copy(File src, File dst) throws IOException
    {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    public static void delete(Set<Integer> files){
        for (Integer file:files) {
            new File(savedFiles.get(file)).delete();
        }
    }

    public static String getExtensionFromPath(String path){
        return path.substring(path.lastIndexOf("."));
    }

    private static void copyFileFast(File sourceLocation, File targtLocation) throws IOException {
        if (sourceLocation.exists()) {
            FileInputStream in = new FileInputStream(sourceLocation);
            FileOutputStream out = new FileOutputStream(targtLocation);
            Log.i("tag","source "+sourceLocation);
            Log.i("tag","des "+targtLocation);
            try { in = new FileInputStream(sourceLocation);
                out = new FileOutputStream(targtLocation);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buf = new byte[2048];
            int len;
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
            BufferedInputStream bufferedInputStream=new BufferedInputStream(in);
            while ((len = bufferedInputStream.read(buf)) > 0) {
                bufferedOutputStream.write(buf, 0, len);
            }
            in.close();
            bufferedOutputStream.close();
            out.close();
            Log.e("tag", "Copy file successful.");
        }
        else { Log.v("tag", "Copy file failed. Source file missing.");
        }
    }

    public static void share(Context context, List<String> files, Set<Integer> selectedFiles){
        ArrayList<Uri> uris = new ArrayList<>();
        for (Integer f:selectedFiles) {
            uris.add(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(), new File(files.get(f))));
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.setType("*/*");
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.share_to)));
    }

}
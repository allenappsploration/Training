package com.example.appsplorationdev.ver20;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by appsplorationdev on 8/12/14.
 */
public class FileCache {

    private File cacheDir;

    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            cacheDir = new File (android.os.Environment.getExternalStorageDirectory(), "TempImages");
        else
            cacheDir= context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile (String url){
        String filename = String.valueOf(url.hashCode());
        File f = new File (cacheDir, filename);
        return f;
    }

    public void clear(){
        File[] files = cacheDir.listFiles();
        if(files == null)
            return;
        for(File f:files)
            f.delete();
    }
}

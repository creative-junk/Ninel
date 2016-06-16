package com.crysoft.me.pichat.helpers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Maxx on 6/16/2016.
 */
public class ReadFiles {
    public ReadFiles(){

    }
    public static String readRawFileAsString(Context context, int rawFile) throws java.io.IOException{
        InputStream inputStream = context.getResources().openRawResource(rawFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer result = new StringBuffer();
        String line;
        while((line = bufferedReader.readLine())!= null){
            result.append(line);
        }
        bufferedReader.close();
        return result.toString();
    }
}

package com.crysoft.me.pichat.helpers;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maxx on 6/20/2016.
 */
public class EmoticonsHelper {
    private static final String TAG = "Emoticons Helper";

    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();
    private static Map<Pattern,Integer> emoticons = new HashMap<Pattern, Integer>();
    private static final void load(){
    }
    private static void addPattern(Map<Pattern, Integer> map, String smile, int resource){
        map.put(Pattern.compile(Pattern.quote(smile)),resource);
    }
    public static boolean addSmiles(Context context,Spannable spannable){
        boolean hasChances = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()){
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()){
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start() && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else{
                        set = false;
                    }
                if (set){
                    hasChances = true;
                    ImageSpan imageSpan = new ImageSpan(context,entry.getValue());
                    spannable.setSpan(imageSpan, matcher.start(),matcher.end(), spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChances;
    }
    public static Spannable getSmiledText(Context context, CharSequence text){
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context,spannable);
        return spannable;
    }

}

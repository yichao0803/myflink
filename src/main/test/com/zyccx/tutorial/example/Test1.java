package com.zyccx.tutorial.example;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Test1 {


    @Test
    public void open() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());


        System.out.println(nowAsISO);

        // Input
        Date date = new Date(System.currentTimeMillis());

// Conversion
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("CTT"));
        String text = sdf.format(date);
        System.out.println(text);


        SimpleDateFormat sdf2;
        sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
        String text2 = sdf2.format(date);
        System.out.println(text2);
    }
}

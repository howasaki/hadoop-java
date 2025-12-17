package com.ue.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class LetterMapper extends Mapper<Object, Text, Text, IntWritable>{
    private final static IntWritable one = new IntWritable(1);
    private Text letter = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        for (char c : line.toCharArray()) {
            if (Character.isLetter(c)) {
                letter.set(String.valueOf(Character.toLowerCase(c)));
                context.write(letter, one);
            }
        }
    }
}
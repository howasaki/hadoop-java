package com.ue.hadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PrepositionSkippingMapper extends Mapper<Object, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    private static final Set<String> PREPOSITIONS_SP = new HashSet<>(Arrays.asList(
            "a", "ante", "bajo", "cabe", "con", "contra", "de", "desde", "durante", "en",
            "entre", "hacia", "hasta", "mediante", "para", "por", "según", "sin", "so",
            "sobre", "tras", "versus", "vía"
    ));

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString());
        while (itr.hasMoreTokens()) {
            String token = itr.nextToken().toLowerCase();
            if (!PREPOSITIONS_SP.contains(token)) {
                word.set(token);
                context.write(word, one);
            }
        }
    }
}
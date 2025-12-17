package com.ue.hadoop;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TopTenReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private PriorityQueue<AbstractMap.SimpleEntry<Text, IntWritable>> queue;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Min-heap to keep the largest elements
        queue = new PriorityQueue<>(11, new Comparator<AbstractMap.SimpleEntry<Text, IntWritable>>() {
            @Override
            public int compare(AbstractMap.SimpleEntry<Text, IntWritable> a, AbstractMap.SimpleEntry<Text, IntWritable> b) {
                return a.getValue().compareTo(b.getValue());
            }
        });
    }

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        // Add copy of data to queue
        queue.add(new AbstractMap.SimpleEntry<>(new Text(key), new IntWritable(sum)));

        if (queue.size() > 10) {
            queue.poll(); // Remove smallest
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<AbstractMap.SimpleEntry<Text, IntWritable>> topTen = new ArrayList<>();
        while (!queue.isEmpty()) {
            topTen.add(queue.poll());
        }
        // Reverse to get descending order
        Collections.reverse(topTen);

        for (AbstractMap.SimpleEntry<Text, IntWritable> entry : topTen) {
            context.write(entry.getKey(), entry.getValue());
        }
    }
}
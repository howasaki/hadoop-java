package com.ue.hadoop;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.PriorityQueue;
import java.util.AbstractMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.Text;

public class WordCount {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreElements()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        
        private IntWritable result = new IntWritable();
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }

    }

    public static class TopTenReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(TopTenReducer.class);
        job.setNumReduceTasks(1);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
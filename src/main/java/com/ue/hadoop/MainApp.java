package com.ue.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MainApp {

    private static Job configureJob(Configuration conf, String jobName, Class<?> jarByClass,
                                   Class<? extends Mapper> mapperClass,
                                   Class<? extends Reducer> combinerClass,
                                   Class<? extends Reducer> reducerClass,
                                   String inputPath, String outputPath) throws Exception {
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(jarByClass);
        job.setMapperClass(mapperClass);
        job.setCombinerClass(combinerClass);
        job.setReducerClass(reducerClass);
        job.setNumReduceTasks(1);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        return job;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: MainApp <app_name> <input_path> <output_path>");
            System.err.println("Available app_names: wordcount, topten, toptenletters");
            System.exit(-1);
        }

        String appName = args[0];
        String inputPath = args[1];
        String outputPath = args[2];

        Configuration conf = new Configuration();
        Job job;

        if ("wordcount".equalsIgnoreCase(appName)) {
            job = configureJob(conf, "word count", MainApp.class, TokenizerMapper.class, IntSumReducer.class, TopTenReducer.class, inputPath, outputPath);
        } else if ("topten".equalsIgnoreCase(appName)) {
            job = configureJob(conf, "top ten words", MainApp.class, PrepositionSkippingMapper.class, IntSumReducer.class, TopTenFilteredReducer.class, inputPath, outputPath);
        } else if ("toptenletters".equalsIgnoreCase(appName)) {
            job = configureJob(conf, "top ten letters", MainApp.class, LetterMapper.class, IntSumReducer.class, TopTenReducer.class, inputPath, outputPath);
        } else {
            System.err.println("Invalid app_name: " + appName);
            System.exit(-1);
            return;
        }

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

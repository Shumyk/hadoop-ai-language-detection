package org.bigdatainc;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.bigdatainc.util.FileUtil;

public class Main {
    public static final String MODE_TRAIN = "train";
    public static final String MODE_DETECT = "detect";

    public static void main(String[] args) throws Exception {
        final String inputPath = args[0];
        final String outputPath = args[1];
        final String mode = args.length == 3 ? args[2] : MODE_DETECT;

        if (MODE_TRAIN.equals(mode))
            trainNewLanguage(inputPath, outputPath);
        else
            detectLanguage(inputPath, outputPath);
    }

    private static void trainNewLanguage(final String inputPath,
                                         final String outputPath) throws IOException, InterruptedException, ClassNotFoundException {
        final Configuration config = new Configuration();
        config.set("mapreduce.output.textoutputformat.separator", ":");

        final Job job = Job.getInstance(config, "Language Training");
        job.setJarByClass(LanguageTraining.class);
        job.setMapperClass(LanguageTraining.TrainingMapper.class);
        job.setCombinerClass(LanguageTraining.TraningReducer.class);
        job.setReducerClass(LanguageTraining.TraningReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        final Path outputFolder = new Path(FileUtil.hadoopOutputsDirPath(outputPath));
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, outputFolder);

        job.waitForCompletion(false);

        FileUtil.copyLanguageTrainingResults(inputPath, outputFolder.toString());
    }

    private static void detectLanguage(final String inputPath,
                                       final String outputPath) throws IOException, InterruptedException, ClassNotFoundException {
        final Configuration config = new Configuration();

        final Job job = Job.getInstance(config, "Language Detection");
        job.setJarByClass(LanguageDetection.class);
        job.setMapperClass(LanguageDetection.BigramMapper.class);
        job.setCombinerClass(LanguageDetection.BigramReducer.class);
        job.setReducerClass(LanguageDetection.BigramReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        final Path outputFolder = new Path(FileUtil.hadoopOutputsDirPath(outputPath));
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, outputFolder);

        job.waitForCompletion(false);

        FileUtil.copyLanguageDetectionResults(outputFolder.toString());
    }
}
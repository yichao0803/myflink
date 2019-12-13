package myflink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;
import org.apache.flink.util.Preconditions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Implements the "WordCount" program that computes a simple word occurrence histogram
 * over text files.
 *
 * <p>The input is a plain text file with lines separated by newline characters.
 *
 * <p>Usage: <code>WordCount --input &lt;path&gt; --output &lt;path&gt;</code><br>
 * If no parameters are provided, the program is run with default data from {@link WordCountData}.
 *
 * <p>This example shows how to:
 * <ul>
 * <li>write a simple Flink program.
 * <li>use Tuple data types.
 * <li>write and use user-defined functions.
 * </ul>
 */
public class WordCount4 {

    // *************************************************************************
    //     PROGRAM
    // *************************************************************************
    public static void main(String[] args) throws Exception {

        final ParameterTool params2 = ParameterTool.fromArgs(args);

        // set up the execution environment
        final ExecutionEnvironment env2 = ExecutionEnvironment.getExecutionEnvironment();

        env2.setParallelism(1);
        // make parameters available in the web interface
        env2.getConfig().setGlobalJobParameters(params2);

        int count = 10;
        if (params2.has("count")) {
            count = params2.getInt("count");
        }

        // 创建容量为NUMBER的线程池。
        ExecutorService exeService = Executors.newFixedThreadPool(10);
        // 线程返回值
        ArrayList<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            TndspfcdFlagResult flagResult= new TndspfcdFlagResult(env2,i,count,params2);

            // 为每次就诊建立一个新线程
            TaskThreadFlagResult taskThreadFlagResult = new TaskThreadFlagResult(flagResult);
            // 线程返回值
            Future<Integer> future = exeService.submit(taskThreadFlagResult);
            futures.add(future);
        }
        exeService.shutdown();
    }


    /**
     * 多线程查询数据，返回结果LIST
     */
    public static class TaskThreadFlagResult implements Callable<Integer> {

        private TndspfcdFlagResult flagResult;

        public TaskThreadFlagResult(TndspfcdFlagResult flagResult) {
            this.flagResult = flagResult;
        }

        @Override
        public Integer call() throws Exception {

            String jobName="WordCount Example [" + (flagResult.i+1) + "/" + flagResult.count + "]";

            try {
                // get input data
                DataSet<String> text2 = null;

                if (flagResult.params2.has("input")) {
                    System.out.println(flagResult.params2.get("input"));

                    String input = flagResult.params2.get("input");
                    text2 = flagResult.env2.readTextFile(input);

                    Preconditions.checkNotNull(text2, "Input DataSet should not be null.");
                } else {
                    // get default test text data
                    System.out.println("Executing WordCount example with default input data set.");
                    System.out.println("Use --input to specify file input.");
                    text2 = WordCountData.getDefaultTextLineDataSet(flagResult.env2);
                }

                DataSet<Tuple2<String, Integer>> counts =
                        // split up the lines in pairs (2-tuples) containing: (word,1)
                        text2.flatMap(new Tokenizer())
                                // group by the tuple field "0" and sum up tuple field "1"
                                .groupBy(0)
                                .sum(1);

                // emit result
                if (flagResult.params2.has("output")) {
                    counts.writeAsCsv(flagResult.params2.get("output")+File.separator+(flagResult.i+1), "\n", " ");
                    // execute program
                } else {
                    System.out.println("Printing result to stdout. Use --output to specify output path.");

                    counts.print();

                }
                System.out.println(jobName);
                // flagResult.env2.execute(jobName);
            } catch (Exception e) {
                e.printStackTrace();
//                        System.out.println(e.getMessage());
//                        System.out.println(e.getStackTrace());
            }

            return 0;
        }
    }

    public static class  TndspfcdFlagResult{
        ExecutionEnvironment env2;
        Integer i;
        Integer count;
        ParameterTool params2;
        TndspfcdFlagResult(ExecutionEnvironment env2,Integer i,Integer count,ParameterTool params2){
            this.env2=env2;
            this.i=i;
            this.count=count;
            this.params2=params2;
        }
    }

    // *************************************************************************
    //     USER FUNCTIONS
    // *************************************************************************

    /**
     * Implements the string tokenizer that splits sentences into words as a user-defined
     * FlatMapFunction. The function takes a line (String) and splits it into
     * multiple pairs in the form of "(word,1)" ({@code Tuple2<String, Integer>}).
     */
    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }

    public static class WordCountData {

        public static final String[] WORDS = new String[]{
                "To be, or not to be,--that is the question:--",
                "Whether 'tis nobler in the mind to suffer",
                "The slings and arrows of outrageous fortune",
                "Or to take arms against a sea of troubles,",
                "And by opposing end them?--To die,--to sleep,--",
                "No more; and by a sleep to say we end",
                "The heartache, and the thousand natural shocks",
                "That flesh is heir to,--'tis a consummation",
                "Devoutly to be wish'd. To die,--to sleep;--",
                "To sleep! perchance to dream:--ay, there's the rub;",
                "For in that sleep of death what dreams may come,",
                "When we have shuffled off this mortal coil,",
                "Must give us pause: there's the respect",
                "That makes calamity of so long life;",
                "For who would bear the whips and scorns of time,",
                "The oppressor's wrong, the proud man's contumely,",
                "The pangs of despis'd love, the law's delay,",
                "The insolence of office, and the spurns",
                "That patient merit of the unworthy takes,",
                "When he himself might his quietus make",
                "With a bare bodkin? who would these fardels bear,",
                "To grunt and sweat under a weary life,",
                "But that the dread of something after death,--",
                "The undiscover'd country, from whose bourn",
                "No traveller returns,--puzzles the will,",
                "And makes us rather bear those ills we have",
                "Than fly to others that we know not of?",
                "Thus conscience does make cowards of us all;",
                "And thus the native hue of resolution",
                "Is sicklied o'er with the pale cast of thought;",
                "And enterprises of great pith and moment,",
                "With this regard, their currents turn awry,",
                "And lose the name of action.--Soft you now!",
                "The fair Ophelia!--Nymph, in thy orisons",
                "Be all my sins remember'd."
        };

        public static DataSet<String> getDefaultTextLineDataSet(ExecutionEnvironment env) {
            return env.fromElements(WORDS);
        }
    }
}
package myflink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class SplitUnionCount {

    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream text;
        //判断输入文件,否则直接获取words类的内容
        if (params.has("input")) {
            text = env.readTextFile(params.get("input"));
        } else {
            text = env.fromElements(WordsData.WORDS);
        }

        //text.print();
        SplitStream<Tuple2<String, Integer>> split = text.flatMap(
                new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                //按照单词进行分割
                String[] words = s.split("\\W+");
                for (String word : words) {
                    collector.collect(new Tuple2<>(word, 1));
                }
            }
        }).split(new OutputSelector<Tuple2<String, Integer>>() {
            @Override
            public Iterable<String> select(Tuple2<String, Integer> values) {
                //使用arraylist存储分流信息
                List<String> output = new ArrayList<>();
                //按照第一列单词是否包含the进行分组
                if (values.f0.equals("the")) {
                    output.add("a");
                } else {
                    output.add("b");
                }
                return output;
            }
        });

       // split.print();

        //把分组a的split流转成data stream流
        DataStream<Tuple2<String, Integer>> stream1 =
                split.select("a").keyBy(0).sum(1);

        //把分组b的split流转成data stream流
        DataStream<Tuple2<String, Integer>> stream2 =
                split.select("b").keyBy(0).sum(1);
//        stream2.print();
        //union合并a、b两个数据流
        DataStream<Tuple2<String, Integer>> stream = stream1.union(stream2).keyBy(0).sum(1);

        if (params.has("output")) {
            stream.writeAsText(params.get("output"));
        } else {
            //输出包含the的统计
           // stream1.print();
            //输出合并流之后的所有统计
            stream.print();
        }
        env.execute("split union stream");
    }

    public static class WordsData {

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

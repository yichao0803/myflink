package com.zyccx.tutorial.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class ValueStateTest3 {

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<Person> personDataStreamSource = env.addSource(new ValueStateTest3.PersonSource());
        KeyedStream<Person, String> personStringKeyedStream = personDataStreamSource.keyBy(person -> person.name);
        SingleOutputStreamOperator<Tuple2<String, Long>> tuple2SingleOutputStreamOperator =
                personStringKeyedStream.flatMap(new ValueStateTest3.AverageFlatMap());
        tuple2SingleOutputStreamOperator.print("test2");

        env.execute("value state test");

    }

    public static class Person implements Serializable {
        public String name;
        public String sex;
        public Integer age;
        public Long index;

        public Person() {
        }

        public Person(String name, String sex, Integer age, Long index) {
            this.name = name;
            this.sex = sex;
            this.age = age;
            this.index = index;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", sex=" + sex +
                    ", age=" + age +
                    '}';
        }
    }

    public static class PersonSource extends RichSourceFunction<Person> {

        /**
         * isRunning
         */
        private boolean isRunning = true;

        @Override
        public void run(SourceContext<Person> sourceContext) throws Exception {
            Long index = 0L;
            Random random = new Random();
            Integer key = 100000;
            while (isRunning) {

                switch ((int) (index++ % 5)) {
                    case 0:
                        sourceContext.collect(new Person("张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_张三_" + (index.intValue() % key), "男", 18 + random.nextInt(5), index));
                        break;
                    case 1:
                        sourceContext.collect(new Person("李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_李四_" + (index.intValue() % key), "男", 20 + random.nextInt(5), index));
                        break;
                    case 2:
                        sourceContext.collect(new Person("王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_王五_" + (index.intValue() % key), "男", 21 + random.nextInt(5), index));
                        break;
                    case 3:
                        sourceContext.collect(new Person("韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_韩梅梅_" + (index.intValue() % key), "女", 21 + random.nextInt(5), index));
                        break;
                    case 4:
                        sourceContext.collect(new Person("露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_露西_" +(index.intValue() % key), "女", 20 + random.nextInt(5), index));
                        break;
                    default:
                        break;
                }
                Thread.sleep(1);

            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    private static class AverageFlatMap extends RichFlatMapFunction<Person, Tuple2<String, Long>> {

        private ValueState<Tuple3<String, Long, Long>> sum;

        @Override
        public void open(Configuration parameters) throws Exception {
//            StateTtlConfig ttlConfig = StateTtlConfig
//                    .newBuilder(Time.seconds(10))
//                    .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
//                    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
//                    .build();

            //super.open(parameters);
            ValueStateDescriptor<Tuple3<String, Long, Long>> descriptor = new ValueStateDescriptor<>(
                    "average",
                    TypeInformation.of(new TypeHint<Tuple3<String, Long, Long>>() {
                    }),
                    Tuple3.of("key", 0L, 0L)
            );
            //descriptor.enableTimeToLive(ttlConfig);
            sum = getRuntimeContext().getState(descriptor);

        }

        @Override
        public void flatMap(Person person, Collector<Tuple2<String, Long>> out) throws Exception {

            Tuple3<String, Long, Long> currentSum = sum.value();
            if ("key".equals(currentSum.f0)) currentSum.f0 = person.name;

            currentSum.f1 += 1;
            currentSum.f2 += person.age;

            sum.update(currentSum);
            if (person.index % 1000 == 0)
                ValueStateTest3.printMemory();
            out.collect(new Tuple2<>(currentSum.f0, currentSum.f2 / currentSum.f1));
        }
    }

    public static void printMemory() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage usage = memoryMXBean.getHeapMemoryUsage();
        System.out.printf("init heap: %s \n", getSize(usage.getInit()));
        System.out.printf("max heap: %s \n", getSize(usage.getMax()));
        System.out.printf("used heap: %s \n", getSize(usage.getUsed()));
        System.out.printf("committed heap: %s\n", getSize(usage.getCommitted()));

        System.out.println("\nFull Information:");
        System.out.printf("Heap Memory Usage: %s \n", usage);
        System.out.printf("Non-Heap Memory Usage: %s\n", memoryMXBean.getNonHeapMemoryUsage());

        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println("=====================jvm options==================");
        System.out.println(jvmArgs);


        System.out.println("=====================通过java来获取相关系统状态====================");
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        System.out.printf("Total_Memory(-Xms )： %s\n", getSize(totalMemory));
        System.out.printf("Max_Memory(-Xmx )： %s\n", getSize(maxMemory));
        System.out.printf("freeMemory： %s\n\n", getSize(freeMemory));

    }

    /**
     * getSize
     *
     * @param size size
     * @return
     */
    public static String getSize(Long size) {

        // GB
        Long GB = 1024 * 1024 * 1024L;
        // MB
        Long MB = 1024 * 1024L;
        // KB
        Long KB = 1024L;
        // 格式化小数
        DecimalFormat df = new DecimalFormat("0.00");
        String resultSize = "";
        if (size / GB >= 1) {
            resultSize = String.format("%sGB", df.format(size / (float) GB));
        } else if (size / MB >= 1) {
            resultSize = String.format("%sMB", df.format(size / (float) MB));
        } else if (size / KB >= 1) {
            resultSize = String.format("%sKB", df.format(size / (float) KB));
        } else {
            resultSize = String.format("%dB", size);
        }
        return resultSize;
    }
}

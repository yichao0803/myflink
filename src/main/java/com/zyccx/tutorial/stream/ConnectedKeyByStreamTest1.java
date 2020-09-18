package com.zyccx.tutorial.stream;

import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Int;


public class ConnectedKeyByStreamTest1 {

    static final OutputTag<Tuple3<String, Integer, Integer>> outputTagOne = new OutputTag<Tuple3<String, Integer, Integer>>("side-output-one") {    };
    static final OutputTag<Tuple3<String, Integer, Integer>> outputTagTwo = new OutputTag<Tuple3<String, Integer, Integer>>("side-output-two") {    };

    public void run(String[] args) throws Exception {
        // Checking input parameters
        final MultipleParameterTool params = MultipleParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);
        env.setParallelism(1);
        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(KeyByData.getSource());
        DataStreamSource<Tuple3<String, Integer, Integer>> sourceSame = env.fromCollection(KeyByData.getSource(6));


        // DataStream<Tuple3<String, Integer, Integer>> sideOutput = sourceSame.getSideOutput(outputTagOne);

        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> tuple3SingleOutputStreamOperator = source
                .keyBy(1)
                .connect(sourceSame.keyBy(1))
                .process(new SourceKeyedCoProcessFunction());

        DataStream<Tuple3<String, Integer, Integer>> sideOutputOne = tuple3SingleOutputStreamOperator.getSideOutput(outputTagOne);
        DataStream<Tuple3<String, Integer, Integer>> sideOutputTwo = tuple3SingleOutputStreamOperator.getSideOutput(outputTagTwo);

        source.print("source");
        sourceSame.print("sourceSame");
        tuple3SingleOutputStreamOperator.print("tuple3SingleOutputStreamOperator");
        sideOutputOne.print("sideOutputOne");
        sideOutputTwo.print("sideOutputTwo");

        env.execute("ConnectedKeyByStreamTest1");

    }

    /**
     * SourceRichCoFlatMapFunction
     */
    public static class SourceRichCoFlatMapFunction extends RichCoFlatMapFunction<Tuple3<String, Integer, Integer>,
            Tuple3<String, Integer, Integer>,
            Tuple3<String, Integer, Integer>> {

        Logger logger = LoggerFactory.getLogger(ConnectedKeyByStreamTest1.class);
        private ValueState<Tuple3<String, Integer, Integer>> oneState;
        private ValueState<Tuple3<String, Integer, Integer>> twoState;

        @Override
        public void open(Configuration parameters) throws Exception {
            oneState = getRuntimeContext().getState(
                    new ValueStateDescriptor<>(
                            "one"
                            , TypeInformation.of(new TypeHint<Tuple3<String, Integer, Integer>>() {
                    })
                            , new Tuple3<>("", 0, 0)));
            twoState = getRuntimeContext().getState(
                    new ValueStateDescriptor<>(
                            "two"
                            , TypeInformation.of(new TypeHint<Tuple3<String, Integer, Integer>>() {
                    })
                            , new Tuple3<>("", 0, 0)));
        }

        @Override
        public void flatMap1(Tuple3<String, Integer, Integer> one, Collector<Tuple3<String, Integer, Integer>> out) throws Exception {
            Tuple3<String, Integer, Integer> two = twoState.value();
            if (two != null && two.f1.equals(one.f1)) {
                twoState.clear();
                oneAddTwo(one, two, out);

            } else {
                oneState.update(one);
            }
        }


        @Override
        public void flatMap2(Tuple3<String, Integer, Integer> two, Collector<Tuple3<String, Integer, Integer>> out) throws Exception {
            Tuple3<String, Integer, Integer> one = oneState.value();
            if (one != null && one.f1.equals(two.f1)) {
                oneState.clear();
                oneAddTwo(one, two, out);

            } else {
                twoState.update(two);
            }
        }

        private void oneAddTwo(Tuple3<String, Integer, Integer> one, Tuple3<String, Integer, Integer> two, Collector<Tuple3<String, Integer, Integer>> out) {
            out.collect(new Tuple3<>(one.f0, one.f1, one.f2 + two.f2));
        }
    }

    public static class SourceKeyedCoProcessFunction extends KeyedCoProcessFunction<Integer,
            Tuple3<String, Integer, Integer>,
            Tuple3<String, Integer, Integer>,
            Tuple3<String, Integer, Integer>> {

        Logger logger = LoggerFactory.getLogger(ConnectedKeyByStreamTest1.class);
        private ValueState<Tuple3<String, Integer, Integer>> oneState;
        private ValueState<Tuple3<String, Integer, Integer>> twoState;

        @Override
        public void open(Configuration parameters) throws Exception {
            oneState = getRuntimeContext().getState(
                    new ValueStateDescriptor<>(
                            "one"
                            , TypeInformation.of(new TypeHint<Tuple3<String, Integer, Integer>>() {
                    })
                            , new Tuple3<>("", 0, 0)));
            twoState = getRuntimeContext().getState(
                    new ValueStateDescriptor<>(
                            "two"
                            , TypeInformation.of(new TypeHint<Tuple3<String, Integer, Integer>>() {
                    })
                            , new Tuple3<>("", 0, 0)));
        }

        @Override
        public void processElement1(Tuple3<String, Integer, Integer> one, Context ctx, Collector<Tuple3<String, Integer, Integer>> out) throws Exception {
            Tuple3<String, Integer, Integer> two = twoState.value();
            if (two != null && two.f1.equals(one.f1)) {
                twoState.clear();
                oneAddTwo(one, two, out);

            } else {
                oneState.update(one);
            }
        }

        @Override
        public void processElement2(Tuple3<String, Integer, Integer> two, Context ctx, Collector<Tuple3<String, Integer, Integer>> out) throws Exception {
            Tuple3<String, Integer, Integer> one = oneState.value();
            if (one != null && one.f1.equals(two.f1)) {
                oneState.clear();
                oneAddTwo(one, two, out);

            } else {
                twoState.update(two);
            }
        }
        private void oneAddTwo(Tuple3<String, Integer, Integer> one, Tuple3<String, Integer, Integer> two, Collector<Tuple3<String, Integer, Integer>> out) {
            out.collect(new Tuple3<>(one.f0, one.f1, one.f2 + two.f2));
        }


        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple3<String, Integer, Integer>> out) throws Exception {
            if (oneState.value() != null) {
                ctx.output(outputTagOne, oneState.value());
                oneState.clear();
            }
            if (twoState.value() != null) {
                ctx.output(outputTagTwo, twoState.value());
                twoState.clear();
            }
        }
    }


    public static void main(String[] args) {
        ConnectedKeyByStreamTest1 keyByStreamTest1 = new ConnectedKeyByStreamTest1();
        try {
            keyByStreamTest1.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

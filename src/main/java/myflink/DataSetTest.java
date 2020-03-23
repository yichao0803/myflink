package myflink;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DataSetTest {


    public static class User { public String name; public int zip; }
    public static class Store { public String mgr; public int zip; }

    public static void main(String[] args) throws Exception {
        // set up the batch execution environment
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        List<User> users= new ArrayList();
        List<Store> stores= new ArrayList();
        for (int i = 0; i < 5; i++) {
            User user=new User();
            user.name="张三"+i;
            user.zip=i;
            users.add(user);

            if(i==4) continue;
            Store store=new Store();
            store.mgr="Sore"+i;
            store.zip=i;
            stores.add(store);
        }


        DataSet<User> input1 =  env.fromCollection(users);
                DataSet<Store> input2 = env.fromCollection(stores);
        // result dataset is typed as Tuple2
        DataSet<Tuple2<User, Store>>
                result = input1.leftOuterJoin(input2)
                .where("zip")       // key of the first input (users)
                .equalTo("zip").with(new JoinFunction<User, Store, Tuple2<User, Store>>() {
                    @Override
                    public Tuple2<User, Store> join(User first, Store second) throws Exception {
                        return new Tuple2<User, Store>(first,second);
                    }
                });;    // key of the second input (stores)

        result.print();


        /*
         * Here, you can start creating your execution plan for Flink.
         *
         * Start with getting some data from the environment, like
         * 	env.readTextFile(textPath);
         *
         * then, transform the resulting DataSet<String> using operations
         * like
         * 	.filter()
         * 	.flatMap()
         * 	.join()
         * 	.coGroup()
         *
         * and many more.
         * Have a look at the programming guide for the Java API:
         *
         * http://flink.apache.org/docs/latest/apis/batch/index.html
         *
         * and the examples
         *
         * http://flink.apache.org/docs/latest/apis/batch/examples.html
         *
         */

        // execute program
        env.execute("Flink Batch Java API Skeleton");
    }

//    public class PointAssigner
//            implements FlatJoinFunction<Tuple2<String, String>, Rating, Tuple2<String, Integer>> {
//        @Override
//        public void join(Tuple2<String, String> movie, Rating rating
//    Collector<Tuple2<String, Integer>> out) {
//            if (rating == null ) {
//                out.collect(new Tuple2<String, Integer>(movie.f0, -1));
//            } else if (rating.points < 10) {
//                out.collect(new Tuple2<String, Integer>(movie.f0, rating.points));
//            } else {
//                // do not emit
//            }
//        }
//
//        DataSet<Tuple2<String, Integer>>
//                moviesWithPoints =
//                movies.leftOuterJoin(ratings) // [...]
}



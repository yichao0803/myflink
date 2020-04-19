package com.zyccx.tutorial.map;

import com.zyccx.tutorial.map.IncrementMapFunction;
import static org.junit.Assert.*; //必须是static
import org.junit.Test;

public class IncrementMapFunctionTest {

    @Test
    public void map() throws Exception {
        // instantiate your function
        IncrementMapFunction incrementer = new IncrementMapFunction();

        // call the methods that you have implemented
        assertEquals((Long) 3L, incrementer.map(2L) );
    }
}

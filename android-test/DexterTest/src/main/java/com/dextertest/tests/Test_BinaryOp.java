package com.dextertest.tests;

import java.util.Random;

public class Test_BinaryOp implements PropagationTest {

    @Override
    public int propagate(int argA) {
        int argB = (new Random()).nextInt();
        return argA + argB;
    }
}

package uk.ac.cam.db538.dexter.tests;

/**
 * Created by db538 on 7/22/13.
 */
public class PropagationTestExerciser extends TestExerciser {

    private final PropagationTest test;

    public PropagationTestExerciser(PropagationTest test) {
        this.test = test;
    }

    @Override
    public Test getTest() {
        return test;
    }

    @Override
    public boolean run() {
        int argNormal = 1;
        int resNormal = test.propagate(argNormal);

        int argTainted = new Test_Const().generate();
        int resTainted = test.propagate(argTainted);

        return !TaintChecker.isTainted(resNormal) && TaintChecker.isTainted(resTainted);
    }
}


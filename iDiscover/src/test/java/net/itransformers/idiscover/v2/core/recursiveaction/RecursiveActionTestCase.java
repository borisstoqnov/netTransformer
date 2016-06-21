package net.itransformers.idiscover.v2.core.recursiveaction;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * Created by vasko on 20.06.16.
 */
public class RecursiveActionTestCase {

    @Test
    public void testRecursiveActionJoin(){
        ForkJoinPool pool = new ForkJoinPool();
        ArrayList<SimpleRecursiveAction> globalTasks = new ArrayList<SimpleRecursiveAction>();
        List<RecursiveAction> initialTasks =  new ArrayList<RecursiveAction>();
        for (int i=0; i< 2; i++) {
            SimpleRecursiveAction task = new SimpleRecursiveAction(globalTasks, 2);
            initialTasks.add(task);
            pool.submit(task);

        }
        for (RecursiveAction initialTask : initialTasks) initialTask.quietlyJoin();
        for (SimpleRecursiveAction action : globalTasks) {
            Assert.assertTrue("Some tasks seems are not finished", action.isFinished());
        }
    }
}


class SimpleRecursiveAction extends RecursiveAction {
    boolean isFinished;
    private ArrayList<SimpleRecursiveAction> globalActions;
    private int level;

    public SimpleRecursiveAction(ArrayList<SimpleRecursiveAction> globalActions, int level) {
        this.globalActions = globalActions;
        this.level = level;
        this.globalActions.add(this);
    }

    @Override
    protected void compute() {
        try {
            Thread.sleep((long) (500*Math.random())+500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<RecursiveAction> recursiveActionList = new ArrayList<RecursiveAction>();
        for (int i=0; i < level; i++) {
            recursiveActionList.add(new SimpleRecursiveAction(globalActions, level-1));
        }
        System.out.println("wait level:"+level);
        invokeAll(recursiveActionList);
        System.out.println("level:"+level);
        isFinished = true;
    }

    public boolean isFinished() {
        return isFinished;
    }
}

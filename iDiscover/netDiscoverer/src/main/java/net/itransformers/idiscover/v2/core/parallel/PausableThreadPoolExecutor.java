package net.itransformers.idiscover.v2.core.parallel;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PausableThreadPoolExecutor extends ThreadPoolExecutor {
  public boolean isPaused;
  private ReentrantLock pauseLock = new ReentrantLock();
  private Condition unpaused = pauseLock.newCondition();
  private Latch activeTasksLatch = new Latch();


  public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
  }


  public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
  }

  public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
  }

  public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
  }

  private class Latch {
    private final Object synchObj = new Object();
    private int count;

    public boolean awaitZero(long waitMS) throws InterruptedException {
      long startTime = System.currentTimeMillis();
      synchronized (synchObj) {
        while (count > 0) {
          if ( waitMS != 0) {
            synchObj.wait(waitMS);
            long curTime = System.currentTimeMillis();
            if ( (curTime - startTime) > waitMS ) {                
              return count <= 0;
            }
          }
          else
            synchObj.wait();
        }
        return count <= 0; 
      }
    }
    public void countDown() {
      synchronized (synchObj) {
        if (--count <= 0) {
          // assert count >= 0;              
          synchObj.notifyAll();
        }
      }
    }
    public void countUp() {
      synchronized (synchObj) {
        count++;
      }
    }    
  }

  /**
   * Executed before a task is assigned to a thread.
   */
  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    pauseLock.lock();
    try {
      while (isPaused)
        unpaused.await();
    } catch (InterruptedException ie) {
      t.interrupt();
    } finally {
      pauseLock.unlock();
    }

    activeTasksLatch.countUp();
    super.beforeExecute(t, r);
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    try {
      super.afterExecute(r, t);
    }
    finally {
      activeTasksLatch.countDown();
    }
  }

  /**
   * Pause the threadpool. Running tasks will continue running, but new tasks
   * will not start untill the threadpool is resumed.
   */
  public void pause() {
    pauseLock.lock();
    try {
      isPaused = true;
    } finally {
      pauseLock.unlock();
    }
  }

  /**
   * Wait for all active tasks to end.
   */ 
  public boolean await(long timeoutMS) {
    // assert isPaused;
    try {
      return activeTasksLatch.awaitZero(timeoutMS);
    } catch (InterruptedException e) {
      // log e, or rethrow maybe
    }
    return false;
  }

  /**
   * Resume the threadpool.
   */
  public void resume() {
    pauseLock.lock();
    try {
      isPaused = false;
      unpaused.signalAll();
    } finally {
      pauseLock.unlock();
    }
  }

}
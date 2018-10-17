package cn.wenqi;

import cn.wenqi.lock.RedisLock;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class LockTest {

    @Test
    public void redisLockTest() {
        RedisLock redisLock=new RedisLock("abc");


        Runnable run1=()->{
            System.out.println("线程1 开始执行，开始抢占锁...");
            try{
                redisLock.lock();
                System.out.println("线程1 获得锁，开始执行");
                try {
                    Thread.sleep(4000);
                    System.out.println("线程1 执行完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }finally {
                redisLock.unlock();
                System.out.println("线程1 释放锁");
            }
        };

        Runnable run2=()->{
            System.out.println("线程2 开始执行，开始抢占锁...");
            try{
                redisLock.lock();
                System.out.println("线程2 获得锁，开始执行");
                try {
                    Thread.sleep(4000);
                    System.out.println("线程2 执行完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }finally {
                redisLock.unlock();
                System.out.println("线程2 释放锁");
            }
        };

        new Thread(run1).start();
        new Thread(run2).start();
    }
}

package cn.wenqi.lock;

import cn.wenqi.redis.RedisMock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author wenqi
 * @since v1.0.1
 */
public class RedisLock implements Lock {

    private final LockSupport lockSupport;

    private final String lockKey;


    public RedisLock(String lockKey){
        lockSupport=new LockSupport();
        this.lockKey=lockKey;
    }

    @Override
    public void lock() {
        lockSupport.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lockSupport.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return lockSupport.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lockSupport.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        lockSupport.release();
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    class LockSupport extends AbstractQueuedSynchronizer{
        @Override
        protected boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final void lock(){
            acquire(1);
        }

        final void release(){
            release(1);
        }
        //非公平的
        @Override
        protected boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (RedisMock.lock(lockKey) && compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }else
                    RedisMock.unlock(lockKey);//如果有其中一个失败，则先释放redis锁
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");

                setState(nextc);
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0 && RedisMock.unlock(lockKey)) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
    }
}

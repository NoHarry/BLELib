package cc.noharry.blelib.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolProxy {

    ThreadPoolExecutor mExecutor;
    private int mCorePoolSize;
    private int mMaximumPoolSize;
    private String name;


    /**
     * @param corePoolSize    核心池的大小
     * @param maximumPoolSize 最大线程数
     */
    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize) {
       this(corePoolSize,maximumPoolSize,"");
    }

    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, String name) {
        mCorePoolSize = corePoolSize;
        mMaximumPoolSize = maximumPoolSize;
        this.name = name;
    }

    /**
     * 初始化ThreadPoolExecutor
     * 双重检查加锁,只有在第一次实例化的时候才启用同步机制,提高了性能
     */
    private void initThreadPoolExecutor() {
        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            synchronized (ThreadPoolProxy.class) {
                if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                    long keepAliveTime = 3000;
                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>();
                    ThreadFactory threadFactory = new LocalTheadFactory(name);
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();

                    mExecutor = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, keepAliveTime, unit, workQueue,
                            threadFactory, handler);
                }
            }
        }
    }

    /**
     * 执行任务
     */
    public void execute(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.execute(task);
    }

    /**
     * 提交任务
     */
    public Future<?> submit(Runnable task) {
        initThreadPoolExecutor();
        return mExecutor.submit(task);
    }

    /**
     * 移除任务
     */
    public void remove(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.remove(task);
    }

    static class LocalTheadFactory implements ThreadFactory {
        private static final AtomicInteger POOLNUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final boolean daemoThread;

        public LocalTheadFactory(){

            this("",false);
        }

        public LocalTheadFactory(String namePrefix){
            this(namePrefix,false);
        }

        public LocalTheadFactory(String namePrefix,boolean daemo) {
            this.namePrefix = TextUtils.isEmpty(namePrefix)
                ?"blelib-pool-"+POOLNUMBER.getAndIncrement()+"-thread-"
                :"blelib-pool-"+POOLNUMBER.getAndIncrement()+"-"+namePrefix+"-thread-";
            SecurityManager s = System.getSecurityManager();
            group=(s!=null)?s.getThreadGroup():Thread.currentThread().getThreadGroup();
            daemoThread=daemo;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            String name=namePrefix+threadNumber.getAndIncrement();
            Thread thread=new Thread(group,r,name,0);
            thread.setDaemon(daemoThread);
            return thread;
        }
    }
}
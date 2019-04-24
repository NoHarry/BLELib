package cc.noharry.blelib.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduledThreadPoolProxy {

    ScheduledThreadPoolExecutor mExecutor;
    private int mCorePoolSize;
    private int mMaximumPoolSize;
    private String name;


    /**
     * @param corePoolSize    核心池的大小
     * @param maximumPoolSize 最大线程数
     */
    public ScheduledThreadPoolProxy(int corePoolSize, int maximumPoolSize) {
       this(corePoolSize,maximumPoolSize,"");
    }

    public ScheduledThreadPoolProxy(int corePoolSize, int maximumPoolSize, String name) {
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
            synchronized (ScheduledThreadPoolProxy.class) {
                if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                    ThreadFactory threadFactory = new LocalTheadFactory(name);
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
                    mExecutor = new ScheduledThreadPoolExecutor(mCorePoolSize,threadFactory,handler);
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

    public ScheduledFuture<?> schedule(Runnable task,long delay, TimeUnit unit) {
        initThreadPoolExecutor();
        return mExecutor.schedule(task,delay,unit);
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

    public static class LocalTheadFactory implements ThreadFactory {
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
                ?"blelib-"+POOLNUMBER.getAndIncrement()+"-thread-"
                :"blelib-"+POOLNUMBER.getAndIncrement()+"-"+namePrefix+"-thread-";
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
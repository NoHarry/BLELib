package cc.noharry.bledemo.util;

public class ThreadPoolProxyFactory {
    static ThreadPoolProxy mNormalThreadPoolProxy;
    static ThreadPoolProxy mUpdateThreadPoolProxy;
    static ThreadPoolProxy mLogThreadPoolProxy;

    /**
     * 得到普通线程池代理对象mNormalThreadPoolProxy
     */
    public static ThreadPoolProxy getNormalThreadPoolProxy() {
        if (mNormalThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(5, 5);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

    public static ThreadPoolProxy getUpdateThreadPoolProxy() {
        if (mUpdateThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mUpdateThreadPoolProxy == null) {
                    mUpdateThreadPoolProxy = new ThreadPoolProxy(1, 1);
                }
            }
        }
        return mUpdateThreadPoolProxy;
    }

    public static ThreadPoolProxy getLogThreadPoolProxy() {
        if (mLogThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mLogThreadPoolProxy == null) {
                    mLogThreadPoolProxy = new ThreadPoolProxy(1, 1);
                }
            }
        }
        return mLogThreadPoolProxy;
    }

}
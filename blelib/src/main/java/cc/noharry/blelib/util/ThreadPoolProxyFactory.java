package cc.noharry.blelib.util;

public class ThreadPoolProxyFactory {
    static ThreadPoolProxy mNormalThreadPoolProxy;
    static ThreadPoolProxy mScanThreadPoolProxy;
    static ThreadPoolProxy mTaskThreadPoolProxy;
    static ThreadPoolProxy mConnectionThreadPoolProxy;
    static ThreadPoolProxy mSingleScanThreadPoolProxy;



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

    public static ThreadPoolProxy getScanThreadPoolProxy() {
        if (mScanThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mScanThreadPoolProxy == null) {
                    mScanThreadPoolProxy = new ThreadPoolProxy(3, 3,"ScanHandler");
                }
            }
        }
        return mScanThreadPoolProxy;
    }

    public static ThreadPoolProxy getSingleScanThreadPoolProxy() {
        if (mSingleScanThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mSingleScanThreadPoolProxy == null) {
                    mSingleScanThreadPoolProxy = new ThreadPoolProxy(1, 1,"Scan");
                }
            }
        }
        return mSingleScanThreadPoolProxy;
    }

    public static ThreadPoolProxy getTaskThreadPoolProxy() {
        if (mTaskThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mTaskThreadPoolProxy == null) {
                    mTaskThreadPoolProxy = new ThreadPoolProxy(1, 1,"Task");
                }
            }
        }
        return mTaskThreadPoolProxy;
    }


    public static ThreadPoolProxy getConnectionThreadPoolProxy() {
        if (mConnectionThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mConnectionThreadPoolProxy == null) {
                    mConnectionThreadPoolProxy = new ThreadPoolProxy(1, 1,"Connection");
                }
            }
        }
        return mConnectionThreadPoolProxy;
    }
}
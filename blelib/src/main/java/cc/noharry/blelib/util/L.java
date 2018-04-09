/*   
 * Copyright (c) 2015-10-10 下午1:57:59  Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 * @author borturn  
 */
/**
 * @Title: L.java
 * @Package com.lingyun.rtc.doorphone.utils
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市呤云科技有限公司
 * 
 * @author borturn
 * @date 2015-10-10 下午1:57:59
 * @version V1.0
 */
package cc.noharry.blelib.util;

import android.util.Log;


public class L {


	// 是否需要打印bug
	public static boolean isDebug = true;
	private static final String TAG = "BLELIB";

	// 下面四个是默认tag的函数
	public static void i(String msg) {
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (isDebug)
			Log.v(TAG, msg);
	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}
}

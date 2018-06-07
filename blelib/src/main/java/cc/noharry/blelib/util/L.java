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
import cc.noharry.blelib.ble.BleAdmin;


public class L {


	// 是否需要打印bug
	public static boolean isDebug = true;
	public static int logStyle=0;
	private static final String TAG = "BLELIB";

	// 下面四个是默认tag的函数
	public static void i(String msg) {
		i(TAG,msg);
	}

	public static void d(String msg) {
		d(TAG,msg);
	}

	public static void e(String msg) {
	  e(TAG,msg);
	}

	public static void v(String msg) {
	  v(TAG,msg);
	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg) {
		if (isDebug){
			if (logStyle==BleAdmin.LOG_STYLE_LOGGER){
				Log.i(tag,"┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
				Log.i(tag,"|                                               BLELIB                ");
				Log.i(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.i(tag,"| Thread:"+Thread.currentThread().getName());
				Log.i(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.i(tag,"| "+targetStackTraceMSg());
				Log.i(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.i(tag,"| "+ msg);
				Log.i(tag,"└────────────────────────────────────────────────────────────────────────────────────────────────────────────────");

			}else {
				Log.i(tag,msg);
			}
		}

	}

	public static void d(String tag, String msg) {
		if (isDebug){
			if (logStyle==BleAdmin.LOG_STYLE_LOGGER){
				Log.d(tag,"┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
				Log.d(tag,"|                                               BLELIB                ");
				Log.d(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.d(tag,"| Thread:"+Thread.currentThread().getName());
				Log.d(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.d(tag,"| "+targetStackTraceMSg());
				Log.d(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.d(tag,"| "+ msg);
				Log.d(tag,"└────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
			}else {
				Log.d(tag, msg);
			}
		}

	}

	public static void e(String tag, String msg) {
		if (isDebug){
			if (logStyle==BleAdmin.LOG_STYLE_LOGGER){
				Log.e(tag,"┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
				Log.e(tag,"|                                               BLELIB                ");
				Log.e(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.e(tag,"| Thread:"+Thread.currentThread().getName());
				Log.e(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.e(tag,"| "+targetStackTraceMSg());
				Log.e(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.e(tag,"| "+ msg);
				Log.e(tag,"└────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
			}else {
				Log.e(tag, msg);
			}
		}

	}

	public static void v(String tag, String msg) {
		if (isDebug){
			if (logStyle==BleAdmin.LOG_STYLE_LOGGER){
				Log.v(tag,"┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
				Log.v(tag,"|                                               BLELIB                ");
				Log.v(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.v(tag,"| Thread:"+Thread.currentThread().getName());
				Log.v(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.v(tag,"| "+targetStackTraceMSg());
				Log.v(tag,"|┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
				Log.v(tag,"| "+ msg);
				Log.v(tag,"└────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
			}else {
				Log.v(tag, msg);
			}

		}

	}

	private static String targetStackTraceMSg() {
		StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
		if (targetStackTraceElement != null) {
			return "At " + targetStackTraceElement.getClassName() + "." + targetStackTraceElement.getMethodName() +
					"(" + targetStackTraceElement.getFileName() + ":" + targetStackTraceElement.getLineNumber() + ")";

		} else {
			return "";
		}
	}

	private static StackTraceElement getTargetStackTraceElement() {
		StackTraceElement targetStackTrace = null;
		boolean shouldTrace = false;
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		for (StackTraceElement stackTraceElement : stackTrace) {
			boolean isLogMethod = stackTraceElement.getClassName().equals(L.class.getName());
			if (shouldTrace && !isLogMethod) {
				targetStackTrace = stackTraceElement;
				break;
			}
			shouldTrace = isLogMethod;
		}
		return targetStackTrace;
	}



}

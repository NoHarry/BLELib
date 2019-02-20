
package cc.noharry.blelib.util;

import android.util.Log;
import cc.noharry.blelib.ble.BleAdmin;


public class L {


	// 是否需要打印bug
	public static boolean isDebug = true;
	public static int logStyle=0;
	public static final String TAG = "BLELIB";

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

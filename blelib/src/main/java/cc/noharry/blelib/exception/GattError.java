/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cc.noharry.blelib.exception;

import android.bluetooth.BluetoothGatt;

/**
 * Parses the error numbers according to the <b>gatt_api.h</b> file from bluedroid stack.
 * See: https://android.googlesource.com/platform/external/bluetooth/bluedroid/+/android-5.1.0_r1/stack/include/gatt_api.h (and other versions) for details.
 * See also: https://android.googlesource.com/platform/external/libnfc-nci/+/master/src/include/hcidefs.h#447 for other possible HCI errors.
 */
public class GattError {
	public static final int GATT_SUCCESS                =        0x00;
	public static final int GATT_INVALID_HANDLE         =        0x01;
	public static final int GATT_READ_NOT_PERMIT        =        0x02;
	public static final int GATT_WRITE_NOT_PERMIT       =        0x03;
	public static final int GATT_INVALID_PDU            =        0x04;
	public static final int GATT_INSUF_AUTHENTICATION   =        0x05;
	public static final int GATT_REQ_NOT_SUPPORTED      =        0x06;
	public static final int GATT_INVALID_OFFSET         =        0x07;
	public static final int GATT_INSUF_AUTHORIZATION    =        0x08;
	public static final int GATT_PREPARE_Q_FULL         =        0x09;
	public static final int GATT_NOT_FOUND              =        0x0a;
	public static final int GATT_NOT_LONG               =        0x0b;
	public static final int GATT_INSUF_KEY_SIZE         =        0x0c;
	public static final int GATT_INVALID_ATTR_LEN       =        0x0d;
	public static final int GATT_ERR_UNLIKELY           =        0x0e;
	public static final int GATT_INSUF_ENCRYPTION       =        0x0f;
	public static final int GATT_UNSUPPORT_GRP_TYPE     =        0x10;
	public static final int GATT_INSUF_RESOURCE         =        0x11;
	public static final int GATT_ILLEGAL_PARAMETER      =        0x87;
	public static final int GATT_NO_RESOURCES           =        0x80;
	public static final int GATT_INTERNAL_ERROR         =        0x81;
	public static final int GATT_WRONG_STATE            =        0x82;
	public static final int GATT_DB_FULL                =        0x83;
	public static final int GATT_BUSY                   =        0x84;
	public static final int GATT_ERROR                  =        0x85;
	public static final int GATT_CMD_STARTED            =        0x86;
	public static final int GATT_PENDING                =        0x88;
	public static final int GATT_AUTH_FAIL              =        0x89;
	public static final int GATT_MORE                   =        0x8a;
	public static final int GATT_INVALID_CFG            =        0x8b;
	public static final int GATT_SERVICE_STARTED        =        0x8c;
	public static final int GATT_ENCRYPED_NO_MITM       =        0x8d;
	public static final int GATT_NOT_ENCRYPTED          =        0x8e;
	public static final int GATT_CONGESTED              =        0x8f;
	public static final int GATT_CCC_CFG_ERR            =        0xFD; /* Client Characteristic Configuration Descriptor Improperly Configured */
	public static final int GATT_PRC_IN_PROGRESS        =        0xFE; /* Procedure Already in progress */
	public static final int GATT_OUT_OF_RANGE           =        0xFF; /* Attribute value out of range */


	public static final int GATT_CONN_UNKNOWN              =     0;
	public static final int GATT_CONN_L2C_FAILURE          =     1;         /* general L2cap failure  */
	public static final int GATT_CONN_TIMEOUT              =     0x08;      /* 0x08 connection timeout  */
	public static final int GATT_CONN_TERMINATE_PEER_USER  =     0x13;      /* 0x13 connection terminate by peer user  */
	public static final int GATT_CONN_TERMINATE_LOCAL_HOST =     0x16;   		/* 0x16 connectionterminated by local host  */
	public static final int GATT_CONN_FAIL_ESTABLISH       =     0x03E;			/* 0x03E connection fail to establish  */
	public static final int GATT_CONN_LMP_TIMEOUT          =     0x22;     	/* 0x22 connection fail for LMP response tout */
	public static final int GATT_CONN_CANCEL               =     0x0100;    /* 0x0100 L2CAP connection cancelled  */

	/**
	 * Converts the connection status given by the {@link android.bluetooth.BluetoothGattCallback#onConnectionStateChange(BluetoothGatt, int, int)} to error name.
	 * @param error the status number
	 * @return the error name as stated in the gatt_api.h file
	 */
	public static String parseConnectionError(final int error) {
		switch (error) {
			case BluetoothGatt.GATT_SUCCESS:
				return "SUCCESS";
			case GATT_CONN_L2C_FAILURE:
				return "GATT CONN L2C FAILURE";
			case GATT_CONN_TIMEOUT:
				return "GATT CONN TIMEOUT";
			case GATT_CONN_TERMINATE_PEER_USER:
				return "GATT CONN TERMINATE PEER USER";
			case GATT_CONN_TERMINATE_LOCAL_HOST:
				return "GATT CONN TERMINATE LOCAL HOST";
			case GATT_CONN_FAIL_ESTABLISH:
				return "GATT CONN FAIL ESTABLISH";
			case GATT_CONN_LMP_TIMEOUT:
				return "GATT CONN LMP TIMEOUT";
			case GATT_CONN_CANCEL:
				return "GATT CONN CANCEL ";
			case GATT_ERROR:
				return "GATT ERROR"; // Device not reachable
			default:
				return "UNKNOWN (" + error + ")";
		}
	}

	/**
	 * Converts the bluetooth communication status given by other BluetoothGattCallbacks to error name. It also parses the DFU errors.
	 * @param error the status number
	 * @return the error name as stated in the gatt_api.h file
	 */
	public static String parse(final int error) {
		switch (error) {
			case GATT_INVALID_HANDLE:
				return "GATT INVALID HANDLE";
			case GATT_READ_NOT_PERMIT:
				return "GATT READ NOT PERMIT";
			case GATT_WRITE_NOT_PERMIT:
				return "GATT WRITE NOT PERMIT";
			case GATT_INVALID_PDU:
				return "GATT INVALID PDU";
			case GATT_INSUF_AUTHENTICATION:
				return "GATT INSUF AUTHENTICATION";
			case GATT_REQ_NOT_SUPPORTED:
				return "GATT REQ NOT SUPPORTED";
			case GATT_INVALID_OFFSET:
				return "GATT INVALID OFFSET";
			case GATT_INSUF_AUTHORIZATION:
				return "GATT INSUF AUTHORIZATION";
			case GATT_PREPARE_Q_FULL:
				return "GATT PREPARE Q FULL";
			case GATT_NOT_FOUND:
				return "GATT NOT FOUND";
			case GATT_NOT_LONG:
				return "GATT NOT LONG";
			case GATT_INSUF_KEY_SIZE:
				return "GATT INSUF KEY SIZE";
			case GATT_INVALID_ATTR_LEN:
				return "GATT INVALID ATTR LEN";
			case GATT_ERR_UNLIKELY:
				return "GATT ERR UNLIKELY";
			case GATT_INSUF_ENCRYPTION:
				return "GATT INSUF ENCRYPTION";
			case GATT_UNSUPPORT_GRP_TYPE:
				return "GATT UNSUPPORT GRP TYPE";
			case GATT_INSUF_RESOURCE:
				return "GATT INSUF RESOURCE";
			case GATT_CONN_LMP_TIMEOUT:
				return "GATT CONN LMP TIMEOUT";
			case 0x003A:
				return "GATT CONTROLLER BUSY";
			case 0x003B:
				return "GATT UNACCEPT CONN INTERVAL";
			case GATT_ILLEGAL_PARAMETER:
				return "GATT ILLEGAL PARAMETER";
			case GATT_NO_RESOURCES:
				return "GATT NO RESOURCES";
			case GATT_INTERNAL_ERROR:
				return "GATT INTERNAL ERROR";
			case GATT_WRONG_STATE:
				return "GATT WRONG STATE";
			case GATT_DB_FULL:
				return "GATT DB FULL";
			case GATT_BUSY:
				return "GATT BUSY";
			case GATT_ERROR:
				return "GATT ERROR";
			case GATT_CMD_STARTED:
				return "GATT CMD STARTED";
			case GATT_PENDING:
				return "GATT PENDING";
			case GATT_AUTH_FAIL:
				return "GATT AUTH FAIL";
			case GATT_MORE:
				return "GATT MORE";
			case GATT_INVALID_CFG:
				return "GATT INVALID CFG";
			case GATT_SERVICE_STARTED:
				return "GATT SERVICE STARTED";
			case GATT_ENCRYPED_NO_MITM:
				return "GATT ENCRYPTED NO MITM";
			case GATT_NOT_ENCRYPTED:
				return "GATT NOT ENCRYPTED";
			case GATT_CONGESTED:
				return "GATT CONGESTED";
			case GATT_CCC_CFG_ERR:
				return "GATT CCCD CFG ERROR";
			case GATT_PRC_IN_PROGRESS:
				return "GATT PROCEDURE IN PROGRESS";
			case GATT_OUT_OF_RANGE:
				return "GATT VALUE OUT OF RANGE";
			case 0x0101:
				return "TOO MANY OPEN CONNECTIONS";
			default:
				return "UNKNOWN (" + error + ")";
		}
	}

	public static boolean isConnectionError(int error){
		switch (error) {
			case GATT_CONN_L2C_FAILURE:
			case GATT_CONN_TIMEOUT:
			case GATT_CONN_TERMINATE_PEER_USER:
			case GATT_CONN_TERMINATE_LOCAL_HOST:
			case GATT_CONN_FAIL_ESTABLISH:
			case GATT_CONN_LMP_TIMEOUT:
			case GATT_CONN_CANCEL:
			case GATT_ERROR:
			  return true;
			default:
				return false;

		}
	}
}

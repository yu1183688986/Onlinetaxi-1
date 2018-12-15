package com.minicreate.online_taxi.transmission.protocol;

import com.minicreate.online_taxi.utils.LogUtil;

import java.util.HashMap;

public class PushParamLib {
    private final String TAG = PushParamLib.class.getSimpleName();
    public static final String PARAM_PACKAGE = "com.minicreate.dc.vehicle.inch7.app.protocol.param.";
    private HashMap<Integer, String> paramLib = new HashMap<Integer, String>();
    private static PushParamLib instance = new PushParamLib();

    private PushParamLib() {
        initLib();
    }

    public static PushParamLib get() {
        return instance;
    }

    private void initLib() {
        //TODO 这里的内容搞明白了再导入
//        paramLib.put(ProtocolVh.PARAM_4G, Signal4G_01.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_GPS_CNT, GPSInfo_02.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_TEMPER, Temper_04.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_DRIVER_NAME, DriverName_06.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_BUS_DIST, BusStationDist_08.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_BUS_LINE, BusLine_09.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_RUN_UP_DOWN, TrafficDirectionInfo_0A.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_STATION_CURR, StationArrival_0B.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_SPEED, SpeedInfo_0C.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_RUN_CNT, RunCount_0D.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_TOMORROW_FIRST_RUN, RunTomorrowFirst_0E.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_NEXT_STAFF, StaffStationNext_0F.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_TIME_NEXT_STAFF, StaffTimeNext_10.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_STAFF_CHECK_TIME, StaffCheckTime_11.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_BIZ_STATUS, BizStatus_12.class.getSimpleName());
//
//        paramLib.put(ProtocolVh.PARAM_SMS_REPORT, MsgReport_15.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_TTS_SPEAK, TTSSpeak_19.class.getSimpleName());
////		paramLib.put(ProtocolVh.PARAM_DIAL, Dialing_16.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_LOCATION, Location_17.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_STATION_NEXT, StationNext_18.class.getSimpleName());
//
//        paramLib.put(ProtocolVh.PARAM_GPS_SNR, GPSSnr_41.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_NET_STATES, NetStatesAll_43.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_GPS_MODEL, GPSModel_32.class.getSimpleName());
//
//        paramLib.put(ProtocolVh.PARAM_CAMERA_SWITCH, CameraSwitch_76.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_IP_PHONE_STATE, IpPhoneState_7B.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_RECV_IP_PHONE, RecvIpPhone_7C.class.getSimpleName());
//
//        paramLib.put(ProtocolVh.PARAM_HOST_REQ_VERSION, ResendVersion_7C.class.getSimpleName());
//
//        paramLib.put(ProtocolVh.PARAM_DEPARTURE_TIME, DepartureTime_1A.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_RESET_BY_HOST, ResetByHost_80.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_NEXT_DEPARTURE_TIME, NextDepartureTime_81.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_BRIGHT_ADJUST, BrightnessAdjust_82.class.getSimpleName());
//
////		paramLib.put(ProtocolVh.PARAM_UPGRADE_APP, UpgradeApp_F1.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_FTP_UPGRADE_APP, UpgradeFtpApp_FA.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_FTP_UPGRADE_SYS, UpgradeFtpSys_FB.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_FTP_UPGRADE_IMG, UpgradeFtpImg_FC.class.getSimpleName());
//        paramLib.put(ProtocolVh.PARAM_FTP_UPGRADE_LOGO, UpgradeFtpLogo_FD.class.getSimpleName());
    }

    public String getClassString(int key) {
        if (paramLib.containsKey(key)) {
            return paramLib.get(key);
        } else {
            LogUtil.d("", "getClassString null");
            return null;
        }
    }

//	public synchronized String addT0ParamLib (Integer param, String className) {
//		if(paramLib.containsKey(param)) {
//			LogUtil.d(TAG, "addT0ParamLib 失败，已经存在！");
//			return null;
//		}
//		return paramLib.put(param, className);
//	}
//	
//	public synchronized String removeFromParamLib (Integer param) {
//		
//		return paramLib.remove(param);
//	}
//	
//	public synchronized void removeAllParamLib (Integer param) {
//		paramLib.clear();
//	}

}

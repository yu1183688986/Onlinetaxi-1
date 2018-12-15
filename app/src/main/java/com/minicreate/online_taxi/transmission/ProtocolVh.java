package com.minicreate.online_taxi.transmission;

public class ProtocolVh {
    /************************** 包的基本框架数据 ********************************/
    // 标识位
    public static final int SYNC_FLAG_HEADER = 0x7E;
    // 校验码

    // 版本号
    public static final int VERSION_0 = 0x00;
    public static final int VERSION_1 = 0x00;
    // 厂商编号
    public static final int CORP_NO_0 = 0x00;
    public static final int CORP_NO_1 = 0x01;
    // 外设类型编号 (peripheral)
    public static final int PERIP_TYPE_NO = 0x02;
    // 命令类型 0x45~0xff

    // 用户数据 n byte

    // 标识位 尾
    public static final int SYNC_FLAG_END = 0x7E;

    public static final int FRAME_ARCH_LEN = 0x09; // 帧架构基本长。
    public static final int BUS_NO = 0x06;         // 车辆编号长
    public static final int USER_DATA_LEN = 0x02;  // 用户数据区总长
    public static final int FRAME_BASE_LEN = FRAME_ARCH_LEN + BUS_NO + USER_DATA_LEN;

    public static final int PARAM_LEN = 0x02;

    public static final int INDEX_CHECK_CODE = 0x01; // 校验码的位置
    public static final int INDEX_USER_DATA = 0x0E;  // 用户数据的位置，
    public static final int INDEX_CMD = 0x07;  // 命令类型的位置
    public static final int INDEX_PARAM = 0x10;  // 参数类型的位置

    /************************** 命令类型 ********************************/
    public static final int CMD_PUSH = 0x45;

    public static final int CMD_REQ_REPORT = 0x45;
    public static final int CMD_READ = 0x46;
    public static final int CMD_WRITE = 0x47;
    public static final int CMD_UPGRADE = 0x48;
    public static final int CMD_HEARTBEAT = 0x49;

    /************************** 参数类型 ********************************/
    /**
     * 主界面显示：主要显示车辆的动态信息 0x45
     */
    public static final int PARAM_STOP_REPORT = 0xFF;   // 停止所有上报
    public static final int PARAM_4G = 0x01;    // 3G、4G信号强度
    public static final int PARAM_GPS_CNT = 0x02;    // GPS卫星数
    public static final int PARAM_DATE = 0x03;    // 时间、日期、星期几显示
    public static final int PARAM_TEMPER = 0x04;    // 温度信息

    public static final int PARAM_DRIVER_NAME = 0x06;    // 司机姓名信息

    public static final int PARAM_BUS_DIST = 0x08;    // 车辆站距显示
    public static final int PARAM_BUS_LINE = 0x09;    // 起始站、线路名、终点站显示
    public static final int PARAM_RUN_UP_DOWN = 0x0A;    // 上行下行显示
    public static final int PARAM_STATION_CURR = 0x0B;    // 本站站名显示
    public static final int PARAM_SPEED = 0x0C;    // 实时车速及限制速度显示
    public static final int PARAM_RUN_CNT = 0x0D;    // 已跑趟数
    public static final int PARAM_TOMORROW_FIRST_RUN = 0x0E;    // 明天首班
    public static final int PARAM_NEXT_STAFF = 0x0F;    // 下一个路签站
    public static final int PARAM_TIME_NEXT_STAFF = 0x10;    // 距离下一个路签站时间
    public static final int PARAM_STAFF_CHECK_TIME = 0x11;    // 路签考核时间
    public static final int PARAM_BIZ_STATUS = 0x12;    // 运营状态（比如包车等）
    public static final int PARAM_CHECK_BIZ_STATUS = 0x13;    // 运营状态确认

    public static final int PARAM_SMS_REPORT = 0x15;    // 短消息上报
    public static final int PARAM_DIAL = 0x16;    // 打电话
    public static final int PARAM_LOCATION = 0x17;    // 经纬度
    public static final int PARAM_STATION_NEXT = 0x18;    // 经纬度
    public static final int PARAM_CAMERA_SWITCH = 0x76;    // 倒车影像
    public static final int PARAM_TTS_SPEAK = 0x19;    // tts 语音播报
    public static final int PARAM_DEPARTURE_TIME = 0x1A;    // 发车倒计时
    /**
     * 设备参数查询  0x46
     */
    public static final int PARAM_SCHEDULE = 0x07;    // 排班信息显示
    public static final int PARAM_SMS_LIST = 0x14;    // 短消息列表

    public static final int PARAM_VEHICLE_NO = 0x20;    // 车辆编号 ，设置查询一样
    public static final int PARAM_POWER_MODE = 0x21;    // 关机模式 电源检测或是定时关机模式
    public static final int PARAM_NET1 = 0x22;    //
    public static final int PARAM_NET2 = 0x23;    //
    public static final int PARAM_STATION_REPROT_VOL = 0x24;    // 报站音量
    public static final int PARAM_GPS_SEND_PERIOD = 0x25;    // 发送周期
    public static final int PARAM_DEVICE_VER = 0x26;    // 主机软件版本号 ，设置查询一样
    public static final int PARAM_STATION_INFO = 0x2A;    // 站点信息
    public static final int PARAM_SET_UP_DOWN = 0x2B;    // 切换上行下行
    public static final int PARAM_LINE_SWITCH = 0x2C;    // 切换线路

    public static final int PARAM_NET_MODEL = 0x30;    // 网络模块
    public static final int PARAM_HDD_STATE = 0x31;    // 硬盘状态
    public static final int PARAM_GPS_MODEL = 0x32;    // GPS模块
    public static final int PARAM_BOARD_CHIP_STATE = 0x33;    // 底板芯片状态
    public static final int PARAM_VOLTAGE_STATE = 0x34;    // 电压状态
    public static final int PARAM_CAMERA_STATE = 0x35;    // 摄像头状态

    //    public static final int PARAM_GPS_CNT_Q               = 0x41;	// GPS 星数
    public static final int PARAM_GPS_SNR = 0x41;    // GPS SNR值
    public static final int PARAM_NET_SIGNAL = 0x42;    // NET 信号强度
    public static final int PARAM_NET_STATES = 0x43;    // 网络1-4状态
    public static final int PARAM_VIDEO_STATE = 0x44;    // 录像状态

    /**
     * 设备参数设置 0x47
     */
    public static final int PARAM_DRIVER_CARD = 0x05;    // 司机刷卡信息显示

    public static final int PARAM_SET_LOCATION = 0x27;    //
    public static final int PARAM_SET_PSW = 0x28;    // 密码验证

    public static final int PARAM_MAIN_CAMERA = 0x50;    // 主摄像头亮度
//    public static final int PARAM_MAIN_CAMERA_CONT        = 0x51;	// 主摄像头对比度
//    public static final int PARAM_MAIN_CAMERA_COLOR       = 0x52;	// 主摄像头色温
//    public static final int PARAM_MAIN_CAMERA_SATU        = 0x53;	// 主摄像头饱和度

    public static final int PARAM_FASTER_ONLINE = 0x60;    // 部标设备快捷上线 终端ID
//    public static final int PARAM_FASTER_ONLINE_COLOR     = 0x61;	// 部标设备快捷上线 车牌颜色
//    public static final int PARAM_FASTER_ONLINE_NO        = 0x62;	// 部标设备快捷上线 车牌号码
//    public static final int PARAM_FASTER_ONLINE_VIN       = 0x63;	// 部标设备快捷上线 车辆VIN
//    public static final int PARAM_FASTER_ONLINE_MOBILE    = 0x64;	// 部标设备快捷上线 手机号

    public static final int PARAM_ADM_ACCOUNT = 0x70;    // 密码修改 管理员账号
    public static final int PARAM_PSWD_MODIFY = 0x71;    // 管理员密码
    public static final int PARAM_DRIVER_NO = 0x72;    // 司机编号
    public static final int PARAM_BIZ_MANAGE = 0x73;    // 运营管理
    public static final int PARAM_TICKET_MANAGE = 0x74;    // 票务管理
    public static final int PARAM_VIDEO_SWITCH = 0x75;    // 视频切换
    public static final int PARAM_KEY_BROADCAST = 0x77;    // 按键播报
    public static final int PARAM_VERSION = 0x78;    // 屏APP、system版本号

    public static final int PARAM_APP_VIEW_ITEM = 0x79;    // 屏APP的显示的界面

    public static final int PARAM_DAIL_IP_PHONE = 0x7A;    // 拨打IP电话
    public static final int PARAM_IP_PHONE_STATE = 0x7B;    // IP电话实时状态
    public static final int PARAM_RECV_IP_PHONE = 0x7C;

    public static final int PARAM_HOST_REQ_VERSION = 0x7F;    // 主机要求再次下发版本号78协议，

    public static final int PARAM_RESET_BY_HOST = 0x80;
    public static final int PARAM_NEXT_DEPARTURE_TIME = 0x81;
    public static final int PARAM_BRIGHT_ADJUST = 0x82;
    public static final int PARAM_SOME_REQ = 0x83;

    public static final int PARAM_UPGRADE_APP = 0xF1;    // (app升级)
    public static final int PARAM_UPGRADE_SYS = 0xF2;    // (system升级)

    public static final int PARAM_FTP_UPGRADE_APP = 0xFA;    // (FTP方式升级app)
    public static final int PARAM_FTP_UPGRADE_SYS = 0xFB;    // (FTP方式升级SYS )
    public static final int PARAM_FTP_UPGRADE_IMG = 0xFC;    // (FTP方式升级司机头像 )
    public static final int PARAM_FTP_UPGRADE_LOGO = 0xFD;    // (FTP方式升级司机头像 )

    public static final int PARAM_BEATHEART = 0xF0;    // (心跳)
    public static final int PARAM_TIMEOUT = 0xEF;    // (超时)

    public static final String UPGRADE_PKG_APP_HEAD = "ScreenA"; // 升级包头
    public static final String UPGRADE_PKG_SYS_HEAD = "ScreenS";
    public static final String UPGRADE_PKG_APP_VER = "_appv";   // 升级版本
    public static final String UPGRADE_PKG_SYS_VER = "_sysv";
    public static final String UPGRADE_PKG_APP_SUFFIX = ".apk";    // 升级后缀
    public static final String UPGRADE_PKG_SYS_SUFFIX = ".zip";

    public static final String UPGRADE_PKG_APP_INSTALL = "ScreenA_install"; // 属于安装类型的升级包
    public static final String UPGRADE_PKG_APP_LAUNCH = "ScreenA_launch";   // 属于launch类型的升级包
}

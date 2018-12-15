package com.minicreate.online_taxi.common;

/**
 * Created by cjd on 15/06/18.
 */
public class Constants {

    public static class ServerConnState {
        public static final String CONNECTING = "Connecting";
        public static final String CONNECTED = "Connected";
        public static final String DISCONNECT = "Disconnected";
    }

    public static class ImageType {
        public static final String TYPE_USER_IMAGE = "0";
        public static final String TYPE_ROOM_IMAGE = "1";
        public static final String TYPE_DEVICE_IMAGE = "2";
    }

    public static final String ALL_ROOM = "ALL";
    public static final String UNKNOWN_ROOM = "UNKNOWN";
    public static final String SELF_ROOM = "SELF";
    public static final String ADD_ROOM = "ADD";

    /***********************************************开关控制*****************************************************/
    public static final String TYPE_NORMAL_DEVICE = "普通设备开关控制";
    public static final String TYPE_SPECIAL_DEVICE = "特殊设备开关控制";

    public static final String SPECIAL_DEVICE_ZN = "路由器";
    public static final String SPECIAL_DEVICE_EN = "Router";

    public static final String TYPE_SPORIT_STATE = "查询插座插口参数";
    public static final String TYPE_ALL_SPORIT_STATE = "查询所有插座插口参数";
    public static final int ALL_SPORIT = 0xFF;
    //添加定时
    public static final int REQUEST_CODE_TIME_PICKER = 3;

    public static final String TIMER = "timer";
    public static final String TIMER_REPEAT = "repeat";
    public static final String TIMER_START = "start";
    public static final String TIMER_END = "end";
    public static final String TIMER_ACTION = "action";
    public static final String TIMER_ID = "id";
    public static final String TIMER_USED = "used";
    public static final String TIMER_DEVICE_DB_ID = "device_db_id";
    public static final int ACTION_ON = 1;
    public static final int ACTION_OFF = 0;

    public static final int NO_ID = -0xFF;


    public static final String WIFI_DEVICE = "wifi_device";
    public static final String WIFI_DEVICE_1 = "wifi_device_1";
    public static final String WIFI_DEVICE_ID = "wifi_device_id";

    public static final String PHONE = "Phone";


    public static final String DEVICE_ICON_PATH = "device_icon_path";

    public static final int RESP_SUCCESS = 0;
    public static final int RESP_FAILURE = 1;
    public static final int RESP_RESULT = -1;

    public static final int CONTROL_SUCCESS = 0;
    public static final int CONTROL_SUCCESS_OPEN = 0;
    public static final int CONTROL_SUCCESS_CLOSE = 0;
    public static final int CONTROL_FAILURE_JACK = 1;
    public static final int CONTROL_UNKOWN = -1;

    public static final int CMD_FAILURE = -1;
    public static final int CMD_SUCCESS = 0;

    public static final int SEND_SUCCESS_LOCATION = 0;//地理信息发送成功
    public static final int SEND_FAILE_LOCATION = -1;//发送失败

    //控制时间间隔
    public static final long CLICK_GAP_TIME = 700;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;
    public static final int DISCONNECTED = 0;

    //短信注册
    public static final String TYPE_REGISTER_MSG = "send_password_by_mobile";
    public static final String TYPE_DEVICE_TYPE = "device_type";//设备类型
    public static final String TYPE_REGISTER_SIMPLE = "simple";

    public static final String ASSETS_PREFIX = "assets://icon//";


    public static final int DUPLICATE_BIND = 50320;

    public static final int ERROR_LOGIN = 50220;                        //账号或密码错误
    public static final int ERROR_REGISTER_REGISTERED = 50210;           //Mobile has been registered.
    public static final int ERROR_REGISTER_MOBILE = 50211;           //Unable to send message to the mobile.
    public static final int ERROR_REGISTER_LIMIT = 50212;           //Register count limit.
    public static final int ERROR_RESET_NOT_EXIST = 50250;           //Mobile has not been registered.
    public static final int ERROR_RESET_PASSWORD_LIMIT = 50251;           //Reset password count limit.

    public static final int EXTRA_CODE_OK = 0;           //
    public static final int EXTRA_CODE_TIMEOUT = 100;      //
    public static final int EXTRA_CODE_NO_DEVICE = 101;     //
    public static final int EXTRA_CODE_EXCEPTION = 102;     //

    public static final int RESP_TIMEOUT = -3;     //
    public static final int RESP_DATA_NULL = -2;     //
    public static final int RESP_LEN_0 = -1;     //

    /***********************************************定时重复时间(int->bit)*****************************************************/
    public static final String TYPE_NORMAl_TASK = "NORMAl_TASK";
    public static final String TYPE_SCENE_TASK = "SCENE_TASK";
    public static final String TYPE_LED_TASK = "LED_TASK";

    public static final int NEW_TIME_FLAG = 0;        //新建定时
    public static final int UPDATA_TIME_FLAG = 1;    //更新定时
    public static final int DELETE_TIME_FLAG = 2;    //删除定时
    public static final int NIGHT_TIME_FLAG = 3;    //夜间关灯
    public static final int QUERY_TIME_FLAG = 4;    //查询定时
    public static final int NIGHT_MODEL = 0xFF;

    public static final int NO_ID_VALUE = -0xFF;

    public static final int RESERVE = 1;
    public static final int MONDAY = 1 << 1;
    public static final int TUESDAY = 1 << 2;
    public static final int WEDNESDAY = 1 << 3;
    public static final int THURSDAY = 1 << 4;
    public static final int FRIDAY = 1 << 5;
    public static final int SATURDAY = 1 << 6;
    public static final int SUNDAY = 1 << 7;

    public static final int RESERVE16 = 0x01;
    public static final int MONDAY16 = 0x02;
    public static final int TUESDAY16 = 0x04;
    public static final int WEDNESDAY16 = 0x08;
    public static final int THURSDAY16 = 0x10;
    public static final int FRIDAY16 = 0x20;
    public static final int SATURDAY16 = 0x40;
    public static final int SUNDAY16 = 0x80;

    public static final int[] weekList16 = {RESERVE16, MONDAY16, TUESDAY16, WEDNESDAY16, THURSDAY16, FRIDAY16, SATURDAY16, SUNDAY16};
    public static final int[] weekList = {RESERVE, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
    public static final String[] weekText = {"当天", "一 ", "二 ", "三 ", "四 ", "五 ", "六 ", "日 "};


    /***********************************************按键锁、上电设置*****************************************************/
    // TODO 启动按键锁功能
    public static final int LOCK_TYPE_FLAG = 0;
    // TODO 启动上电状态功能
    public static final int POWER_TYPE_FLAG = 1;
    // TODO 启动按键锁和上电状态功能
    public static final int LOCK_AND_POWER_TYPE_FLAG = 2;
    // TODO 全部开启
    public static final int OPEN_ALL = 3;
    // TODO 全部关闭
    public static final int CLOSE_ALL = 4;
    // TODO 恢复上次设置状态
    public static final int REGAIN_LAST_ONE = 5;
    // TODO 无效字段
    public static final int NO_VALUE = -0xFF;
    //按键锁。
    //使用低四位表示4个插口的按键锁状态，每个Bit为一个插口的按键锁状态
    //1为打开按键锁，0为关闭按键锁，
    //比如0x01则打开1号按键的按键锁，0x0F打开所有按键锁


    // TODO 功能关闭
    public static final int DEFAULE_OFF = 0x00;
    // TODO 打开所有按键锁
    public static final int LOCK_DEFAULE_ALL_ON = 0x0F;


    public static final int LOCK_SPORIT_0 = 1;
    public static final int LOCK_SPORIT_1 = 1 << 1;
    public static final int LOCK_SPORIT_2 = 1 << 2;
    public static final int LOCK_SPORIT_3 = 1 << 3;

    //上电状态。
    //使用低四位表示4个插口的状态，每个Bit为一个插口的上电态，
    //1为上电打开，0为上电闭合，
    //比如0x01则表示上电后第一个插口未闭合的状态，0x0F为上电时所有状态都为闭合,为0xFF时则表示恢复到上次断电时的状态

    // TODO 上电时所有状态为闭合
    public static final int POWER_DEFAULE_ALL_ON = 0x0F;
    // TODO 恢复到上次断电时的状态
    public static final int POWER_REGAIN_LASTONE = 0xFF;

    public static final int POWER_SPORIT_0 = 1;
    public static final int POWER_SPORIT_1 = 1 << 1;
    public static final int POWER_SPORIT_2 = 1 << 2;
    public static final int POWER_SPORIT_3 = 1 << 3;

    public static final String PIC_TYPE_USER = "0";        //0 用户图片
    public static final String PIC_TYPE_ROOM = "1";        //1 房间图片
    public static final String PIC_TYPE_MODULE = "2";    //2 设备图片

    public static final int PIC_SHARP_DEFAULT = 0;    //图片形状 default
    public static final int PIC_SHARP_ROUND = 1;    // Round
    public static final int PIC_SHARP_SQUARE = 2;    // Square

    public static final int PIC_COLOR_DEFAULT = 0;    //图片default
    public static final int PIC_COLOR_GREY = 1;        // 图片变灰


    public static final int TEMP_DEFAULT = 0;
    public static final int RET_OK = 0;

    public static final String UPGRADE_TYPE_ANY = "0";        // 有新版本，不强迫升级
    public static final String UPGRADE_TYPE_FORCE = "1";    // 有新版本，必须升级

    public static final String COMPANY_TONGGU = "lingchen";

    public static final String KEY_LIGHT_ID = "ligth_id";

    public static final String KEY_SCENE_TYPE = "SCENE_TYPE";
    //    public enum SceneId {
//    	LIST_ALL_ON,
//    	LIST_ALL_OFF,
//    	LIST_SCENE_DUSK,
//    	LIST_SCENE_FINE,
//    	LIST_SCENE_SNOW,
//    	LIST_SCENE_RAIN,
//    	LIST_SCENE_NIGHT
//    }
    public static final int LIST_ALL_ON = 0;
    public static final int LIST_ALL_OFF = 1;
    public static final int LIST_SCENE_DUSK = 2;
    public static final int LIST_SCENE_FINE = 3;
    public static final int LIST_SCENE_SNOW = 4;
    public static final int LIST_SCENE_RAIN = 5;
    public static final int LIST_SCENE_NIGHT = 6;
    // 必须依照列表的顺序

    public static final int LIGHT_FLASH = 0;
    public static final int LIGHT_LED = 1;

    public static final String CHAR_SET = "gb2312";

    public static final int KEYCODE_ENTER = 66;
    public static final int KEYCODE_DOWN = 20;
    public static final int KEYCODE_UP = 19;
    public static final int KEYCODE_RIGHT = 22;
    public static final int KEYCODE_LEFT = 21;
    public static final int KEYCODE_BACK = 4;

    public static final int KEYCODE_NUM_0 = 7;
    public static final int KEYCODE_NUM_1 = 8;
    public static final int KEYCODE_NUM_9 = 16;
}

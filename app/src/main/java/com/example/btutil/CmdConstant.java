package com.example.btutil;


import android.app.SearchableInfo;

public class CmdConstant {
    //ARM ---> BT : AT#[CMD]\r\n
    public static String HEAD = "AT#";
    public static String END = "\r\n";
    public static String CONNECT = "CC";            // 1.连接最后一次连接设备,或者配对列表index
    public static String DISCONNECT = "CD";         // 2.断开当前设备所有连接
    public static String ANSWER_CALL = "CE";        // 3.接听来电
    public static String END_CALL = "CF";           // 4.拒绝电话
    public static String REFUSE_CALL = "CG";        // 5.CG:结束通话;
    public static String RE_DIAL = "CH";            // 6.重拨
    public static String ON_OFF_MIC = "CM";         // 7.CM:打开关闭 MIC(部分版本不支持);
    public static String DELETE_MATCH_RECORD = "CV";// 8.CV:删除配对记录
    public static String DIAL = "CW";               // 9.CW[NUMBER]:拨号;
    public static String EXT_NUM = "CX";            // 10.CX[NUMBER:1]:分机号;
    public static String INQUIRE = "CY";            // 11.CY:查询 HF 状态;
    public static String BT_RESET = "CZ";           // 12.CZ:蓝牙复位;
    public static String VOICE_DIAL = "CI";         // 13.CI:语音拨号(部分版本暂不支持);
    public static String VOICE_DIAL_CANCEL = "CJ";  // 14.CJ:取消语音拨号;
    public static String VOICE_ADD = "CK";          // 15.CK;音量+(部分版本暂不支持);
    public static String VOICE_SUB = "CL";          // 16.CL:音量- (部分版本暂不支持);
    public static String VOICE_SET = "VS";          // 17.VS[VALUE];设置音量:”1”<=VALUE<=”16”; 查询音量:value=”0”;
    public static String VOICE_SWITCH_PHONE_OR_BT = "CO";  // 18.CO:通话声音互切;
    public static String VOICE_SWITCH_BT = "CP";// 19.CP:声音切到蓝牙;
    public static String VOICE_SWITCH_PHONE = "CN"; // 20.CN:声音切到手机;
    public static String PLAY_OR_PAUSE_MUSIC = "MA";   // 21.MA:播放、暂停音乐;
    public static String VOICE_FORCE_STOP = "MB";    // 22.MB:声音强制暂停;
    public static String STOP_MUSIC = "MC";          // 23.MC:停止音乐;
    public static String NEXT_MUSIC = "MD";          // 24.MD:下一曲;
    public static String PRE_MUSIC = "ME";           // 25.ME:上一曲;

//设置
    public static String QUERY_STATUS = "MF";       // 26.MF:查询自动连接和自动接听状态;
    public static String QUERY_MUSIC = "MK";        // 27.MK:查询歌曲信息(客户须提出此功能要求);
    public static String SET_AUTO_CONNECT = "MG";   // 28.MG:设置自动连接;
    public static String CANCEL_AUTO_CONNECT = "MH";// 29.MH:取消自动连接;
    public static String QUERY_BT_NAME = "MM";      // 30.MM[BTNAME:0/N]:查询/设置蓝牙名称;
    public static String SET_MATCH_PASSWD = "MN";   // 31.MN[PIN]:设置配对密码;
    public static String QUERY_VERSION = "MY";      // 32.MY:查询版本;
    public static String SET_AUTO_ANSWER = "MP";    // 33.MP:设置自动接听;
    public static String CANCEL_AUTO_ANSWER = "MQ"; // 34.MQ:取消自动接听;
    public static String QUERY_A2DP_STATUE = "MV";  // 35.MV:查询 A2DP 状态;
    public static String QUERY_MACTH_LIST = "MX";   // 36.MX:查询配对列表
    public static String READ_TELE_BOOK = "PA";     // 37.PA:读取电话本;
    public static String CALL_RECORDS = "PH";       // 38.PH:拨打通话记录;
    public static String RECEIVED_CALLS = "PI";     // 39.PI:已接通话记录;
    public static String MISS_CALLS = "PJ";         // 40.PJ::未接通话记录
    public static String PAGE_DOWN = "PQ";          // 41.PQ:下翻;
    public static String PAGE_UP = "PP";            // 42.PP:上翻;
    public static String SEARCH = "SD";             // 43.SD:搜索;
    public static String SEARCH_STOP = "ST";        // 44.ST:停止搜索;

//一键通部分
    public static String SET_ONEKEY_DAIL = "DT";    // 45.DT[number]:设置一键通号码，只能设置一个里面有保存部分默认号码
    public static String SER_ENLARGE = "DR";        // 46.DR[number]:设置放大倍数，10 进制字符串;

//HID 部分(客户须提出此功能要求)
    public static String CONNECT_LAST_DEVICE = "HE";// 47.HE:连接最后一个设备的 hid;
    public static String DISCONNECT_LAST_DEVICE = "HD";   // 48.HD:断开最后一个设备的 hid;
    public static String KEY = "HK";                // 49.HK[key]:HOME,MENU,BACK,A.....Z;
    public static String SEND_MOUSE_COORD = "HM";   // 50.HM[x:4][y:4]:发送鼠标坐标点;
    public static String MOUSE_KEY = "HL";          // 51.HL:鼠标按键
    public static String MOUSE_DOWN = "HO";         // 52.HO[x:4][y:4]:鼠标按下
    public static String MOUSE_UP = "HP";           // 53.HP[x:4][y:4]:鼠标弹起


    //BT --->ARM: [CMD]\r\n
    public static String BT_CONNECT = "IB";      //IB:蓝牙连接;
    public static String BT_DISCONNECT = "IA";   //IA:蓝牙断开;
    public static String CALL_IN = "ID";         //ID[number]:来电; IP[numberlen:2]\r\nID[number]\r\n:来电回复 IG:通话中
    public static String HANG_UP = "IF";         //IF:电话挂断;
    public static String CURRENT_NUM = "IR";     //IR[NUMBER]:当前通话号码
    public static String MIC_OPEN = "MJ";        //MJ:MIC 开
    public static String MIC_CLOSE = "MK";       //MK:MIC 关
    public static String CALL_OUT = "IC";        //IC[number]:去电
    public static String HF_STATUS = "MG";//MG[STATUS:1]:1;未连接 2;连接中 3;已连接 4 去电 5 来电 6通话中
    public static String START_SURCESS = "IS";   //IS;启动成功
    public static String VOICE_VALUE = "VS";     //VS[VALUE]
    public static String VOICE_BT = "MC";        //MC:声音在蓝牙端
    public static String VOICE_PHONE = "MD";     //MD:声音在手机端
    public static String MUSIC_PLAYING = "MB";   //音乐播放中
    public static String MUSIC_PAUSE = "MA";     //音乐暂停
    public static String STATUS_AUTO_CONNECT = "MF";     //自动接听状态:MF[AUTO_CONNECT:1][AUTO_ANSWER:1]
    public static String MUSIC_INFO = "MI";      // MI[name]\xff[author]\xff[timer]\xff[index]\xff[count]
    public static String BT_NAME = "MM";         //查询/设置蓝牙名称 MM[NAME]
    public static String PASSWD = "MN";          //MN[CODE]
    public static String VERSION = "MW";         //MW[version]
    public static String A2DP_STAUTS = "MU";     //MU[STATUS:1]: 1:未连接 2:连接中 3:已连接 4:播放中
    public static String MACTCH_LIST = "MX";     //MX[index:1][addr][name]
    public static String READ_TELE_BOOK_DATA = "PB";      //读取电话本的数据PB[name]\FF[number]\r\n;
    public static String READ_TELE_BOOK_END = "PC";       //PC:结束下载
    public static String CALL_RECORDS_DATA = "PD";        //PD[index:2][number]\r\n;
    public static String RECEIVED_CALLS_DATA = "PD";      //PD[index:2][number]\r\n;
    public static String MISS_CALLS_DATA = "PD";          //PD[index:2][number]\r\n;
    public static String DOWNLOAD_END = "PD";               //PD:结束下载

    public static String SEARCH_RESULT = "IX";    //IX[addr:12][name]
    public static String SEARCH_OVER = "IY";      //IY:搜索结束
    public static String PARSE_NUM = "DT";        //DT[number]:当前解析出来的号码;
}

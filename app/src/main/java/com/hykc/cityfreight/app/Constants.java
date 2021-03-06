package com.hykc.cityfreight.app;

import android.os.Environment;

/**
 * Created by Administrator on 2018/3/21.
 */

public class Constants {
    public static final String WEBSERVICE_URL = "http://tyhykc.com/";//http://tyhykc.com/
    //public static final String WEBSERVICE_URL="http://ewytek.cn/";
    public static final String MQTT_URL = "tcp://122.114.76.7:1883";//tcp://122.114.76.7:1883
    public static final String ETC_URL = "http://39.105.210.202:63688/";
    public static final String OIL_URL_TEST="http://122.114.76.7:63688/";//
    public static final String BESTSIGN_URL_TEST="http://122.114.76.7:63688/";//http://122.114.76.7:63688
    public static final String MQTT_PWD = "admin";
    public static final String AppId = "driver";
    public static final int TIMER_DELAY = 10000;
    public static final String UPDATEAPP_LOCATION = Environment.getExternalStorageDirectory() +
            "/cityfreight/AllenVersionPath/";
    public static final String SFZ_ZMZ = "zmz";
    public static final String JSZ = "jsz";
    public static final String XCZ = "xcz";
    public static final String CLZ = "clz";
    public static final String PAYTYPE_ZFB = "zfb";
    public static final String PAYTYPE_WX = "wx";
    public static final String PAYTYPE_YHK = "yhk";
    public static final String wxAPP_ID = "wx9e92b64666bcd9cb";
    public static final String ZFB_NOTIFY_URL = "http://ewytek.cn/zfb/cp_zfb_notifyurl.jsp";
    public static final String ZFB_APP_ID = "2018050302626171";
    public static final String PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC0+xu6MQD2VIOWNTO2zESyTyYfb+iZzZagovLcQ3MfaA88+U8ZxiA8DlD2s9wTvHo2z9YVnIxSGKaPX73QPv8rRq1c24UMN8IGD8X5zmjyFf1KSm5fQpWiBvDRKZk7OKO4UJRsIdMsKU1y3lGw5Y1M3ghel4FdI/FJH0BooUGKtKg6bJ5hO0UIeROrfm+h/dAAhUQad4lyG3kIxm65SImbojZe2lXrnSe2415J2uVfezS+NV6SFZnVFqkedLMvoz+z36pdJjZtJ34HwCsmBHENHtwszyQUzJVXBbjQ3z05i1xFDrX7TSpwBwZ+K6k+dnpLTYK/3NvvxSKWnyJxTfnrAgMBAAECggEAcTl8UlMgH/wlys6ECQPooolj910C714ws1TejbDhEDABAIuU0jIiro/iVUWXFP3gk/QZIrIrE516bs5eKS+QYlm4UhTAOzGTAEnT+QIKpBHfwh+ox44XoSOoe6MDy4oW21QGV1QeEk3Qt2GhSAOFECcEo3EzrLq01KNpn0vrfkOSPW49u2UgiosLwO1xubF5O2R9V6njBM1CgyPm6T+UluFsi/tf695C4Z0gwCLT7IVAKFzmexSj6CrI1L/nBaGFt2gM2ETe8igkju1pPk1GiQLvxPE1jjorLlr4ytzSRBnq3YR/xluJRGioaPWoqaZ+DZjV58xejpO5qHafoDsRIQKBgQD04xhZ4ekV6Cszq54rBZbUg+MkvSkHIc8CIVOEjrJ5bzRwZa8JgupkiKFK6lIyVOYPTrAENl079dfQYMG9Ke4LOENaQXx7BM/LMB/iJzSkcqYbX7+Mi/hi8SoXTD1uNs088NyhaohSQvZLXUKVdqidzI2gnxHF3vCJK4SkMCFPUQKBgQC9MZn3LVEF7P1I+dGA8x7XsThK6E25bEZzJJKWxIQ7ZwOKHKi60mqUemzxZ9DaDm17UIhmoJtSIo+w099OOXPf3jUFX+cZvO0VBMGRywQ902Y15z9Q6KDjsTAVPaSeFc2+lEqsMiSYPYluFt0kPkpzcYamY+MNwXIXFcyzpVN+ewKBgQCjIi10owPtLg6piygArOZ/h6RnMwQD1kfauWyzn4PRVX1vaq2h/m46rrIT/+jl0py3kcm75KNTcEBmdi5Z33bOTpq/o2RTZy8twk9bDpcI/b8d+TjXXd8HunQ5tPYvV559fE7d0OQjOKxuJCBZBQTqqfunBpCGai9+kVj5L37hIQKBgQCczD4JUAJlgdsNHxZV46h9JIA9/Ldl5Gmixf3DfE0HGkIhtczVQMxaD/i4Pc0m22w7FixludfgQ4qJB597qjvxnqycrrXOa/ilQdLXbMUwJEMCi4u7F4E4E8KA6BzFc9aXizkz6z7O+bkrh0Fx5zo24TCyN/iWjutqXIPRo0RGfwKBgQCmmCwofUe0u++mrOYxPE46C6ct3uEkI7CjgGVNt9INc+O9v7sBtDjUxXTbp88acOAFGkpr9/lctsBUg2I0eERt5XIWRYj7syuClFn6OEwnBW4p2hqSUMvKTyc9QNIfj1aGHmEkrIKrS5Ly0t4SQIKMOJOu6h8T5A70qb0SnADmVw==";
    public static final String UPLOADE_URL = WEBSERVICE_URL + "files/image_upload_new.jsp";
    public static final int LOC_RADIUS = 3000;
    public static final String FACE_LICENSE = "ZUAShdDeu4pVwM1jswD8t2j6rGe9UFBvFq6aIV5FKb27P5lnaEwF72aQ8L6EEQKjFbZeL2uFJ6ZmleYCxxePqpHYz5DBAEx30YX88Rc62luhssIByZiQ5XpTBM3A+HTKq3aMyb+Q8fKyKir6tZtXk5i0GR6svXsNQ2dDEFD0BaJYmI5f+ZUGMhyk5r37ERrUq1Z9G75CFfhaZgmJK/mpHaSUToo95DeR+neiXBr9UVWoz9sSl2otMahWc3PeuhbpDuXWoF8ybTPjK8+lCPecm/aQF6BhA9kI0x/2d+E2LBR7L/KvVEbKRbL6xyME+ZOSMQDxwRL2OBTkweGBW9Pc/A==";
    public static final String YJJY_NUM="13223016072";
    //测试
    //public static final String FACE_APPID="TIDA4BmI";

    //正式
    public static final String FACE_APPID = "IDAN33rz";
    //总公司测试
/*    public static final String ENTERPRISECODE="E0000074";
    public static final String APPIDENTITY="660f712cdb0b11e79148246e965b4750";
    public static final String APPKEY="8a82286bdb0b11e78c190242ac120002";
    public static final String ALCT_URL="https://oapi-staging.alct56.com";*/

    //总公司正式
    public static String ALCT_URL = "https://oapi.alct56.com";
    public static String ENTERPRISECODE = "E0018821";
    public static String APPIDENTITY = "85d0100a001211e8a48d6c0b84d5a88f";
    public static String APPKEY = "812345cb001311e8a48d6c0b84d5a88f";

    //环信客服云
    public static String ServiceIMNumber="kefuchannelimid_638234";
    public static String IMAPPKEY="1487191011061303#kefuchannelapp75108";
    public static String IMTENANTID="75108";

    //省平台参数
    public static final String LOCATION_API_APPID="com.hykc.cityfreight";//网络货运企业APP的唯一标识
    public static final String LOCATION_APPSECURITY="0a1b1cf928da45719d48446ec85ff5b67316e27992114a6e9bcbd466bab372e7";//网络货运企业在省平台申请的接入密钥
    public static final String LOCATION_API_ENTERPRISESENDERCODE="41110042";//网络货运企业在省平台申请的唯一标识代码
    public static final String LOCATION_API_ENVIRONMENT="release";//环境:“debug”接入测试环境，“release”接入正式环境。
}

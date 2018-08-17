//
// Created by root on 17-7-13.
//
#include <jni.h>

extern "C" {

jbyteArray
Java_com_sunruncn_lib_mqtt_MqttData_GetHead(
        JNIEnv *env,
        jobject object,
        jbyte devType, jbyteArray userId, jbyteArray serial, jint length, jbyteArray time) {
    jbyte *head = new jbyte[24];
    head[0] = (jbyte) length;
    head[1] = 0x01;
    head[2] = devType;
    head[3] = (jbyte) 1 % 100;
    head[4] = (jbyte) 1 >> 2 % 100;
    //用户ID
    jint userLen = env->GetArrayLength(userId);
    jbyte *userIds = env->GetByteArrayElements(userId, false);
    for (int i = 0, j = userLen - 1; i < userLen; i++, j--) {
        head[5 + i] = userIds[j];
    }
    //设备序列号
    jint serialLen = env->GetArrayLength(serial);
    jbyte *serials = env->GetByteArrayElements(serial, false);
    for (int i = 0, j = serialLen - 1; i < serialLen; i++, j--) {
        head[i + 9] = serials[j];
    }
    //年
    jint timeLen = env->GetArrayLength(time);
    jbyte *times = env->GetByteArrayElements(time, false);
    for (int i = 0; i < timeLen; ++i) {
        head[15 + i] = times[i];
    }
    head[23] = 0x01;
    head[23] = 0x01;
    jbyteArray headArray = env->NewByteArray(24);
    env->SetByteArrayRegion(headArray, 0, 24, head);
    return headArray;
}
}

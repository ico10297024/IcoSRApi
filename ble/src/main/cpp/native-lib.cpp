//
// Created by root on 17-7-13.
//
#include <jni.h>

extern "C" {

jbyteArray
Java_com_sunruncn_ble_Protocal_getQueryI(
        JNIEnv *env,
        jobject object) {
    //校验
    jbyte *bytes = new jbyte[8];
    bytes[0] = 0xf5;
    bytes[1] = 0xf5;
    //设备类型
    bytes[2] = 0x01;
    //命令体
    bytes[3] = 0x04;
    //校验  bytes[0]|bytes[1]|bytes[2]|bytes[3]
    bytes[4] = 0x00;
    bytes[5] = 0x00;
    //结束
    bytes[6] = 0x0d;
    bytes[7] = 0x0a;
    jbyteArray array = env->NewByteArray(8);
    env->SetByteArrayRegion(array, 0, 8, bytes);
    return array;
}

jbyteArray
Java_com_sunruncn_ble_Protocal_getOpenI(
        JNIEnv *env,
        jobject object) {
    //校验
    jbyte *bytes = new jbyte[8];
    bytes[0] = 0xf5;
    bytes[1] = 0xf5;
    //设备类型
    bytes[2] = 0x01;
    //命令体
    bytes[3] = 0x01;
    //校验  bytes[0]|bytes[1]|bytes[2]|bytes[3]
    bytes[4] = 0x00;
    bytes[5] = 0x00;
    //结束
    bytes[6] = 0x0d;
    bytes[7] = 0x0a;
    jbyteArray array = env->NewByteArray(8);
    env->SetByteArrayRegion(array, 0, 8, bytes);
    return array;
}


jbyteArray
Java_com_sunruncn_ble_Protocal_getResetI(
        JNIEnv *env,
        jobject object) {
    //校验
    jbyte *bytes = new jbyte[8];
    bytes[0] = 0xf5;
    bytes[1] = 0xf5;
    //设备类型
    bytes[2] = 0x01;
    //命令体
    bytes[3] = 0x02;
    //校验  bytes[0]|bytes[1]|bytes[2]|bytes[3]
    bytes[4] = 0x00;
    bytes[5] = 0x00;
    //结束
    bytes[6] = 0x0d;
    bytes[7] = 0x0a;
    jbyteArray array = env->NewByteArray(8);
    env->SetByteArrayRegion(array, 0, 8, bytes);
    return array;
}


jbyteArray
Java_com_sunruncn_ble_Protocal_getConfigI(
        JNIEnv *env,
        jobject object) {
    //校验
    jbyte *bytes = new jbyte[8];
    bytes[0] = 0xf5;
    bytes[1] = 0xf5;
    //设备类型
    bytes[2] = 0x01;
    //命令体
    bytes[3] = 0x03;
    //校验  bytes[0]|bytes[1]|bytes[2]|bytes[3]
    bytes[4] = 0x00;
    bytes[5] = 0x00;
    //结束
    bytes[6] = 0x0d;
    bytes[7] = 0x0a;
    jbyteArray array = env->NewByteArray(8);
    env->SetByteArrayRegion(array, 0, 8, bytes);
    return array;
}

jbyte
Java_com_sunruncn_ble_Protocal_analyze(
        JNIEnv *env,
        jobject object,
        jbyteArray buffer) {
    if (env->GetArrayLength(buffer) < 8) {
        return -1;
    }
    jbyte *bytes = new jbyte[env->GetArrayLength(buffer)];
    env->GetByteArrayRegion(buffer, 0, env->GetArrayLength(buffer), bytes);
    if ((bytes[0] & 0xff) == 0xf5
        && (bytes[1] & 0xff) == 0xf5
        && (bytes[6] & 0xff) == 0x0d
        && (bytes[7] & 0xff) == 0x0a
            ) {
        return bytes[3];
    } else {
        return -1;
    }
}

jstring
Java_com_sunruncn_ble_Protocal_getWriteUUID(
        JNIEnv *env,
        jobject object) {
    jstring uuid = env->NewStringUTF("0000ff02-0000-1000-8000-00805f9b34fb");
    return uuid;
}
jstring
Java_com_sunruncn_ble_Protocal_getReadUUID(
        JNIEnv *env,
        jobject object) {
    jstring uuid = env->NewStringUTF("0000ff01-0000-1000-8000-00805f9b34fb");
    return uuid;
}

}

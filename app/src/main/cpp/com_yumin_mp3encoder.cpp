//
// Created by tangtang on 18/6/11.
//

//TODO 当java层内存回收时候自动释放对应的native Mp3Encoder

#include <jni.h>
#include <android/log.h>
#include "Mp3Encoder.h"

#define LOG_TAG  "C_TAG"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)


jfieldID FIELD_ID_Mp3Encoder_nativeObj;


inline Mp3Encoder* getNativeMp3Encoder(JNIEnv* env,jobject owner) {
    jlong nativePtr = env->GetLongField(owner,FIELD_ID_Mp3Encoder_nativeObj);
    return reinterpret_cast<Mp3Encoder *>(nativePtr);
}

extern "C" JNIEXPORT void JNICALL
        Java_com_yumin_mp3encoder_Mp3Encoder_encod(JNIEnv* env,
                                                   jobject jobj){
    Mp3Encoder*  mp3Encoder = getNativeMp3Encoder(env,jobj);
    mp3Encoder->encod();
}


extern "C" JNIEXPORT long JNICALL
Java_com_yumin_mp3encoder_Mp3Encoder_nativeInit(JNIEnv* env,
                                                 jobject /* this */){
    LOGD("Mp3Encoder init");
    Mp3Encoder* nativeMp3Encoder = new Mp3Encoder;
    return reinterpret_cast<jlong >(nativeMp3Encoder);
}

extern "C" JNIEXPORT void JNICALL
Java_com_yumin_mp3encoder_Mp3Encoder_nativeDestory(JNIEnv* env,
                                                    jobject jobj){
    Mp3Encoder*  mp3Encoder = getNativeMp3Encoder(env,jobj);
    mp3Encoder->destory();
}

/**
 * 注意获取字符串的时候有可能oom，但是这里不做检查，就让它崩溃吧
 */
extern "C" JNIEXPORT void JNICALL
Java_com_yumin_mp3encoder_Mp3Encoder_encodeInit(JNIEnv* env,
                                                    jobject jobj,jstring jpcmFilePath,
                                                    jstring joutMp3Path,jint sampleRate,
                                                    jint channels,jint ratebit){
    Mp3Encoder*  mp3Encoder = getNativeMp3Encoder(env,jobj);
    const char *pcmFilePath =env->GetStringUTFChars(jpcmFilePath,NULL);
    const char *outMp3Path = env->GetStringUTFChars(joutMp3Path,NULL);
    mp3Encoder->init(pcmFilePath,outMp3Path,sampleRate,channels,ratebit);
    env->ReleaseStringUTFChars(jpcmFilePath,pcmFilePath);
    env->ReleaseStringUTFChars(joutMp3Path,outMp3Path);

}

extern "C" JNIEXPORT void JNICALL
Java_com_yumin_mp3encoder_Mp3Encoder_initIds(JNIEnv* env, jclass cls){
    FIELD_ID_Mp3Encoder_nativeObj = env->GetFieldID(cls,"nativeObj","J");
}







//
// Created by wishy.gupta on 23-04-2018.
//

#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_viewlift_presenters_AppCMSPresenter_getNativeKey1(JNIEnv *env, jobject instance) {

 return (*env)->  NewStringUTF(env, "TmF0aXZlNWVjcmV0UEBzc3cwcmQx");
}

JNIEXPORT jstring JNICALL
Java_com_viewlift_presenters_AppCMSPresenter_getNativeKey2(JNIEnv *env, jobject instance) {

 return (*env)->NewStringUTF(env, "TmF0aXZlNWVjcmV0UEBzc3cwcmQy");
}
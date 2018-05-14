//
// Created by wishy.gupta on 23-04-2018.
//

#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_viewlift_presenters_AppCMSPresenter_getStoreId(JNIEnv *env, jobject instance) {

 return (*env)->  NewStringUTF(env, "dGVzdF9zdmYwMDE=");
}

JNIEXPORT jstring JNICALL
Java_com_viewlift_presenters_AppCMSPresenter_getStorePwd(JNIEnv *env, jobject instance) {

 return (*env)->NewStringUTF(env, "dGVzdF9zdmYwMDFAc3Ns");
}
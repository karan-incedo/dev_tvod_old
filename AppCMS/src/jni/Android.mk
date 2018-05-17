LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := SSLKeys
LOCAL_SRC_FILES := SSLKeys.c

include $(BUILD_SHARED_LIBRARY)
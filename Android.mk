LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES += zltd_common

LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_DEX_PREOPT := false
LOCAL_PACKAGE_NAME := AllInOneScanTest
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)

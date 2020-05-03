//
// Created by root1 on 5/3/20.
//

#ifndef RACINGLUMBER_NATIVE_LIB_H
#define RACINGLUMBER_NATIVE_LIB_H

#include "../../../../../../Android/Sdk/ndk/21.0.6113669/toolchains/llvm/prebuilt/linux-x86_64/sysroot/usr/include/jni.h"

JNIEXPORT void JNICALL Java_com_example_racinglumber_dataStorage_stringFromJNI(JNIEnv *, jobject);
JNIEXPORT jlong JNICALL Java_com_example_racinglumber_dataStorage_sumIntegers(JNIEnv*, jobject, jint, jint);

#endif //RACINGLUMBER_NATIVE_LIB_H

#include <jni.h>
#include <string>
#include <iostream>

JNIEXPORT void JNICALL Java_com_example_racinglumber_dataStorage_stringFromJNI
        (JNIEnv* env, jobject thisObject) {
    std::cout << "Hello from C++ !!" << std::endl;
}

JNIEXPORT jlong JNICALL Java_com_example_racinglumber_dataStorage_sumIntegers
        (JNIEnv* env, jobject thisObject, jint first, jint second) {
    std::cout << "C++: The numbers received are : " << first << " and " << second << std::endl;
    return (long)first + (long)second;
}



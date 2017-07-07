#include <jni.h>
#include <string>

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_ayw_template_ui_MainActivity_getTest(JNIEnv *env, jclass type) {
    std::string appId = "hello jni";
    return env->NewStringUTF(appId.c_str());}
}
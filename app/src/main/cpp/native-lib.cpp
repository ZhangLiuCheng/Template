#include <jni.h>
#include <string>

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_zlc_template_ui_MainActivity_getTest(JNIEnv *env, jobject instance) {
    std::string appId = "hello jni";
    return env->NewStringUTF(appId.c_str());
}

}
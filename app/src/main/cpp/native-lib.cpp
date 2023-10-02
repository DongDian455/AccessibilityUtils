#include <jni.h>
#include <string>
#include<android/log.h>

#define LOG_TAG "jniLog"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
//
//extern "C"
//JNIEXPORT jboolean JNICALL
//Java_com_returntolife_accessibilityutils_AuthUtils_checkAuthByNative(JNIEnv *env, jobject thiz,
//                                                                     jstring s) {
//
//    jclass jclazz = env->FindClass("com/returntolife/accessibilityutils/RSAUtils");
//
//
//    jmethodID jmethodId = env->GetStaticMethodID(jclazz, "decryptByPublicKey",
//                                           "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
//
//    jstring pub_key = env->NewStringUTF("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3Q9t/salI5qVD64h5lxJ170ob\n"
//                                        "5nPx8UsXcDbaQD81A7cukJClmvi6TUb/V+YjBKggOcXlELtJ0bipkARlPz6QduZO\n"
//                                        "eT3fTiFOkMow5j372OFdFcWq+SNW0STMKttkHD8KF7nbIL2KlWu0oyMD5gIpaFMA\n"
//                                        "fc/mKJWmf6+FkX9tAwIDAQAB");
//
//
//    auto key = static_cast<jstring>(env->CallStaticObjectMethod(jclazz, jmethodId, pub_key, s));
//
//    LOGD("hjj_test size=%s", key);
//
//
//    return false;
//}


char *jstringToChar(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}


std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

long getCurrentTime()
{
    struct timeval tv;
    gettimeofday(&tv,NULL);
    return tv.tv_sec;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_returntolife_accessibilityutils_AuthUtils_checkAuthByNative(JNIEnv *env, jobject thiz,
                                                                     jstring s) {


    jclass jclazz = env->FindClass("com/returntolife/accessibilityutils/RSAUtils");


    jmethodID jmethodId = env->GetStaticMethodID(jclazz, "decryptByPublicKey",
                                           "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");

    jstring pub_key = env->NewStringUTF( "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCivB/3gMUJTv3LxKZORMhSX586pNgClEYn8390xlnoiyyWAYbGafda/N8pbl2hi1T85IOA9vKRT4dRy7OY5est/2B+gvvd2oWF2BOzPfdIvDWcJ3KPVd2TeYlgeTmvldhbfokasYUHqOhnVGcXcgG+4u+1IuTn2Huw9sDqSlTmawIDAQAB");



    auto key = static_cast<jstring>(env->CallStaticObjectMethod(jclazz, jmethodId, pub_key, s));

    std::string info = jstring2string(env,key);

//   LOGD("currentTime=%s",info.data());
    try{
        size_t pos = info.find('-');


        std::string temp = info.substr(0, pos);
        long  time = std::stol(temp);

        long currentTime = getCurrentTime();

//    LOGD("currentTime=%ld time=%ld",currentTime,time);
        auto diff = labs(currentTime-time);

        LOGD("diff=%ld",diff);

        return diff < 60 * 60 * 12;
    }catch (std::exception &e){

    }

    return false;
}



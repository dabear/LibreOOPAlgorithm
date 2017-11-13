#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_hg4_oopalgorithm_oopalgorithm_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

//private native boolean getNeedsReaderInfoForActivation(int i);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getNeedsReaderInfoForActivation(
        JNIEnv *env, jobject thisObj,
        jint i) {

}

//private native MemoryRegion getNextRegionToRead(int i, byte[] bArr, int i2);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getNextRegionToRead(
        JNIEnv *env, jobject thisObj,
        jint i,
        jbyteArray bArr,
        jint i2) {

}

//private native int getProductFamily(int i);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getProductFamily(
        JNIEnv *env, jobject thisObj,
        jint i) {
}

//private native int getTotalMemorySize(int i);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getTotalMemorySize(
        JNIEnv *env, jobject thisObj,
        jint i) {
}



//private native int getUnlockCode(int i);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getUnlockCode(
        JNIEnv *env, jobject thisObj,
        jint i) {
}

extern "C"
JNIEXPORT jboolean  JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_isPatchSupported(
        JNIEnv *env, jobject thisObj,
        jint i,
        jbyteArray bArr,
        jobject applicationRegion){

    return 0;

}


//private native DataProcessingResult processScan(int i, AlarmConfiguration alarmConfiguration, NonActionableConfiguration nonActionableConfiguration, byte[] bArr, int i2,
// int i3, int i4, byte[] bArr2, Out<Integer> out, Out<Integer> out2, Out<Boolean> out3, Out<Boolean> out4, Out<byte[]> out5, Out<AlgorithmResults> out6);
extern "C"
JNIEXPORT jobject JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_processScan(
        JNIEnv *env, jobject thisObj,
        jint i,
        jobject alarmConfiguration,
        jbyteArray nonActionableConfiguration,
        jbyteArray bArr,
        jint i2,
        jint i3,
        jint i4,
        jbyteArray bArr2,
        jobject out,
        jobject out2,
        jobject out3,
        jobject out4,
        jobject out5,
        jobject out6){

    return NULL;
}


//public native void initialize(Object obj);
extern "C"
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_initialize(
        JNIEnv *env, jobject thisObj,
        jobject obj) {
}
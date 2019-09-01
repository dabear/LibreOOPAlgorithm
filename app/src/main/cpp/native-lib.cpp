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

//private native byte getActivationCommand(int i, byte[] bArr);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getActivationCommand(
        JNIEnv *env, jobject thisObj,
        jint i,
        jbyteArray bArr) {
}

//private native byte[] getActivationPayload(int i, byte[] bArr, byte[] bArr2, byte b);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getActivationPayload(
        JNIEnv *env, jobject thisObj,
        jint i,
        jbyteArray bArr,
		jbyteArray bArr2,
		jbyte b
		) {
}


//private native MemoryRegion getNextRegionToRead(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, int i2);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getNextRegionToRead(
        JNIEnv *env, jobject thisObj,
        jint i,
        jbyteArray bArr,
		jbyteArray bArr2,
		jbyteArray bArr3,
        jint i2) {

}

//private native boolean getPatchTimeValues(int i, byte[] bArr, Out<Integer> out, Out<Integer> out2);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getNextRegionToRead(
        JNIEnv *env, jobject thisObj,
        jint i,
        jbyteArray bArr,
		jobject out,
		jobject out2) {
}


 //public native long getStatusCode(String str, int i, int i2, int i3, boolean z);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getStatusCode(
        JNIEnv *env, jobject thisObj,
        jint i,
		jint i2,
		jint i3,
		jboolean z) {
}

//private native boolean getNeedsReaderInfoForActivation(int i);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getNeedsReaderInfoForActivation(
        JNIEnv *env, jobject thisObj,
        jint i) {

}

//private native MemoryRegion getNextRegionToRead(int i, byte[] bArr, int i2);
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_getNextRegionToRead_v1(
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
JNIEXPORT jobject JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_processScan_v1(
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



// private native DataProcessingResult processScan(int i, AlarmConfiguration alarmConfiguration, NonActionableConfiguration nonActionableConfiguration,
// AttenuationConfiguration attenuationConfiguration, byte[] bArr, byte[] bArr2, byte[] bArr3, int i2, int i3, int i4,
// int i5, int i6, byte[] bArr4, byte[] bArr5, Out<Integer> out, Out<Integer> out2, Out<Boolean> out3, Out<Boolean> out4,
// Out<byte[]> out5, Out<byte[]> out6, Out<AlgorithmResults> out7, Out<List<PatchEvent>> out8, Out<Byte> out9);
extern "C"
JNIEXPORT jobject JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_processScan(
        JNIEnv *env, jobject thisObj,
        jint i,
        jobject alarmConfiguration,
		jobject nonActionableConfiguration,
		jobject attenuationConfiguration,
        jbyteArray bArr,
		jbyteArray bArr2,
		jbyteArray bArr3,
        jint i2,
        jint i3,
        jint i4,
		jint i5,
		jint i6,
        jbyteArray bArr4,
		jbyteArray bArr5,
        jobject out,
        jobject out2,
        jobject out3,
        jobject out4,
        jobject out5,
        jobject out6,
		jobject out7,
		jobject out8,
		jobject out9){

    return NULL;
}




//public native void initialize(Object obj);
extern "C"
JNIEXPORT void JNICALL Java_com_abbottdiabetescare_flashglucose_sensorabstractionservice_dataprocessing_DataProcessingNative_initialize(
        JNIEnv *env, jobject thisObj,
        jobject obj) {
}

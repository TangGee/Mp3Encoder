package com.yumin.mp3encoder;

public class Mp3Encoder {

    static {
        System.loadLibrary("mp3-lib");
        initIds();
    }

    private long nativeObj = -1;

    private native long nativeInit();

    public native void encodeInit(String pcmFilePath,String outMp3Path,int sampleRate,int channels,int ratebit);

    public native void encod();

    public native void nativeEndEncod();

    public Mp3Encoder(){
        nativeObj = nativeInit();
    }

    private static native void initIds();

    public native void nativeDestory();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        nativeDestory();
    }
}

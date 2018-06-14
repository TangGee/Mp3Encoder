//
// Created by tangtang on 18/6/12.
//

#include "Mp3Encoder.h"


Mp3Encoder::Mp3Encoder() {
}

Mp3Encoder::~Mp3Encoder() {
   __destory();
}

int Mp3Encoder::init(const char *pcmFilePath, const char *mp3OutPath, int sampleRate, int channels, int bitRate) {

    pcmFile = fopen(pcmFilePath,"r");
    if (!pcmFile) return  -1;
    mp3File = fopen(mp3OutPath,"w+");
    if (!mp3File) return -1;

    lameClient = lame_init();
    lame_set_in_samplerate(lameClient,sampleRate);
    lame_set_num_channels(lameClient,channels);
    lame_set_brate(lameClient,bitRate);

    return 0;
}


int Mp3Encoder::encod() {
    int bufferSize = 256*1024; //256k
    short *leftBuffer = new short[bufferSize/4]; //左声道
    short *rightBuffer = new short[bufferSize/4];//右声道
    char *buffer = new char[bufferSize/2];
    unsigned char *mp3buffer = new unsigned char[bufferSize];
    int readsize = 0;
    while((readsize = fread(buffer,2,bufferSize/2,pcmFile))>0) {
        for (int i = 0; i < readsize; ++i) {
            if (i%2==0){
                leftBuffer[i/2] = buffer[i];
            } else {
                rightBuffer[i/2] = buffer[i];
            }
        }
        size_t wroteSize = lame_encode_buffer(lameClient,leftBuffer,rightBuffer,readsize/2,mp3buffer,bufferSize);
        fwrite(mp3buffer,1,wroteSize,mp3File);
    }

    delete[] leftBuffer;
    delete[] rightBuffer;
    delete[] buffer;
    delete[] mp3buffer;

    return 0;
}

void Mp3Encoder::destory() {
    __destory();
}


void Mp3Encoder::__destory() {
    if (pcmFile) {
        fclose(pcmFile);
        pcmFile = NULL;
    }

    if (mp3File) {
        fclose(mp3File);
        mp3File = NULL;
    }
    if (lameClient) {
        lame_close(lameClient);
        lameClient = NULL;
    }
}



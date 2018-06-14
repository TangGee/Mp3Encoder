//
// Created by tangtang on 18/6/12.
//

#ifndef UNTITLED8_MP3ENCORDER_H
#define UNTITLED8_MP3ENCORDER_H


#include <cstdio>
#include <lame/lame.h>

class Mp3Encoder {
private:
    FILE* pcmFile;
    FILE* mp3File;
    lame_t lameClient;

    void __destory();

public:
    Mp3Encoder();
    ~Mp3Encoder();

    int init(const char* pcmFilePath, const char *mp3OutPath, int sampleRate, int channels, int bitRate);
    int encod();
    void destory();
};


#endif //UNTITLED8_MP3ENCORDER_H

package com.bemonovoid.playqd.core;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioStreamingServer {

    public static void main(String[] args) throws Exception {
        String wavFilePath = "D:/Music/Downtempo/8. out is in.wav";
        File wavFile = new File(wavFilePath);
//        WAVE (.wav) file, byte length: 73215294, data format: PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian, frame length: 18289664
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(wavFile);
        System.out.println(audioFileFormat);

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);

        AudioFormat audioFormat = audioInputStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();

        InetAddress address = InetAddress.getByName("localhost");
        DatagramSocket socket = new DatagramSocket(5555);

        long total = 0;
        long totalToRead = audioInputStream.getFrameLength();
        int numOfBytesToRead = 0;
//        while (total < totalToRead) {
        while (true) {
            byte[] buffer = new byte[1024];
            numOfBytesToRead = audioInputStream.read(buffer, 0,  1024);
            if (numOfBytesToRead == -1) {
                sourceDataLine.drain();
                break;
            }
//            total += numOfBytesToRead;
//            sourceDataLine.write(buffer, 0, 1024);
            DatagramPacket request = new DatagramPacket(buffer, numOfBytesToRead, address, 5555);
            socket.send(request);
        }
        Thread.sleep(100000);

    }
}

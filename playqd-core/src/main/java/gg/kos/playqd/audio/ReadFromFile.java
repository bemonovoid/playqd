package gg.kos.playqd.audio;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class ReadFromFile {

    public static void main(String[] args) throws Exception {
        String wavFilePath = "D:/Music/AUDIO TEST/a2002011001-e02.wav";
        File wavFile = new File(wavFilePath);

//        WAVE (.wav) file, byte length: 73215294, data format: PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian, frame length: 18289664
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(wavFile);
        System.out.println(audioFileFormat);

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);

        playback(audioInputStream);

//        int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
//        System.out.println("Bytes per frame: " + bytesPerFrame);
//        if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
//            bytesPerFrame = 1;
//        }
//        int numBytes = 1024 * bytesPerFrame;
//        byte[] audioBytes = new byte[numBytes];
//
//        int totalFramesRead = 0;
//
//        int numBytesRead = 0;
//        int numFramesRead = 0;
//        while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
//            numFramesRead = numBytesRead / bytesPerFrame;
//            totalFramesRead += numFramesRead;
//            System.out.println(numBytesRead);
//        }
    }

    private static void playback(AudioInputStream audioInputStream) throws Exception {
        AudioFormat audioFormat = audioInputStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();

        long total = 0;
        long totalToRead = audioInputStream.getFrameLength();
        long numOfBytesToRead = 0;


        System.out.println("Line isOpen: " + sourceDataLine.isOpen());
        System.out.println("Line isRunning: " + sourceDataLine.isRunning());
        System.out.println("Line isActive: " + sourceDataLine.isActive());

        while (total < totalToRead) {
            int available = sourceDataLine.available();
            byte[] buffer = new byte[available];
            numOfBytesToRead = audioInputStream.read(buffer, 0, available);
            if (numOfBytesToRead == -1) {
                break;
            }
            total += numOfBytesToRead;
            sourceDataLine.write(buffer, 0, available);
        }

        Thread.sleep(100000);
//
//        while (sourceDataLine.isActive()) {
//            continue;
//        }

        System.out.println("Read from file to buffers completed");

//        System.out.println("Line isOpen: " + sourceDataLine.isOpen());
//        System.out.println("Line isRunning: " + sourceDataLine.isRunning());
//        System.out.println("Line isActive: " + sourceDataLine.isActive());
//
//        sourceDataLine.drain();
//        sourceDataLine.stop();
//        sourceDataLine.close();
//
//        System.out.println("Playing completed");
//
//        System.out.println("Line isOpen: " + sourceDataLine.isOpen());
//        System.out.println("Line isRunning: " + sourceDataLine.isRunning());
//        System.out.println("Line isActive: " + sourceDataLine.isActive());




    }
}

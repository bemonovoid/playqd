package com.bemonovoid.playqd.core;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class PlayFile {

    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {

        String wavFilePath = "D:/Music/AUDIO TEST/a2002011001-e02.wav";
        File wavFile = new File(wavFilePath);

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(wavFile);

            AudioFormat format = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);

            audioLine.open(format);

            audioLine.start();

            System.out.println("Playback started.");

            byte[] bytesBuffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
                audioLine.write(bytesBuffer, 0, bytesRead);
            }

            audioLine.drain();
            audioLine.close();
            audioStream.close();

            System.out.println("Playback completed.");

        } catch (Exception ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        }
    }
}

package com.bemonovoid.playqd.core;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

public class ReactivePlay {

    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        String wavFilePath = "D:/Music/AUDIO TEST/a2002011001-e02.wav";
        File wavFile = new File(wavFilePath);
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(wavFile);

            byte[] bytesBuffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            DirectProcessor<BytesInfo> range = DirectProcessor.create();
            Flux<BytesInfo> streamTo = range.doOnComplete(() -> System.out.println("Streaming completed"));

            streamTo.subscribe(new AudioStreamSubscriber("A1", audioStream.getFormat()));

            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(4);
                    System.out.println("Adding new audio subscriber");
                    streamTo.subscribe(new AudioStreamSubscriber("A2", audioStream.getFormat()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
                range.onNext(new BytesInfo(bytesRead, bytesBuffer));
            }

            range.onComplete();

            audioStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class BytesInfo {
        private final int bytesRead;
        private final byte[] bytes;

        public BytesInfo(int bytesRead, byte[] bytes) {
            this.bytesRead = bytesRead;
            this.bytes = bytes;
        }

        public int getBytesRead() {
            return bytesRead;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

    public static class AudioStreamSubscriber extends BaseSubscriber<BytesInfo> {

        private final String id;
        private final SourceDataLine audioLine;

        public AudioStreamSubscriber(String id, AudioFormat format) {
            this.id = id;
            try {
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                audioLine = (SourceDataLine) AudioSystem.getLine(info);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        protected void hookOnSubscribe(Subscription subscription) {

            try {
                audioLine.open(audioLine.getFormat());
                System.out.println(id + " Audio line opened");
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }

            audioLine.start();

            super.hookOnSubscribe(subscription);
        }

        @Override
        protected void hookOnNext(BytesInfo bytesInfo) {
            audioLine.write(bytesInfo.getBytes(), 0, bytesInfo.getBytesRead());
        }

        @Override
        protected void hookOnComplete() {
            System.out.println(id + ": Subscriber completed");
            super.hookOnComplete();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            System.out.println(id + ":  Subscriber failed: " + throwable.getMessage());
            closeAudioLine();
        }

        @Override
        protected void hookOnCancel() {
            System.out.println(id + ": Cancelling subscriber");
            closeAudioLine();
        }

        private void closeAudioLine() {
            System.out.println("Closing audio line");
            audioLine.drain();
            audioLine.close();
        }
    }
}

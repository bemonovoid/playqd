package com.bemonovoid.playqd.core.helpers;

import java.io.File;

import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;

@Slf4j
public final class AudioFileUtils {

    public static String getFileNameWithoutExtension(AudioFile audioFile) {
        String fileName = audioFile.getFile().getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String readTagOrNull(AudioFile audioFile, FieldKey key) {
        try  {
            return audioFile.getTag().getFirst(key);
        } catch (UnsupportedOperationException e) {
            log.error("Failed to read {} tag from path {}. \n {}",
                    key, audioFile.getFile().getAbsolutePath(), e.getMessage());
            return null;
        }
    }

    public static AudioFile readAudioFile(File file) {
        try {
            return AudioFileIO.read(file);
        } catch (Exception e) {
            log.error(String.format("Unable to read AudioFile from: %s", file.getAbsolutePath()), e);
            return null;
        }
    }

    private AudioFileUtils() {

    }
}

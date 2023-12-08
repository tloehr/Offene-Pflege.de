package de.offene_pflege.op.tools;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundPlayer implements LineListener {
    boolean isPlaybackCompleted;


    @Override
    public void update(LineEvent event) {

        if (LineEvent.Type.START == event.getType()) {
            System.out.println("Playback started.");
        } else if (LineEvent.Type.STOP == event.getType()) {
            isPlaybackCompleted = true;
            System.out.println("Playback completed.");
        }
    }

    /**
     *
     * Play a given audio file.
     * @param audioFilePath Path of the audio file.
     *
     */
    public void play(String audioFilePath) {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(audioFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);

            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.addLineListener(this);
            audioClip.open(audioStream);
            audioClip.start();
            while (!isPlaybackCompleted) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            audioClip.close();
            audioStream.close();

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
            System.out.println("Error occured during playback process:"+ ex.getMessage());
        }

    }


}

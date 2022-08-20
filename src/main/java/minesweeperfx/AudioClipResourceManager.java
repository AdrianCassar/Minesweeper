package minesweeperfx;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
public enum AudioClipResourceManager {
    Click(ClassLoader.getSystemResourceAsStream("audio/Click.wav")),
    Step(ClassLoader.getSystemResourceAsStream("audio/Step.wav")),
    Flag(ClassLoader.getSystemResourceAsStream("audio/Flag.wav")),
    Hover(ClassLoader.getSystemResourceAsStream("audio/Hover.wav")),
    Win(ClassLoader.getSystemResourceAsStream("audio/Win.wav")),
    Boom(ClassLoader.getSystemResourceAsStream("audio/Boom.aiff"));
    
    private Clip audioClip;
    
    AudioClipResourceManager(InputStream audioClipStrem) {
            AudioInputStream audioInputStream;
            
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(audioClipStrem));
            
            audioClip = AudioSystem.getClip();
            audioClip.open(audioInputStream);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
            Logger.getLogger(AudioClipResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void playAudioClip () {
        if (((FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN)).getValue() != 0) {
            // || audioClip.getFramePosition() > 0 && audioClip.getFramePosition() < audioClip.getFrameLength()
            if ((AudioClipResourceManager.Boom.getAudioClip().equals(audioClip)) && audioClip.isRunning() || audioClip.getFramePosition() == audioClip.getFrameLength()) {
                audioClip.setFramePosition(0);
            }

            if ((AudioClipResourceManager.Boom.getAudioClip().equals(audioClip)) || !audioClip.isRunning()) {
                audioClip.start();
            }
        }
    }

    public Clip getAudioClip() {
        return audioClip;
    }
    
    public static void setVolume(int volume) {
        for (AudioClipResourceManager value : AudioClipResourceManager.values()) {
            if(!AudioClipResourceManager.Click.getAudioClip().equals(value.getAudioClip())) {
                ((FloatControl)value.getAudioClip().getControl(FloatControl.Type.MASTER_GAIN)).setValue(volume);
            } else {
                ((FloatControl)value.getAudioClip().getControl(FloatControl.Type.MASTER_GAIN)).setValue(1);
            }
        }
    }
}

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioPlayer {

    public void playScore() {
        playSound("lib/scoring.wav");
    }

    public void playJump(){
        playSound("lib/jump.wav");
    }

    public void playBackround(){
        playSound("lib/background.wav");
    }

    public void playDead(){
        playSound("lib/dead.wav");
    }

    public void playSound(String path){
        try{
            AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream2);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
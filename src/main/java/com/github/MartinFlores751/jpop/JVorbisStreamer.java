package com.github.MartinFlores751.jpop;

import com.github.MartinFlores751.VolumeControl;
import com.github.MartinFlores751.events.VolumeChangeEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class JVorbisStreamer implements Runnable, VolumeControl {
    private boolean isShuttingDown = false;
    private final byte[] buffer = new byte[4096];
    private boolean volChange = false;
    private float gainChange = -5f;

    public void shutdown() {
        isShuttingDown = true;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(VolumeChangeEvent e) {
        if (e.isVolumeUp())
            incVolume();
        else
            decVolume();
    }

    @Override
    public void incVolume() {
        gainChange += 1f;
        volChange = true;
    }

    @Override
    public void decVolume() {
        gainChange -= 1f;
        volChange = true;
    }

    synchronized private void playSong(Thread thread) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        AudioInputStream weebIn = AudioSystem.getAudioInputStream(new URL("https://listen.moe/stream"));

        // Input format
        AudioFormat baseFormat = weebIn.getFormat();

        // Target output format
        AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
                16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

        // Create input stream from stream
        AudioInputStream dataIn = AudioSystem.getAudioInputStream(targetFormat, weebIn);

        // get a line from a mixer in the system with the desired format
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

        if (line != null) {
            line.open();

            // Get Volume Control and set current gain
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gainChange);

            line.start();

            // Process audio buffer length bytes at a time
            int nBytesRead = 0;
            while (nBytesRead != -1) {
                if (isShuttingDown) {
                    line.flush();
                    line.stop();
                    line.close();

                    dataIn.close();
                    return;
                }
                nBytesRead = dataIn.read(buffer, 0, buffer.length);
                if (nBytesRead != -1) {
                    line.write(buffer, 0, nBytesRead);
                }
                if (volChange) {
                    gainControl.setValue(gainChange);
                    volChange = false;
                }
            }

            line.drain();
            line.stop();
            line.close();

            dataIn.close();
        }

        weebIn.close();
    }

    @Override
    public void run() {
        EventBus.getDefault().register(this);
        try {
            while (!isShuttingDown) {
                playSong(Thread.currentThread());
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}

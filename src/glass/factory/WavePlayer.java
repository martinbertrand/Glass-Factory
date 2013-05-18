/**
 * *****************************************************************************
 * WavePlayer.java - The main sample player class by Martin Bertrand
 *
 * Purpose: This class connects to the mixer object and plays supplied samples
 * using Java Sound API classes. We create a Line interface class with the
 * SourceDataLine class that connect the OS's internal mixer. We then write into
 * a byteBuffer object the amplitude values of a signal.
 *
 * @author Martin Bertrand
 * @version 1.0
 * *****************************************************************************
 */
package glass.factory;

import javax.sound.sampled.*;

/**
 * @author Martin Bertrand
 */
public class WavePlayer extends Thread {

    /* Sample buffer constants */
    public static final int BUFFER_SIZE = 1000;    // Chunk of audio processed at one time
    public static final int SAMPLES_PER_BUFFER = BUFFER_SIZE / 2;
    /* AudioFormat parameters */
    public static final int SAMPLE_RATE = 44100;    // Samples per second
    private static final int SAMPLE_SIZE = 16;      // Sample size in bits
    private static final int CHANNELS = 1;          // Number of channels
    private static final boolean SIGNED = true;     // Sample value is signed
    private static final boolean BIG_ENDIAN = true; // Big Endian style! Most significant byte is to the left
    private static boolean KILLALL = false;          // will force all running wave threads to exit when pressing QUIT button
    /* Instance members */
    private AudioFormat format;                     // Format object to pass to line
    private DataLine.Info info;
    private SourceDataLine auline;                  // SourceDataline object that connects to internal OS' mixer
    private boolean done;                           // To break the run method of Thread
    private byte[] sampleData = new byte[BUFFER_SIZE];
    private SampleProviderInterface provider;       // Will supply buffer with samples
    private boolean hasStarted;                     // Will force the creation of a new thread if original has been killed

    /* Constructor */
    public WavePlayer() {

        /* Creates the audio format object and the info object related */
        format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, SIGNED, BIG_ENDIAN);
        info = new DataLine.Info(SourceDataLine.class, format);
        hasStarted = false;
    }

    @Override
    public void run() {

        if (provider == null) {
            return;
        }
        done = false;
        int nBytesRead = 0;
        try {
            // Get and start line to write data to
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
            auline.start();
            while ((nBytesRead != -1) && (!done) && (!KILLALL)) {
                nBytesRead = provider.getSamples(sampleData);
                if (nBytesRead > 0) {
                    auline.write(sampleData, 0, nBytesRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            auline.drain();     // Plays remaining samples
            auline.close();     // Close resource                    
        }
    }

    /* Starts the thread If original thread has been killed, we create a new one */
    public void startPlayer() {

        if (provider != null) {
            if (hasStarted == false) {
                hasStarted = true;
                start();
            } else {
                Thread t = new Thread(this);
                t.start();
            }
        }
    }

    /* Breaks the while loop to finish the run method and kills the thread */
    public void stopPlayer() {

        done = true;
    }

    /* Sets the sample provider */
    public void setSampleProvider(SampleProviderInterface provider) {

        this.provider = provider;
    }

    /*  Will stop all running threads simultanously */
    public static void setKillAllToTrue() {

        KILLALL = true;
    }
    /* WavePlayer.java */
}
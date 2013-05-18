/**
 * *****************************************************************************
 * SampleProvider.java - An basic sample provider class by Martin Bertrand
 *
 * Purpose: This class supplies samples made of amplitude values where the user
 * can control base frequency, its signal shape (SINE, SQUARE, SAW). This base
 * signal can be used either to create frequency modulation (FM) and amplitude
 * modulation (AM). in another signal. It is used in conjunction with the
 * ExtendedSampleProvider class.
 *
 * @author Martin Bertrand
 * @version 1.0
 * *****************************************************************************
 */
package glass.factory;

/**
 * @author Martin Bertrand
 */
public class SampleProvider implements SampleProviderInterface {

    /* Buffer info from class that uses the sample provider */
    private int sampleRate;                        // Sample rate of the wave player
    private int bufferSize;                        // Buffer size in bytes
    private int samplesPerBuffer;                  // Number of samples per buffer size
    private int sampleSize;                        // Sample size in bytes of each sample
    /* Instance members */
    protected WAVESHAPE waveshape;                 // Type of wave built
    protected long periodSamples;                  // Number of samples for one period
    protected long sampleNumber;                   // Index of sample in buffer
    protected double frequency;                    // Frequency in Hz

    /* Default constructor  with preset values */
    public SampleProvider() {

        /* Set default values */
        this.sampleRate = 44100;
        this.bufferSize = 1000;
        this.sampleSize = 2;
        this.samplesPerBuffer = bufferSize / sampleSize;

        waveshape = WAVESHAPE.SIN;
        frequency = 440.0;
    }

    /* Contructor with frequency, wave shape and calling buffer info */
    public SampleProvider(double frequency, WAVESHAPE waveShape, int sampleRate,
            int bufferSize, int sampleSize) {

        /* Calling buffer info */
        this.waveshape = waveShape;
        this.frequency = frequency;
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;
        this.sampleSize = sampleSize;
        this.samplesPerBuffer = bufferSize / sampleSize;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * Sets waveshape of oscillator
     *
     * @param waveshape Determines the wave shape of this oscillator
     */
    public void setOscWaveshape(WAVESHAPE waveshape) {

        this.waveshape = waveshape;
    }

    /* Returns the used waveshape */
    public WAVESHAPE getOscWavesShape() {

        return waveshape;
    }

    /**
     * Set the frequency of the oscillator in Hz.
     *
     * @param frequency Frequency in Hz for this oscillator
     */
    public void setFrequency(double frequency) {

        if (frequency <= 0.0) {
            periodSamples = 0;
            this.frequency = 0.0;
            return;
        }
        this.frequency = frequency;
        periodSamples = (long) (sampleRate / frequency); // Number of samples for one period
    }

    /* Returns frequency */
    public double getFrequency() {

        return frequency;
    }

    /**
     * Return the next sample of the oscillator's waveform
     *
     * @return Next oscillator sample
     */
    protected double getSample() {

        double value = 0.0;
        double x = 0.0;
        if (periodSamples == 0) { // No sound!
            return 0.0;
        }
        x = sampleNumber / (double) periodSamples; // Represents portion of completed period
        switch (waveshape) {
            case SIN:
                value = Math.sin(2.0 * Math.PI * x);
                break;
            case SQU:
                if (sampleNumber < (periodSamples / 2)) {
                    value = 1.0;
                } else {
                    value = -1.0;
                }
                break;
            case SAW:
                value = 2.0 * (x - Math.floor(x + 0.5));
                break;
            default:
                break;
        }
        sampleNumber = (sampleNumber + 1) % periodSamples; // Increments sampleNumber and resets to 0
        return value;
    }

    /**
     * Get a buffer of oscillator samples
     *
     * @param buffer Array to fill with samples
     * @return Count of bytes produced.
     */
    @Override
    public int getSamples(byte[] buffer) {

        int index = 0;
        for (int i = 0; i < samplesPerBuffer; i++) {
            double ds = getSample() * Short.MAX_VALUE;
            short ss = (short) Math.round(ds);
            buffer[index++] = (byte) (ss >> 8); // Takes higher byte first
            buffer[index++] = (byte) (ss & 0xFF); // Saves lower byte after
        }
        return bufferSize;
    }
    /* SampleProvider.java */
}

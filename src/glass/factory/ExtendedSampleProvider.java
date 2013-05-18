/**
 * *****************************************************************************
 * ExtendedSampleProvider.java - An extended sample provider class by Martin
 * Bertrand
 *
 * Purpose: This class supplies samples made of amplitude values where the user
 * can control base frequency, its signal shape (SINE, SQUARE, SAW). This base
 * signal can also be modulated in frequency (FM) and amplitude (AM). This is
 * achieved by the use of other signal generators of the SampleProvider class.
 *
 * @author Martin Bertrand
 * @version 1.0
 * *****************************************************************************
 */
package glass.factory;

/**
 * @author Martin Bertrand
 */
public class ExtendedSampleProvider extends SampleProvider {

    /* Static members */
    public static double MASTER_VOLUME = 1.0;    // Master volume of class will control all open oscillators
    /* Instance members */
    private double volume;                       // Volume of each runnning class, for balancing each class
    private SampleProvider LAM;                  // Simple sample provider for Low Amplitude Modulation oscillator
    private SampleProvider LFO;                  // Simple sample provider to create a Low Frequency oscillator    
    private double baseFrequency;                // Basic oscillator frequency we are creating
    private WAVESHAPE baseWaveShape;             // Base signal shape

    /**
     * ExtendedSampleProvider Class Constructor Default instance has SIN wave
     * shape at 440.0 Hz
     */
    public ExtendedSampleProvider() {

        /* Set default values */
        super();
        volume = 1.0;
        LAM = new SampleProvider(); // initialized sine shape
        LFO = new SampleProvider();
        LAM.setFrequency(0.0);
        LFO.setFrequency(0.0);
    }

    /* BASE FREQUENCY CONTROLS *************************************************/
    /* Sets the principal frequency of the oscillator */
    public void setBaseFrequency(double frequency) {

        baseFrequency = frequency;
    }

    public void setBaseWaveShape(WAVESHAPE waveShape) {

        baseWaveShape = waveShape;
        super.setOscWaveshape(baseWaveShape);
    }

    /* VOLUME CONTROL METHODS *************************************************/
    /* Controls the master volume of all open oscillators */
    public static void setMasterVolume(double volume) {

        MASTER_VOLUME = volume;
    }

    /* Sets the volume of each oscillator for balance between oscillators */
    public void setVolume(double volume) {

        this.volume = volume;
    }

    /* LFO CLASS CONTROL METHODS **********************************************/
    /* Sets wave shape of LFO */
    public void setLFOWaveShape(WAVESHAPE waveShape) {

        LFO.setOscWaveshape(waveshape);
    }

    /* Sets frequency of LFO, at what rate will the frequency change */
    public void setLFOFrequency(double frequency) {

        LFO.setFrequency(frequency);
    }

    /* LAM CLASS CONTROL METHODS **********************************************/
    /* Sets wave shape of LAM */
    public void setLAMWaveShape(WAVESHAPE waveShape) {

        LAM.setOscWaveshape(waveshape);
    }

    /* Sets frequency of LAM, how fast the  overall amplitude will change */
    public void setLAMFrequency(double frequency) {

        LAM.setFrequency(frequency);
    }

    /**
     * Return the next sample of the oscillator's waveform
     *
     * @return Next oscillator sample
     */
    @Override
    protected double getSample() {

        double freq = baseFrequency; // Base frequency of main oscillator        
        if (LFO.getFrequency() > 0.0) { // If FM
            double LFOValue = LFO.getSample(); //LFO freq set independently
            freq = freq * Math.pow(2.0, LFOValue);
        }
        super.setFrequency(freq); // Modulates frequency of  base oscillator        
        double sample = super.getSample(); // Get the osc sample        
        if (LAM.getFrequency() > 0.0) { // If AM
            double LAMOffset = (LAM.getSample() + 1.0) / 2.0; // Makes the sine output between 0 and 1     
            sample = sample * LAMOffset;
        }
        return sample * MASTER_VOLUME * volume; // Adjust the overall volumes before sending out      
    }
    /* ExtendedSampleProvider.java */
}

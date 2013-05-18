package glass.factory;

/**
 * @author Martin Bertrand
 */
public interface SampleProviderInterface {

    /* Class that implements interface will fill samples buffer with amplitude values 
     and return number of bytes written */
    public int getSamples(byte[] samples);
}

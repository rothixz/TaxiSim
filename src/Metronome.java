import static java.lang.System.*;
import java.util.concurrent.*;

public class Metronome implements Runnable
{
    /*
     * period in milliseconds
     */
    public Metronome(long period)
    {
        assert period > 0;

        this.period = period;
        clock = Executors.newScheduledThreadPool(1);
        clock.scheduleAtFixedRate(this, 0, period, TimeUnit.MILLISECONDS);
    }

    /*
     * Metronome period in milliseconds
     */
    public long period()
    {
        return period;
    }

    protected ScheduledExecutorService clock;
    protected Metronome tick;

    public synchronized void sync()
    {
        try { wait(); } catch(InterruptedException e) { exit(1); }
    }

    public synchronized void run()
    {
        notifyAll();
    }

    protected long period;
}

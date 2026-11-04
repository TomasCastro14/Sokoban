package port;


public interface ProgressRepository {

    /** The saved progress, or {@link Progress#initial()} when there is none or it cannot be read. */
    Progress load();

    /** Stores the progress; a failure is logged and the game goes on. */
    void save(Progress progress);
}
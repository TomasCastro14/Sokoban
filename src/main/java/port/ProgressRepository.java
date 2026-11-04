package port;


public interface ProgressRepository {

    Progress load();

    
    void save(Progress progress);
}
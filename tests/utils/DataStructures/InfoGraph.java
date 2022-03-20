package tests.utils.DataStructures;

public class InfoGraph {
    public String subject;
    public String predicate;
    public String    object;
    public Double confidence;

    public InfoGraph (String subject, String predicate, String object,Double confidence) {
        this.subject    = subject;
        this.predicate  = predicate;
        this.object = object;
        this.confidence    = confidence;
    }
}

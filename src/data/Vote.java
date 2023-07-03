package data;

public class Vote {
    private int id;
    private String[] vote;

    public Vote(int id, String[] vote) {
        this.id = id;
        this.vote = vote;
    }

    public int getId() {
        return id;
    }

    public String[] getVote() {
        return vote;
    }

    @Override
    public String toString() {
        String result = "Vote " + id + "\n";

        for (String vote : vote) {
            result += vote + " ";
        }
        result += "\n\n";
        return result;
    }
}

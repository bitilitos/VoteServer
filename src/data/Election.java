package data;

import java.util.*;

public final class Election {



    public String[] getElectionResults(List<Vote> ballots, String[] originalOrder) {
        String[] electionResults = new String[originalOrder.length];
        int totalCandidates = originalOrder.length;
        List<CandidateVoteCount> electionCount = new LinkedList<>();
        for (int i = 0; i < originalOrder.length; i++) {
            electionCount.add(new CandidateVoteCount(originalOrder[i], i));
        }

        for (Vote ballot : ballots) {
            for (int i = 0; i < ballot.getVote().length; i++) {
                for (CandidateVoteCount c : electionCount) {
                    if (c.getCandidate().split(" ")[0].equals(ballot.getVote()[i])){
                        c.sumVotes(totalCandidates - i);
                    }
                }

            }
        }
        Collections.sort(electionCount, new CandidateComparator());
        int i = 0;
        for (CandidateVoteCount c : electionCount) {
            electionResults[i] = c.getCandidate();
            i++;
        }

        return electionResults;
    }

    private class CandidateVoteCount  {
        private String candidate;
        private int voteCount;
        private int originalPosition;

        public CandidateVoteCount(String candidate, int originalPosition) {
            this.candidate = candidate;
            this.voteCount = 0;
            this.originalPosition = originalPosition;
        }

        public String getCandidate() {
            return candidate;
        }

        public int getVoteCount() {
            return voteCount;
        }

        public void sumVotes(int value) {
            voteCount = voteCount + value;
        }

        public int getOriginalPosition() {
            return originalPosition;
        }
    }

    private class CandidateComparator implements Comparator<CandidateVoteCount> {

        // -1 : o1 < o2
        // 0 : o1 == o2
        // +1 : o1 > o2
        @Override
        public int compare(CandidateVoteCount c1, CandidateVoteCount c2) {
            if (c1.getVoteCount() < c2.getVoteCount()) return 1;
            if (c1.getVoteCount() > c2.getVoteCount()) return -1;
            if (c1.getVoteCount() == c2.getVoteCount()) {
                if (c1.originalPosition < c2.getOriginalPosition()) return -1;
                else return 1;
            }
            return 0;
        }
    }
}

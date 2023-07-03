package connection;

import data.Election;
import data.ResultWriter;
import data.Vote;
import gui.ServerWindow;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread{
    private ServerSocket serverSocket;
    private double electionID;
    private List<Socket> clientSockets = new ArrayList<>();
    private ServerWindow window;
    private String[] candidates;
    private String[] electionResult;
    private InetAddress address;
    private int clientsConnected = 0;
    private AtomicInteger ballotsSent = new AtomicInteger(0);
    private AtomicInteger votesReceived = new AtomicInteger(0);
    private List<InetAddress> clientsAlreadyVoted = new ArrayList<>();
    private List<InetAddress> clientsWithBallot = new ArrayList<>();
    List<Vote> votes = new ArrayList<>();



    public Server(ServerWindow window) {
        this.window = window;
    }



    @Override
    public void run() {
        try{
            serverSocket = new ServerSocket(888);
            address = InetAddress.getLocalHost();

        while(true) {

            waitingConnections();

        }

        }catch(IOException e) {
            System.out.println("Unable to establish connection: " + e);
        }
    }

    private void waitingConnections() {

        try{

            System.out.println("Waiting for clients to connect on " +
                    InetAddress.getLocalHost().getHostName() + "...");

            // connect it to client socket
            Socket socket = serverSocket.accept();
            clientSockets.add(socket);
            ServerThread serverThread = new ServerThread(socket, this, window, candidates, address);
            serverThread.start();
            clientsConnected++;



        }catch(IOException e) {
            System.out.println("Unable to establish connection: " + e);
        }
    }

    public void beginElection() {

        Election election = new Election();

        electionResult = election.getElectionResults(votes, candidates);
        window.updateElectionResult(electionResult);
        sendMessageToAll(new Message(address, null, MessageType.SEND_ELECTION, createElectionMsg()));
        File file = ResultWriter.createFile();
        if (!file.exists()) {
            informMessage("Erro Ficheiro", "O programa não conseguiu criar o ficheiro de resultados",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            System.out.println(file.getAbsolutePath());
            ResultWriter.createFileContent(file, candidates,electionResult, votes);
        }
    }


    public void sendMessageToAll(String content) {
        sendMessageToAll(new Message(address, null, MessageType.SEND_MESSAGE, content));
    }

    public void sendMessageToAll(Message message) {
        if (!clientSockets.isEmpty()) {

            for(Socket socket : clientSockets){
               Message msg = message;
                try{

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    System.out.println(msg.getSENDER() +" is sending a message...");
                    oos.writeObject(msg);
                } catch (IOException e) {
                    System.out.println(msg.getSENDER() + " is unable to send message!");
                }
            }

        }else {
            System.out.println("No Clients to send messages");
        }
    }

    public  void closeAllConections() {
        sendMessageToAll(new Message(address, null, MessageType.CLOSING_CONNECTION, ""));
        clientSockets.forEach(s -> closeConection(s));
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean voteReceived(Message message) {
        if (alreadyVoted(message.getSENDER())) return false;
        String ballot = message.getCONTENT().substring(message.getCONTENT().indexOf(":")+1);
        votes.add(new Vote(votesReceived.incrementAndGet(),ballot.split(" ")));
        window.updateVotesReceivedField(votesReceived.get());
        clientsAlreadyVoted.add(message.getSENDER());
        return true;
    }
     public int confirmDialog(String title, String msg) {
         JFrame frame = new JFrame();
         Object[] options = {"Sim", "Não"};
         int result = JOptionPane.showOptionDialog(
                 frame,
                 msg,
                 title,
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.QUESTION_MESSAGE,
                 null,
                 options,
                 options[1]
         );
         return result;

     }


    public boolean alreadyVoted(InetAddress sender) {
        for(InetAddress address : clientsAlreadyVoted) {
            if (address.equals(sender)) return true;
        }
        return false;
    }





    public void closeConection(Socket socket) {
        try {
            socket.close();
            clientsConnected--;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCandidates(String[] candidates) { this.candidates = candidates;}

    public void setElectionID() {
        electionID = Math.random();
    }

    public boolean isCandidatesEmpty() { return candidates==null || candidates.length == 0;}

    public void informMessage(String title, String content, int jOptionPaneMessageType) {
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame,
                content,
                title,
                jOptionPaneMessageType);
    }

    public String createElectionMsg() {
        String msg = "";
        for (int i = 0; i < electionResult.length; i++) {
            if (i < electionResult.length - 1) msg += electionResult[i] + ":";
            else msg += electionResult[i];
        }
        return msg;
    }

    public String[] getCandidates() { return candidates;}

    public Double getElectionID() {return  electionID;}

    public boolean isVotesEmpty() {return votes.isEmpty();}

    public void clearData() {
        electionID = 0;
        candidates = null;
        electionResult = null;
        votesReceived.set(0);
        ballotsSent.set(0);
        clientsAlreadyVoted.clear();
        clientsWithBallot.clear();
        votes.clear();
        String[] data = new String[0];
        window.updateVotesReceivedField(votesReceived.get());
        window.updateBallotsSentField(ballotsSent.get());

        window.updateElectionResult(data);
        window.updateCandidatesJList(data);
    }

    public int getBallotsSent() {return ballotsSent.get();}

    public synchronized void incrementBallotsSent(InetAddress who) {
        if (!clientsWithBallot.contains(who)){
            clientsWithBallot.add(who);
            window.updateBallotsSentField(ballotsSent.incrementAndGet());
        }

    }

    public synchronized int getVotesReceived() {return  votesReceived.get();}


    public static void main(String[] args) {
        ServerWindow window = new ServerWindow("Server");
        Server server = new Server(window);
        window.setVisible(true);
    }
}


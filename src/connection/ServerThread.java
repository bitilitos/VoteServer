package connection;

import gui.ServerWindow;

import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ServerThread extends Thread{
    Socket socket;
    Server server;
    ServerWindow window;
    InetAddress address;

    public ServerThread(Socket socket, Server server, ServerWindow window, String[] candidates, InetAddress address) {
        this.window = window;
        this.socket = socket;
        this.address = address;
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("client connected on " + Thread.currentThread());
        while (!socket.isClosed() && !this.isInterrupted()) {
            receiveMessage();
        }
    }

    public void receiveMessage(){

        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(socket.getInputStream());
            Message msg = (Message) ois.readObject();
            System.out.println("Message Received " + msg.getCONTENT());
            //window.updateMessageReceivedJTextField(msg.getCONTENT());

           switch (msg.getMSG_TYPE()) {
               case REQUEST_BALLOT:
                   if (server.isCandidatesEmpty()) {
                       sendMessage(new Message(address, socket.getInetAddress(), MessageType.SEND_MESSAGE,
                               "O servidor ainda não iniciou a votação."));
                   } else {
                       sendMessage(new Message(address, socket.getInetAddress(),
                               MessageType.RESPONSE_BALLOT,createBalotRequestMsg(server.getCandidates())));
                       server.incrementBallotsSent(msg.getSENDER());
                       window.updateConsoleInformation("Boletim de voto enviado a " + socket.getInetAddress());
                   }

                   break;
               case SEND_VOTE:
                   String electionID = msg.getCONTENT().substring(0, msg.getCONTENT().indexOf(":"));
                   if (Double.parseDouble(electionID) != server.getElectionID()) {
                       sendMessage(new Message(address, socket.getInetAddress(), MessageType.SEND_MESSAGE,
                               "O servidor já iniciou nova votação. Clique em \"Pedir Boletim\"."));
                       break;
                   }
                   if (server.voteReceived(msg)) {
                       sendMessage(new Message(address, socket.getInetAddress(), MessageType.CONFIRMATION_VOTE,
                               msg.getCONTENT()));
                       window.updateConsoleInformation("Voto recebido! ");
                   } else {
                       sendMessage(new Message(address, socket.getInetAddress(), MessageType.ALREADY_VOTED, ""));
                       window.updateConsoleInformation("Voto recebido mas descartado por já ter votado!");
                       window.updateConsoleInformation("Voto descartado proveniente de: " + socket.getInetAddress());
                   }
                   break;
               case CLOSING_CONNECTION:
                   if (!socket.isClosed()) socket.close();
                   server.closeConection(socket);
                   this.interrupt();
                   break;
           }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public String createBalotRequestMsg(String[] candidates) {
        String msg = server.getElectionID().toString() + ":";
        for (int i = 0; i < candidates.length; i++) {
            if (i < candidates.length - 1) msg += candidates[i] + ":";
            else msg += candidates[i];
        }
        return msg;
    }


    public void sendMessage(Message msg) {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println(" is sending a message...");
            oos.writeObject(msg);
        } catch (IOException e) {
            System.out.println(" is unable to send message!");

         }
        }


}

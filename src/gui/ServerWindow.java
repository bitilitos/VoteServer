package gui;


import data.LoadData;
import connection.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class ServerWindow {
    private static final Dimension JLIST_SIZE = new Dimension(400, 350);
    private Server server = null;
    private JFrame frame;
    private JTextField messageReceived;
    private JTextField messageToSend;
    private JLabel ballotsSentField;
    private JLabel votesReceivedField;
    private JScrollPane candidatesScrollPane = new JScrollPane();
    private JList<String> candidatesJList = new JList<>();
    private JScrollPane electionResultScrollPane = new JScrollPane();
    private JList<String> electionResultJList = new JList<>();
    private JList<String> console = new JList<>();
    private List<String> consoleInfoList = new LinkedList<>();




    private static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font TEXT_FONT = new Font("Calibri", Font.PLAIN, 18);



    public ServerWindow(String name) {
        frame = new JFrame(name);
        frame.setSize(900,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("res/server_icon.png")));
        frame.setLayout(new GridBagLayout());
        frame.setResizable(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(createBeginVotingButton(), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        frame.add(createBeginElectionButton(), gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.gridheight = 2;
        //frame.add(createMessageContainer(),gbc);
        frame.add(createDataContainer(),gbc);

        gbc.gridheight = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        frame.add(createJListContainer(candidatesJList, candidatesScrollPane, "Boletim de voto:"), gbc);

        gbc.gridx = 1;
        frame.add(createJListContainer(electionResultJList, electionResultScrollPane, "Resultado da eleição:"), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        frame.add(createConsoleJList(console), gbc);

        frame.pack();

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                server.closeAllConections();
                e.getWindow().dispose();
            }
        });


    }

    private Container createJListContainer(JList jList, JScrollPane scrollPane, String title) {
        Container container = new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        gbc.insets = new Insets(10,10,2,10);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TEXT_FONT);
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        container.add(titleLabel, gbc);


        gbc.insets = new Insets(0,10,10,10);
        gbc.gridy = 1;
        gbc.gridx = 0;

        jList.setVisibleRowCount(14);
        jList.setLayoutOrientation(JList.VERTICAL);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(JLIST_SIZE);
        scrollPane.setViewportView(createCandidatesJList(jList));
        container.add(scrollPane, gbc);
        return container;
    }


    private Container createDataContainer() {
        Container container =  new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel ballotsSentLabel = new JLabel("Boletins enviados:");
        ballotsSentLabel.setFont(TEXT_FONT);
        container.add(ballotsSentLabel,gbc);

        gbc.gridx = 1;
        ballotsSentField = new JLabel("0");
        ballotsSentField.setFont(TEXT_FONT);
        container.add(ballotsSentField,gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel votesReceivedLabel = new JLabel("Votos registados:");
        votesReceivedLabel.setFont(TEXT_FONT);
        container.add(votesReceivedLabel ,gbc);

        gbc.gridx = 1;
        votesReceivedField = new JLabel("0");
        votesReceivedField.setFont(TEXT_FONT);
        container.add(votesReceivedField,gbc);

        return container;
    }

    private Container createMessageContainer() {
        Container container =  new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10,10,10,10);
        gbc.gridy = 0;
        gbc.gridx = 0;
        messageToSend = createMessageToSendJTextField();
        container.add(messageToSend,gbc);

        gbc.gridy = 2;
        messageReceived = createMessageReceivedJTextField();
        container.add(messageReceived,gbc);

        gbc.gridy = 3;
        Button sndButton = createSendButton();
        container.add(sndButton);

        return container;
    }

    private JList<String> createCandidatesJList(JList jList) {
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setFont(TEXT_FONT);
        return jList;
    }

    private JList<String> createConsoleJList(JList jList) {
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setPreferredSize(new Dimension(400, 50));
        jList.setFont(TEXT_FONT);
        return jList;
    }

    private JTextField createMessageToSendJTextField() {
        messageToSend = new JTextField("messageToSend");
        messageToSend.setText("Mensagem a enviar");
        return messageToSend;
    }

    private JTextField createMessageReceivedJTextField() {
        messageReceived = new JTextField("messageReceived");
        messageReceived.setText("Mensagem a receber");
        return messageReceived;
    }

//    public void updateMessageReceivedJTextField(String msg) {
//        messageReceived.setText(msg);
//    }

    public synchronized void updateConsoleInformation(String update) {
        consoleInfoList.add(0, update);
        String[] consoleArray = new String[consoleInfoList.size()];
        console.setListData(consoleInfoList.toArray(consoleArray));
    }

    private Button createSendButton() {
        Button sendButton = new Button("Send to All");
        sendButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){

                server.sendMessageToAll(messageToSend.getText());
            }

        });
        return sendButton;
    }

    private Button createBeginElectionButton() {
        Button beginElectionButton = new Button("Cálculo do Resultado");
        beginElectionButton.setFont(BUTTON_FONT);
        beginElectionButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if (server.isCandidatesEmpty()) {
                    server.informMessage("Erro de Dados",
                            "Em primeiro lugar inicie a votação ", JOptionPane.ERROR_MESSAGE);
                }else if (server.isVotesEmpty()) {
                    server.informMessage("Erro de Dados",
                            "Não existem votos para iniciar a votação.", JOptionPane.ERROR_MESSAGE);
                }else if (server.isVotesEmpty()) {
                    server.informMessage("Erro de Dados",
                            "Não existem votos para iniciar a votação.", JOptionPane.ERROR_MESSAGE);
                }

                else if (server.getVotesReceived() > server.getBallotsSent()){
                    server.informMessage("Erro Grave",
                            "Ligação comprometida!!! Número de votos superior ao de boletins enviados."
                                ,JOptionPane.ERROR_MESSAGE);
                } else {
                    String content = "";
                    if(server.getVotesReceived() < server.getBallotsSent())
                        content = "Ainda faltam receber votos!!! Tem a certeza que deseja iniciar a eleição?";
                    else content = "Tem a certeza que deseja iniciar a eleição?";

                    if (server.confirmDialog("Iniciar Eleição?", content)==JOptionPane.OK_OPTION){
                        server.beginElection();
                    }

                }
            }
        });
        return beginElectionButton;
    }


    private Button createBeginVotingButton() {
        Button beginVotingButton = new Button("Iniciar Votação");
        beginVotingButton.setFont(BUTTON_FONT);
        beginVotingButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                server.clearData();
                String[] candidatesArray = LoadData.loadArray();
                if (candidatesArray!=null) {
                    candidatesJList.setListData(candidatesArray);
                    server.setCandidates(candidatesArray);
                    server.setElectionID();
                }
            }
        });
        return beginVotingButton;
    }

    public synchronized void updateElectionResult(String[] data) {
        electionResultJList.setListData(data);
        electionResultScrollPane.setViewportView(electionResultJList);
    }

    public synchronized void updateCandidatesJList(String[] data) {
        candidatesJList.setListData(data);
        candidatesScrollPane.setViewportView(candidatesJList);
    }

    public synchronized void updateBallotsSentField(int ballotsSent) {
        ballotsSentField.setText(Integer.toString(ballotsSent));
    }

    public synchronized void updateVotesReceivedField(int votesReceived) {
        votesReceivedField.setText(Integer.toString(votesReceived));
    }



    public void setServer(Server server){this.server = server; }

    public void setVisible(Boolean visible) {frame.setVisible(visible);}




}

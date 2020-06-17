import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
*HumanClientGUI. Allow a human player to connect to the main server and communicate with it
* 			  in order to play the game and send messages to other players.
*
*@version 1.6
*@release 26/03/17
*
*Code adapted from http://makemobiapps.blogspot.co.uk/p/multiple-client-server-chat-programming.html
*and from https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
*/
public class HumanClientGUI implements Runnable {
	
	private static boolean closed = false;
	private static Socket hpSocket = null;
	private static PrintStream out = null;
	private static BufferedReader in = null;
	private static BufferedReader userInput = null;
	private String inputToServer;
	private String nameInput;

	private boolean connectionMade = false;
	boolean quit = false;

    private JFrame myFrame;
    private JPanel panel1, panel2, panel3, panel4;

    private JTextPane currentIpAndPort;

    private JTextField newIPEntry, newPortEntry;
    private JLabel newServerTitle;
    private JButton newServerButton;

    private JLabel playerControlTitle;

    private JPanel playerControl;
    private JButton helloButton, lookButton, pickupButton, quitButton;
    private JLabel blankSq1, blankSq2, blankSq3, blankSq4,blankSq5,blankSq6, blankSq7;
    private JButton moveNButton, moveWButton, moveSButton, moveEButton;

    private JLabel commandInfoTitle;
    private JTextPane commandInfo;

    private JLabel chatTitle;
    private JTextArea chatArea;
    private JScrollPane chatScroll, chatEntryScroll;
    private JTextArea chatEntry;

    private JButton shoutButton, whisperButton;
    private JPanel whisperPanel;
    private JTextField whisperTo;

    /**
     *Runs all the methods that builds the GUI for the player.
     */
    private void buildHumanGUI(){
        createWindow();
        ipAndPortDisplay();
        newServerEntryDisplay();
        newServerButtonDisplay();
        playerControlTitleDisplay();
        playerControlDisplay();
        commandInfoDisplay();
        chatPaneDisplay();
        shoutAndWhisperDisplay();

        panelSetting();
        placePanels();
        lookViewBlank();
        displayWindow();
    }

    /**
     *Creates the main frame for the rest of the GUI components to attach to.
     */
    private void createWindow(){
        myFrame = new JFrame("Dungeons of Doom");
        myFrame.setSize(new Dimension(1150,800));
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     *Sets up all the panels which are attached to the main frame.
     * GUI components then added to each panel to be placed in the correct place.
     */
    private void panelSetting(){
        panel1 = new JPanel();
        panel1.setBackground(Color.black);
        panel1.setPreferredSize(new Dimension(650, 650));


        panel2 = new JPanel();
        panel2.setBackground(Color.black);
        panel2.setPreferredSize(new Dimension(1150, 150));
        ImageIcon title = new ImageIcon("DODHumanTitle.png");
        JLabel titleImage = new JLabel(title);
        panel2.add(titleImage);

        panel3 = new JPanel();
        panel3.setBackground(Color.black);
        panel3.setPreferredSize(new Dimension(250, 650));
        panel3.add(currentIpAndPort);
        panel3.add(newServerTitle);
        panel3.add(newIPEntry);
        panel3.add(newPortEntry);
        panel3.add(newServerButton);
        panel3.add(playerControlTitle);
        panel3.add(playerControl);
        panel3.add(commandInfoTitle);
        panel3.add(commandInfo);

        panel4 = new JPanel();
        panel4.setBackground(Color.black);
        panel4.setPreferredSize(new Dimension(250, 650));
        panel4.add(chatTitle);
        panel4.add(chatScroll);
        panel4.add(chatEntryScroll);
        panel4.add(shoutButton);
        panel4.add(whisperPanel);

    }

    /**
     *Places the 4 main panels into the main frame and positioning them.
     */
    private void placePanels(){
        myFrame.getContentPane().add(panel1, BorderLayout.CENTER);
        myFrame.getContentPane().add(panel2, BorderLayout.PAGE_START);
        myFrame.getContentPane().add(panel3, BorderLayout.LINE_START);
        myFrame.getContentPane().add(panel4, BorderLayout.LINE_END);
    }

    /**
     *Creation of a text area displaying the current IP and port that the client is connected to.
     */
    private void ipAndPortDisplay(){
        currentIpAndPort = new JTextPane();
        currentIpAndPort.setPreferredSize(new Dimension(240,55));
        currentIpAndPort.setBackground(new Color(207,210,214));
        currentIpAndPort.setForeground(Color.black);
        currentIpAndPort.setText("Current IP: \n\nCurrent Port: ");
        currentIpAndPort.setEditable(false);
    }

    /**
     *Sets the IP and Port in the display to the correct values.
     */
    private void setIpAndPort(String ip, String port){
        currentIpAndPort.setText("Current IP: " + ip + "\n\nCurrent Port: " + port);
    }

    /**
     *Creates two text fields which the user can enter a new server and port to connect to.
     */
    private void newServerEntryDisplay(){
        newServerTitle = new JLabel("Enter a New IP And Port Below: ");
        newServerTitle.setForeground(new Color(207,210,214));
        newServerTitle.setPreferredSize(new Dimension(240,40));
        newServerTitle.setVerticalAlignment(SwingConstants.BOTTOM);

        newIPEntry = new JTextField();
        newIPEntry.setPreferredSize(new Dimension(240,30));
        newIPEntry.setBackground(new Color(207,210,214));
        newIPEntry.setForeground(Color.black);
        newIPEntry.setToolTipText("Enter IP...");

        newPortEntry = new JTextField();
        newPortEntry.setPreferredSize(new Dimension(240,30));
        newPortEntry.setBackground(new Color(207,210,214));
        newPortEntry.setForeground(Color.black);
        newPortEntry.setToolTipText("Enter Port...");

    }

    /**
     *Creation of a button which when pressed will take what has been entered into the text fields above it
     * and try to connect to a server running on that IP and port.
     */
    private void newServerButtonDisplay(){
        //Creation of the button
        newServerButton = new JButton("Join New Server");
        newServerButton.setPreferredSize(new Dimension(150,30));

        //actions taken for when the button is pressed.
        newServerButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                try {
                    if(connectionMade == true){
                        out.close();
                        in.close();
                        hpSocket.close();
                    }
                    quit = true;
                    setIpAndPort(newIPEntry.getText(), newPortEntry.getText());
                    connectToServer(Integer.valueOf(newPortEntry.getText()), newIPEntry.getText());
                }catch(IOException f){
                    JOptionPane.showMessageDialog(null, "There was an error. Try again.", "InfoBox: " + "Server Connection Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    /**
     *Title for the play controls.
     */
    private void playerControlTitleDisplay(){
        playerControlTitle = new JLabel("Player Commands:");
        playerControlTitle.setForeground(new Color(207,210,214));
        playerControlTitle.setPreferredSize(new Dimension(240,50));
        playerControlTitle.setVerticalAlignment(SwingConstants.BOTTOM);
        playerControlTitle.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     *Creates a location for the player command buttons to attach to.
     * All the components for player control are then added.
     */
    private void playerControlDisplay(){
        playerControl = new JPanel();
        playerControl.setPreferredSize(new Dimension(240, 240));
        playerControl.setBackground(Color.black);

        GridLayout gridL = new GridLayout(5,3);
        playerControl.setLayout(gridL);

        createPlayerControlDisplay();
        playerControl.add(blankSq1);
        playerControl.add(moveNButton);
        playerControl.add(blankSq2);
        playerControl.add(moveWButton);
        playerControl.add(moveSButton);
        playerControl.add(moveEButton);
        playerControl.add(blankSq3);
        playerControl.add(blankSq4);
        playerControl.add(blankSq5);
        playerControl.add(helloButton);
        playerControl.add(blankSq6);
        playerControl.add(lookButton);
        playerControl.add(pickupButton);
        playerControl.add(blankSq7);
        playerControl.add(quitButton);

    }

    /**
     * Creates the components which will be attached to the player control
     */
    private void createPlayerControlDisplay(){
        createNonMoveButtons();
        createMoveButtons();

        //Blanks labels used to fill in the spaces as using a grid layout manager for the player control panel
        blankSq1 = new JLabel();
        blankSq1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        blankSq2 = new JLabel();
        blankSq2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        blankSq3 = new JLabel();
        blankSq3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        blankSq4 = new JLabel();
        blankSq4.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        blankSq5 = new JLabel();
        blankSq5.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        blankSq6 = new JLabel();
        blankSq6.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        blankSq7 = new JLabel();
        blankSq7.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /**
     * Creation of the non-movement player control buttons.
     * When pressed that action is sent to the server and handled.
     */
    private void createNonMoveButtons(){
        helloButton = new JButton("Hello");
        helloButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        helloButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                inputToServer = "HELLO";
                out.println(inputToServer);
            }
        });

        lookButton = new JButton("Look");
        lookButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lookButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                //lookViewDisplay();

                inputToServer = "LOOK";
                out.println(inputToServer);
            }
        });

        pickupButton = new JButton("PickUp");
        pickupButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pickupButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                inputToServer = "PICKUP";
                out.println(inputToServer);
            }
        });

        quitButton = new JButton("Quit");
        quitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        quitButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                inputToServer = "QUIT";
                out.println(inputToServer);
                lookViewBlank();
                JOptionPane.showMessageDialog(null, "You have successfully quit from the server.", "InfoBox: " + "Quit.", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * Creation of the Movement player control buttons.
     * When pressed that action is sent to the server and handled.
     * When pressed the look view is blanked so not to make the look command redundant.
     */
    private void createMoveButtons(){
        moveNButton = new JButton("North");
        moveNButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        moveNButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                lookViewBlank();
                inputToServer = "MOVE N";
                out.println(inputToServer);
            }
        });

        moveWButton = new JButton("West");
        moveWButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        moveWButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                lookViewBlank();
                inputToServer = "MOVE W";
                out.println(inputToServer);
            }
        });

        moveSButton = new JButton("South");
        moveSButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        moveSButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                lookViewBlank();
                inputToServer = "MOVE S";
                out.println(inputToServer);
            }
        });

        moveEButton = new JButton("East");
        moveEButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        moveEButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                lookViewBlank();
                inputToServer = "MOVE E";
                out.println(inputToServer);
            }
        });
    }

    /**
     * Creation of the display for when the look button is pressed.
     * Creates a 5x5 grid from a string of the map sent by the server.
     * The grid is in different colours depending what is at each location.
     */
    private void lookViewDisplay(String lookFromServer){
        char[][] lookViewGrid = new char[5][5];

        int c = 0;

        for(int i = 0; i < 30; i++){
            for(int j = 0; j < 5; j++){
                lookViewGrid[j][c] = lookFromServer.charAt(i);
                i++;
            }
            c++;
            if(c == 5){
                c = 0;
            }
        }

        panel1.removeAll();
        JLabel[][] grid = new JLabel[5][5];
        GridLayout gridL = new GridLayout(5,5);
        panel1.setLayout(gridL);

        for (int j = 0; j < 5; j++){
            for (int i = 0; i < 5; i++){
                grid[i][j] = new JLabel();
                grid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                if(i == 2 && j == 2){
                    grid[i][j].setBackground(Color.green);//Player themselves

                }else if(lookViewGrid[i][j] == '#'){
                    grid[i][j].setBackground(Color.darkGray);//WALLS

                }else if(lookViewGrid[i][j] == 'E'){
                    grid[i][j].setBackground(Color.blue);//EXITS

                }else if(lookViewGrid[i][j] == 'G'){
                    grid[i][j].setBackground(Color.yellow);//GOLD

                }else if(lookViewGrid[i][j] == 'P'){
                    grid[i][j].setBackground(Color.MAGENTA);//OTHER PLAYERS

                }else if(lookViewGrid[i][j] == 'B'){
                    grid[i][j].setBackground(Color.red);//BOTS

                }else if(lookViewGrid[i][j] == '.'){
                    grid[i][j].setBackground(Color.lightGray);//EMPTY SPACES
                }

                grid[i][j].setOpaque(true);

                panel1.add(grid[i][j]);
            }
        }
        panel1.updateUI();
    }

    /**
     * Blanking the look view display for when movement buttons are pressed or when the client disconnects from a server.
     * Creates a 5x5 grid of dark grey squares with a green center as the player.
     */
    private void lookViewBlank(){
        panel1.removeAll();
        JLabel[][] grid = new JLabel[5][5];
        GridLayout gridL = new GridLayout(5,5);
        panel1.setLayout(gridL);

        for (int j = 0; j < 5; j++){
            for (int i = 0; i < 5; i++){
                grid[i][j] = new JLabel();
                grid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                if(i == 2 && j == 2){
                    grid[i][j].setBackground(Color.green);
                }else {
                    grid[i][j].setBackground(Color.darkGray);
                }
                grid[i][j].setOpaque(true);

                panel1.add(grid[i][j]);
            }
        }
        panel1.updateUI();
    }

    /**
     * Creation of a small text display to display the outcome of commands.
     */
    private void commandInfoDisplay(){
        commandInfoTitle = new JLabel("Command Response:");
        commandInfoTitle.setForeground(new Color(207,210,214));
        commandInfoTitle.setPreferredSize(new Dimension(240,40));
        commandInfoTitle.setVerticalAlignment(SwingConstants.BOTTOM);
        commandInfoTitle.setHorizontalAlignment(SwingConstants.CENTER);

        commandInfo = new JTextPane();
        commandInfo.setPreferredSize(new Dimension(240,95));
        commandInfo.setBackground(new Color(207,210,214));
        commandInfo.setForeground(Color.black);
        commandInfo.setEditable(false);
    }

    /**
     * Creation of the chat display updating in real time.
     * Also a text field for players to write messages to send.
     */
    private void chatPaneDisplay(){
        chatTitle = new JLabel("Chat:");
        chatTitle.setForeground(new Color(207,210,214));
        chatTitle.setPreferredSize(new Dimension(240,15));
        chatTitle.setVerticalAlignment(SwingConstants.BOTTOM);
        chatTitle.setHorizontalAlignment(SwingConstants.CENTER);

        chatArea = new JTextArea();
        chatArea.setPreferredSize(new Dimension(240, 500));
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(207,210,214));
        chatArea.setForeground(Color.black);
        chatArea.setLineWrap(true);

        //Making the chat disaply scrollable
        chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(240, 425));
        chatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


        chatEntry = new JTextArea("Enter Message...");
        chatEntry.setPreferredSize(new Dimension(240, 100));
        chatEntry.setBackground(new Color(207,210,214));
        chatEntry.setForeground(Color.black);
        chatEntry.setLineWrap(true);

        chatEntryScroll = new JScrollPane(chatEntry);
        chatEntryScroll.setPreferredSize(new Dimension(240, 100));
        chatEntryScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Creations of buttons to choose how messages are wanting to be sent by the user.
     * Also a text field to input a players name for when private messages are sent between players.
     */
    private void shoutAndWhisperDisplay(){
        shoutButton = new JButton("SHOUT");
        //actions for shout button
        shoutButton.setPreferredSize(new Dimension(240,30));
        shoutButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                inputToServer = "SHOUT " + chatEntry.getText();
                out.println(inputToServer);
            }
        });

        whisperPanel = new JPanel();
        whisperPanel.setPreferredSize(new Dimension(240, 40));
        whisperPanel.setBackground(Color.black);
        FlowLayout flowL = new FlowLayout();
        whisperPanel.setLayout(flowL);

        whisperTo = new JTextField("To...");
        whisperTo.setPreferredSize(new Dimension(135,30));
        whisperTo.setForeground(Color.black);
        whisperTo.setBackground(new Color(207,210,214));

        whisperButton = new JButton("Whisper");
        whisperButton.setPreferredSize(new Dimension(95,30));
        //actions for whisper button
        whisperButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                inputToServer = "WHISPER @" + whisperTo.getText() + " "+ chatEntry.getText();
                System.out.println(inputToServer);
                out.println(inputToServer);
            }
        });

        whisperPanel.add(whisperTo);
        whisperPanel.add(whisperButton);
    }

    /**
     *Displays the GUI to the user running the program.
     */
    private void displayWindow(){
        myFrame.pack();
        myFrame.setResizable(false);
        myFrame.setVisible(true);
    }

    /**
     *Method dealing with connecting the client to the server.
     * If connection is successful, a thread is run to deal with reading from the server.
     */
    private void connectToServer(int portNumber, String hostName){
        System.out.println("Trying to connect to " + hostName + ":" + portNumber + " ...");

        try {//Client attempts to connect to the server at host entered on the post entered.
            hpSocket = new Socket(hostName, portNumber);
            //Setting up input and output streams to the server
            userInput = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintStream(hpSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(hpSocket.getInputStream()));

            //Tells the server this is a human player connecting.
            out.println("player");

            //Gets the user to input a name.
            while(true) {
                if(quit == true){
                    myFrame.setVisible(false);
                    myFrame.dispose();
                }
                quit = false;
                nameInput = JOptionPane.showInputDialog("What is your name");
                if(!nameInput.equals("bot")){
                    if(nameInput.indexOf('@') == -1) {
                        break;
                    }
                }

            }
            out.println(nameInput);
            connectionMade = true;
            JOptionPane.showMessageDialog(null, "\nWelcome To Dungeons of DOOM \n You have now entered the dungeon " + nameInput, "InfoBox: " + "Dungeons Of Doom", JOptionPane.INFORMATION_MESSAGE);

        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Do not know about the host.", "InfoBox: " + "Server Connection Error", JOptionPane.INFORMATION_MESSAGE);
            connectionMade = false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't get I/O for the connection to the host.", "InfoBox: " + "Server Connection Error", JOptionPane.INFORMATION_MESSAGE);
            connectionMade = false;
        } catch (IllegalArgumentException e){
            JOptionPane.showMessageDialog(null, "The port supplied is out of range.", "InfoBox: " + "Server Connection Error", JOptionPane.INFORMATION_MESSAGE);
            connectionMade = false;
        }

        //Deals with inputting to the server
        if (hpSocket != null && out != null && in != null) {
            try{
                new Thread(new HumanClientGUI()).start();//creates an thread specifically to read from the server
                while(!closed){
                    //Client socket output stream to input to the server
                    //no necessary when GUI is in use.
                    out.println(userInput.readLine().trim());

                }

                out.close();
                in.close();
                hpSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }

        }
    }

    //Thread created to read from the server
    public void run(){
        String responseLine;
        String commandMessage;
        buildHumanGUI();
        setIpAndPort("localhost", String.valueOf(2222));
        try {
            while ((responseLine = in.readLine()) != null) {//Listens for a stream from the server

                /**
                 * Handles what happens depending on the response back from the server after a command is pressed.
                 */
                if(responseLine.equals("LOOK")){
                    commandInfo.setText("Command: " + responseLine);
                    String lookString = "";
                    for(int i = 0; i < 5; i++) {
                        lookString += in.readLine();
                        lookString += "\n";
                    }
                    lookViewDisplay(lookString);

                }else if(responseLine.contains("MOVE ")){
                    commandMessage = in.readLine();
                    if(!commandMessage.startsWith("SUCCESS, Congratulations")) {
                        commandInfo.setText("Command: " + responseLine + "\nThe move was a " + commandMessage);
                    }else{
                        commandInfo.setText(commandMessage);
                        JOptionPane.showMessageDialog(null, "You have won the game, congratulations.\nYou have now been disconnecting from the server.", "InfoBox: " + "Congratulations.", JOptionPane.INFORMATION_MESSAGE);
                    }
                }else if(responseLine.contains("HELLO")){
                    commandMessage = in.readLine();
                    commandInfo.setText("Command: " + responseLine + "\nRequired " + commandMessage);

                }else if(responseLine.contains("PICKUP")){
                    commandMessage = in.readLine();
                    commandInfo.setText("Command: " + responseLine + "\nPickup was a " + commandMessage);

                }else if(responseLine.startsWith("<")){
                    commandMessage = in.readLine();
                    chatArea.append(commandMessage + "\n");
                    chatEntry.setText("Enter Message...");
                    whisperTo.setText("To...");

                }else if(responseLine.startsWith("Lookout!") || responseLine.startsWith("The user") || responseLine.startsWith("*** Bye")){
                    chatArea.append(responseLine + "\n");
                }

                if (responseLine.indexOf("*** Bye") != -1)
                    break;//closes when disconnecting from server

            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

    /**
     *Main method, dealing with taking the values inputted at command line when the program is run.
     * Calls building the GUI and initial connection to a server.
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java HumanClientGUI <host name> <port number>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        HumanClientGUI HumanClient = new HumanClientGUI();
        //HumanClient.buildHumanGUI();
        //HumanClient.setIpAndPort(host, args[1]);
        HumanClient.connectToServer(port, host);
    }
}

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


/**
*DODServerGUI. Creates a server on the port number specified and deals with a clients
*			initial request to join the server.
*
*@version 1.8
*@release 26/03/17
*@see. DODMultiServerThread.java, GameLogic.java
*
*Code adapted from http://makemobiapps.blogspot.co.uk/p/multiple-client-server-chat-programming.html
*and from https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
*/
public class DODServerGUI{
	
	private static final int maxClientsCount = 10;
	private static final DODMultiServerThread[] threads = new DODMultiServerThread[maxClientsCount];

    private JFrame myFrame;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;

    private JTextPane currentIpAndPort;
    private JTextField newPortEntry;
    private JLabel newPortTitle;
    private JButton newPortButton;

    private JTextPane updateGodViewTitle;
    private JButton updateGodViewButton;

    private JTextPane toggleGodViewTitle;
    private JButton toggleGodViewButton;

    private ServerSocket serverSocket;

    private GameLogic game;

    private boolean toggle = true;
    private boolean firstUpdate = true;

    private boolean portChange = false;
    private int newPort;

    private boolean listenToPort = true;

    /**
     *Runs all the methods that builds the GUI for the server
     */
    private void buildServerGUI(){
        createWindow();
        ipAndPortDisplay();
        newPortEntryDisplay();
        newPortButtonDisplay();
        updateGodViewDisplay();
        toggleGodViewDisplay();

        panelSetting();
        placePanels();
        displayWindow();
    }

    /**
     *Creates the main frame for the rest of the GUI components to attach to
     */
    private void createWindow(){
        myFrame = new JFrame("Dungeons of Doom Server Control");
        myFrame.setSize(new Dimension(900 , 800));
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
        panel2.setPreferredSize(new Dimension(750, 150));
        ImageIcon title = new ImageIcon("DODServerTitle.png");
        JLabel titleImage = new JLabel(title);
        panel2.add(titleImage);

        panel3 = new JPanel();
        panel3.setBackground(Color.black);
        panel3.setPreferredSize(new Dimension(250, 650));
        panel3.add(currentIpAndPort);
        panel3.add(newPortTitle);
        panel3.add(newPortEntry);
        panel3.add(newPortButton);
        panel3.add(updateGodViewTitle);
        panel3.add(updateGodViewButton);
        panel3.add(toggleGodViewTitle);
        panel3.add(toggleGodViewButton);
    }

    /**
     *Places the 3 main panels into the main frame and positioning them.
     */
    private void placePanels(){
        myFrame.getContentPane().add(panel1, BorderLayout.CENTER);
        myFrame.getContentPane().add(panel2, BorderLayout.PAGE_START);
        myFrame.getContentPane().add(panel3, BorderLayout.LINE_START);
    }

    /**
     *Creation of a text area displaying the current IP and port that the server is running on.
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
     *Creates a text entry for a new port that the server wants to run on.
     */
    private void newPortEntryDisplay(){
        newPortTitle= new JLabel("Enter a New Port: ");
        newPortTitle.setForeground(new Color(207,210,214));
        newPortTitle.setPreferredSize(new Dimension(240,40));
        newPortTitle.setVerticalAlignment(SwingConstants.BOTTOM);

        newPortEntry = new JTextField();
        newPortEntry.setPreferredSize(new Dimension(240,30));
        newPortEntry.setBackground(new Color(207,210,214));
        newPortEntry.setForeground(Color.black);
    }

    /**
     *Creation of a button which when pressed will take what has been entered into the text field above it
     * and try to run the server on the new port.
     */
    private void newPortButtonDisplay(){
        //Creation of the button
        newPortButton = new JButton("Set New Port");
        newPortButton.setPreferredSize(new Dimension(150,30));

        //actions taken for when the button is pressed.
        newPortButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    newPort = Integer.valueOf(newPortEntry.getText());
                    setIpAndPort("DODServerGUI", String.valueOf(newPort));
                    try{
                        if(listenToPort){
                            serverSocket.close();
                        }
                        portChange = true;
                        JOptionPane.showMessageDialog(null, "The server is now trying to run on port " + newPort, "InfoBox: " + "Port Change Attempt", JOptionPane.INFORMATION_MESSAGE);
                    } catch(IOException f){
                        System.out.println(f);
                    }
                }catch(NumberFormatException g){
                    JOptionPane.showMessageDialog(null, "The port entered must be a number. Try again.", "InfoBox: " + "Port Change Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    /**
     *Creation of a button which when pressed will update the god view button.
     */
    private void updateGodViewDisplay(){
        //Title of button
        updateGodViewTitle= new JTextPane();
        updateGodViewTitle.setText("\n\nPress the button to update the God View");
        updateGodViewTitle.setForeground(new Color(207,210,214));
        updateGodViewTitle.setBackground(Color.black);
        updateGodViewTitle.setPreferredSize(new Dimension(240,55));
        updateGodViewTitle.setEditable(false);

        //Code adapted allowing for the text in the title JTextPane to be centered
        //http://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment
        StyledDocument style = updateGodViewTitle.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        style.setParagraphAttributes(0, style.getLength(), center, false);

        //Creation of the button
        updateGodViewButton = new JButton("God View");
        updateGodViewButton.setPreferredSize(new Dimension(150,30));

        firstUpdate = true;

        //actions taken for when the button is pressed
        updateGodViewButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(toggle == true) {//Only updates if the toggle is on
                    godViewGridDisplay();
                    firstUpdate = false;
                }
            }
        });
    }

    /**
     *Creation of a button which when pressed will toggle the view of the god view.
     */
    private void toggleGodViewDisplay(){
        //Title of button
        toggleGodViewTitle= new JTextPane();
        toggleGodViewTitle.setText("\n\nPress the eye to toggle the God View");
        toggleGodViewTitle.setForeground(new Color(207,210,214));
        toggleGodViewTitle.setBackground(Color.black);
        toggleGodViewTitle.setPreferredSize(new Dimension(240,55));
        toggleGodViewTitle.setEditable(false);

        //Code adapted allowing for the text in the title JTextPane to be centered
        //http://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment
        StyledDocument style = toggleGodViewTitle.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        style.setParagraphAttributes(0, style.getLength(), center, false);

        //Added an image to the button
        ImageIcon GodViewIcon = new ImageIcon("GodViewImage.png");
        //Creation of the button
        toggleGodViewButton = new JButton( GodViewIcon);
        toggleGodViewButton.setPreferredSize(new Dimension(200,98));
        toggleGodViewButton.setBackground(new Color(207,210,214));

        //Actions taken for when the button is pressed.
        toggleGodViewButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(firstUpdate == false){//Only toggles if the god view has been updated for a first time.
                    if(toggle == true){
                        blankGodView();
                        toggle = false;
                    }else{
                        godViewGridDisplay();
                        toggle = true;
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Update the God View first before toggling", "InfoBox: " + "Toggle God View Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    /**
     *Method to blank the god view.
     * Creates a grid of JLabels all set to the colour grey.
     */
    private void blankGodView(){
        panel1.removeAll();

        int rows = game.mapHeight();
        int cols = game.mapWidth();
        JLabel[][] grid= new JLabel[rows][cols];

        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                grid[i][j] = new JLabel();
                grid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                grid[i][j].setBackground(Color.darkGray);
                grid[i][j].setOpaque(true);
                panel1.add(grid[i][j]);
            }
        }

        panel1.updateUI();
    }

    /**
     *Method to create the god view when not blank.
     * Creates a grid of JLabels all set to the specific colours depending on whats on the map.
     */
    private void godViewGridDisplay(){
        panel1.removeAll();

        int rows = game.mapHeight();
        int cols = game.mapWidth();

        JLabel[][] grid = new JLabel[cols][rows];

        GridLayout gridL = new GridLayout(rows, cols);
        panel1.setLayout(gridL);

        for (int j = 0; j < rows; j++){
            for (int i = 0; i < cols; i++){
                grid[i][j] = new JLabel();
                grid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                if(game.gridView()[i][j] == '#'){//WALLS
                    grid[i][j].setBackground(Color.darkGray);

                }else if(game.gridView()[i][j] == 'E'){//EXITS
                    grid[i][j].setBackground(Color.blue);

                }else if(game.gridView()[i][j] == 'G'){//GOLD
                    grid[i][j].setBackground(Color.yellow);

                }else if(game.gridView()[i][j] == 'P') {//Players
                    grid[i][j].setBackground(Color.GREEN);

                }else if(game.gridView()[i][j] == 'B'){//Bots
                    grid[i][j].setBackground(Color.red);

                }else if(game.gridView()[i][j] == '.'){//Empty Space
                    grid[i][j].setBackground(Color.lightGray);
                }

                grid[i][j].setOpaque(true);
                panel1.add(grid[i][j]);
            }
        }
        panel1.updateUI();
    }

    /**
     *Sets the IP and Port in the display to the correct values.
     */
    private void setIpAndPort(String ip, String port){
        currentIpAndPort.setText("Current IP: " + ip + "\n\nCurrent Port: " + port);
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
     *Method dealing with the setup of the server, and connection of clients.
     */
    private void serverSetup(int portNumber, boolean listening, GameLogic game){
        newPort = portNumber;
        while(true) {
            while(!listenToPort && !portChange){
                System.out.println("Waiting for new port");
            }
            try {
                serverSocket = new ServerSocket(newPort);

                System.out.println("DOD-Server: Running on port " + serverSocket.getLocalPort());

                //Server continuously listens on the server port
                while (listening) {
                    Socket socket = serverSocket.accept();
                    //Server creates new server threads for each client connection
                    int i = 0;
                    for (i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new DODMultiServerThread(socket, game, threads)).start();
                            break;
                        }
                    }
                    //if the the number of client connections attempts to exceed 10, it will inform the client the server is too busy.
                    if (i == maxClientsCount) {
                        PrintStream out = new PrintStream(socket.getOutputStream());
                        out.println("Server too busy. Try later.\n");
                        out.close();
                        socket.close();
                    }
                }
            } catch (IOException e) {
                if (portChange == true) {
                    portChange = false;
                } else {
                    listenToPort = false;
                    System.err.println("Could not listen on port " + newPort);
                    JOptionPane.showMessageDialog(null, "Could not listen on port " + portNumber + ". Enter a new port.", "InfoBox: " + "Server Port Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    /**
     *Setting up the game so it can be feed to connecting clients.
     */
    private GameLogic gameSetup(){
        game = new GameLogic();
        blankGodView();
        return game;
    }

    /**
     *Main method, dealing with taking the values inputted at command line when the program is run.
     * Calls building the GUI and setting the server up.
     */
	public static void main(String[] args){
		
		if (args.length != 1)
		{
            System.err.println("Usage: java DODServerGUI <port number>");
            System.exit(1);
        }
		
		int portNumber = Integer.parseInt(args[0]);
		boolean listening = true;

        DODServerGUI DODServer = new DODServerGUI();
        DODServer.buildServerGUI();
        DODServer.setIpAndPort("DODServerGUI", args[0]);
		//Instantiating the GameLogic which sets up the game to be played by clients

		DODServer.serverSetup(portNumber, listening, DODServer.gameSetup());

	}
}

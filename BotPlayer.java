import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
*Bot. Controls how the bot works, with a small GUI allowing for a disconnection from the server to take place.
*
*@version 1.4
*@release 26/07/2016
*/
public class BotPlayer {

	private Random random;
	private static final char [] DIRECTIONS = {'N','S','E','W'};
	private boolean quit = false;

    private JFrame myFrame;
    private JPanel panel1;
    private JLabel botTitle;
    private JButton disconnectButton;

    /**
     * Constructor, builds the GUI for the bot.
     */
    public BotPlayer(){
		random = new Random();
		buildBotGUI();
	}

    /**
     *Runs all the methods that builds the GUI for the bot.
     */
    private void buildBotGUI(){
        createWindow();
        titleAndButtonDisplay();
        panelSetting();
        placePanels();
        displayWindow();
    }

    /**
     *Creates the main frame for the rest of the GUI components to attach to
     */
    private void createWindow(){
        myFrame = new JFrame("Dungeons of Doom");
        myFrame.setSize(new Dimension(300,100));
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     *Sets up all the panels which are attached to the main frame.
     * GUI components then added to each panel to be placed in the correct place.
     */
    private void panelSetting(){
        panel1 = new JPanel();
        panel1.setBackground(Color.black);
        panel1.setPreferredSize(new Dimension(300, 100));

        FlowLayout flowL = new FlowLayout();
        panel1.setLayout(flowL);
        panel1.add(botTitle);
        panel1.add(disconnectButton);
    }

    /**
     *Places the main panel into the main frame and positions it.
     */
    private void placePanels(){
        myFrame.getContentPane().add(panel1, BorderLayout.CENTER);
    }

    /**
     * Creates a title for the GUI and a button.
     * When the button is pressed the Bot will disconnect from the server.
     */
    private void titleAndButtonDisplay(){
        botTitle = new JLabel("Bot Client: ");
        botTitle.setForeground(new Color(207,210,214));
        botTitle.setPreferredSize(new Dimension(100,100));
        botTitle.setVerticalAlignment(SwingConstants.CENTER);

        disconnectButton = new JButton("Disconnect");
        disconnectButton.setVerticalAlignment(SwingConstants.CENTER);
        disconnectButton.setPreferredSize(new Dimension(150,30));
        disconnectButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                quit = true;
            }
        });
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
     *Selects the next action the bot will perform. Picks a random direction
     */
    public String getNextAction() {
        String action;
        if(quit == false) {//If button has been pressed QUIT will begin sending.
            action = "MOVE " + DIRECTIONS[random.nextInt(4)];
        }
        else{
            action = "QUIT";
        }
    	return action;
    }
}
import java.io.*;
import java.net.*;

/**
*BotClient. Allow a bot clients to connect to the main server and communicate with it
* 			in order to play the game.
*
*@version 1.6
*@release 26/03/17
*@see, BotPlayer.java
*
*Code adapted from http://makemobiapps.blogspot.co.uk/p/multiple-client-server-chat-programming.html
*and from https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
*/
public class BotClient {
	
	private static Socket botSocket = null;
	private static PrintStream out = null;

    /**
     *Main method, dealing with taking the values inputted at command line when the program is run.
     * Connects the bot to the server, and gets the bots moves from BotPlayer.java
     */
	public static void main(String[] args){
		if (args.length != 2) {
            System.err.println("Usage: java BotClient <host name> <port number>");
			System.exit(1);
        }
		BotPlayer botPlayer = new BotPlayer();
		String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
		
	     
        System.out.println("Client trying to connect to " + hostName + ":" + portNumber + " ...");

        try{//Client attempts to connect to the server at host entered on the post entered.
            botSocket = new Socket(hostName, portNumber);
			//Setting up output streams to the server, reading from the server not needed.
            out = new PrintStream(botSocket.getOutputStream());
			//Tells the server this is a bot client connecting.
			out.println("bot");
        }catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }

        int moves = 0;
        boolean disconnect = false;
		while(!disconnect){//BotClient keeps selecting random movement directions from BotPlayer
				String botAction = botPlayer.getNextAction();

				if(!botAction.equals("QUIT")) {
                        //Every 4 seconds the server accepts stream from BotClient
                        //Bot sends through 500 moves which are stored by the server, then stops to free the output stream.
                        if(moves < 500) {
                            out.println(botAction);
                        }
                }else {
				    disconnect = true;
				    out.println(botAction);
                }
                moves++;
		}
		try {
            out.close();
            botSocket.close();
            System.exit(1);
        }catch(IOException e){
            System.exit(1);
        }
	}
}

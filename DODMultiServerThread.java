import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
*DODMultiServerThread. Handled each client connection in separate threads, controlled game actions of clients.
*
*@version 1.7
*@release 26/03/17
*@see. GameLogic.java
*
*Code adapted from http://makemobiapps.blogspot.co.uk/p/multiple-client-server-chat-programming.html
*and from https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
*/
public class DODMultiServerThread extends Thread {

	private Socket socket = null;
	private GameLogic game;
	private String[] clients;
	private String clientName = null;
	private final DODMultiServerThread[] threads;
	private int maxClientsCount;
	private PrintStream out;
	private BufferedReader in;
	
	/**
	 *Constructor. Make similar objects from DODServer.
	 *
	 *@param: socket : The socket the client connected from.
	 *		  game : GameLogic object.
	 *		  threads : The threads for each client connection.
	 */
	public DODMultiServerThread(Socket socket, GameLogic game, DODMultiServerThread[] threads) {
		super("DODMultiServerThread");
		this.socket = socket;
		this.game = game;
		this.threads = threads;
		maxClientsCount = threads.length;
		System.out.println("DOD-Server: Client Connected " + this.socket.getRemoteSocketAddress());
	}
	
	//Runs when a new thread of this class is created.
	public void run() {
		int maxClientsCount = this.maxClientsCount;
		DODMultiServerThread[] threads = this.threads;
		clients = new String[maxClientsCount];
        int clientIndex = 0;
		
		//Creates a string array to store the clients sockets.
		for(int i = 0; i != maxClientsCount; i++){
			if(threads[i] != null){
				clients[i] = threads[i].socket.toString();
			}
		}
		
		try {
			out = new PrintStream(socket.getOutputStream()); //Data stream to clients.
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Input stream from clients.
         		
			String inputLine;
			String name = "";
			boolean player = false;
			clientIndex = 0;
			
			//Determines whether the connected client is a player or a bot.
            inputLine = in.readLine();
			if(inputLine.equals("player")){
				player = true;
			}
			else if (inputLine.equals("bot")){
				player = false;
			}
		
			//Calculates which client number the client is.
			synchronized (this) {
				for(int i = 0; i < clients.length; i++){
					if(clients[i] != null && clients[i].equals(this.socket.toString())){
						clientIndex = i + 1;
					}
				}
			}
			
			//Player clients name themselves, whilst bots always called "bot"
			while (true) {
				//out.println("Enter your name:");
				if(player == true){
						name = in.readLine().trim();
				}else {
					name = "bot";
				}
                break;
				//Not required with GUI
				/*if ((name.indexOf('@') == -1)) {
					break;
				} else {
					out.println("The name should not contain '@' character.");
				}*/
			}
			
			//Informs all current connected clients a new client has joined.
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] == this) {
						clientName = "@" + name;
						break;
					}
				}
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].out.println("Lookout! " + name + " entered the dungeon...");
					}
				}
			}
			
			//Inserts a new client into the game logic, giving it a random starting position.
			synchronized (this){
				game.insertClient(player, clientIndex);
			}
			
			//Handling player interaction
			if(player == true){

			    //Not needed with GUI
				//out.println("\nWelcome To Dungeons of DOOM \n You have now entered the dungeon " +name + "\nTo talk to the rest of the players type SHOUT <message> \nTo send a private message to a player type WHISPER @<player_name> <message>");
				
				while (true) {
					inputLine = in.readLine();

					//Not needed with GUI
					/*if(inputLine.startsWith("QUIT")){//quit when QUIT entered.
						out.println(" PRESS ENTER TO LEAVE ");
						break;
					}*/

					//Allows players to privately message each other.
					if (inputLine.startsWith("WHISPER @")) {
						String[] words = inputLine.split("\\s", 3);//splits message apart from 'WHISPER' command.
						if (words.length > 1 && words[2] != null) {
							words[2] = words[2].trim();
							if (!words[2].isEmpty()) {

								synchronized (this) {
									for (int i = 0; i < maxClientsCount; i++) {

										if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
											//Private message shows who it is from.
											threads[i].out.println("<whisper from " + name + "> " + words[2]);
											//shows the sending client what they sent and to who.
											this.out.println("<whisper to " + threads[i].clientName + "> " + words[2]);
											break;
										}

									}
								}

							}
						}
						//Allows players to message globally.
					} else if (inputLine.startsWith("SHOUT ")) {
						synchronized (this) {
                            String[] words = inputLine.split("\\s", 2);
							for (int i = 0; i < maxClientsCount; i++) {//loop broadcasting the message to each thread.
								if (threads[i] != null && threads[i].clientName != null) {
									threads[i].out.println("<" + name + "> " + words[1]);
								}
							}
						}
						//Handles commands to play the game. Action along with client type and index are sent to GameLogic.
					} else if(inputLine.startsWith("QUIT")){
                        synchronized (this) {
                            out.println(game.commandFromServer(inputLine, player, clientIndex));//Prints out response of action.
                            break;
                        }
                    } else {
						synchronized (this) {
							out.println(game.commandFromServer(inputLine, player, clientIndex));//Prints out response of action.
							if(game.checkWin() == true){
								out.println(game.winGame());
								inputLine = "Bye";
							}
						}
						if (inputLine.equals("Bye"))
							break;
					}
				}
				//Handling Bot Interaction 
			}else if (player == false){
			    /*
			    *Bot creates an array of 500 random moves which it will slowly go through
			    * If the bot makes all 500 moves, it will disconnect from server.
			    * If the disconnect button is pressed on the BotClient GUI the bot will also disconnect.
			    */
                ArrayList<String> movesStore = new ArrayList<>(500);
                int moves = 0;
                for(int i = 0; i < 500; i++){
                    movesStore.add(in.readLine());
                }
				while(true){

					try{
						/*
						*Bot threads sleep for 4 seconds, then makes a move.
						*Game is not turn based, so 4 seconds is a reasonable amount of time to wait between bot movements.
						*/
						this.sleep(4000);
						try {
                            inputLine = movesStore.get(moves);
                        }catch (IndexOutOfBoundsException e){
						    inputLine = "QUIT";
                        }
                        moves++;
                        if(in.ready() == true){
                            inputLine = "QUIT";
                        }


					}catch (InterruptedException e){}
					
					synchronized (this) {
						//Bot passes its movement, type and index to GameLogic.
						game.commandFromServer(inputLine, player, clientIndex);
					}
					if(inputLine.equals("QUIT"))
					    break;
				}				
			}

			//Notifies all remaining clients when one leaves.
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
						threads[i].out.println("The user " + name + " is leaving the dungeon");
					}
				}
			}
			out.println("*** Bye " + name + " ***");
			//Nulls the thread so a new client can join the server.
			synchronized (this) {
					if (threads[clientIndex-1] == this) {
					    threads[clientIndex-1] = null;
					    clients[clientIndex-1] = null;
					}
			}

            System.out.println("DOD-Server: Client Disconnected " + socket.getRemoteSocketAddress());
			//Closes the streams of the disconnecting client.
			in.close();
			out.close();
            socket.close();
        } catch (IOException e) {
		    /*
		    *Handles clients disconnecting through unconventional means(not pressing the quit/disconnect button)
		     */
		    try{
                synchronized (this) {
                        threads[clientIndex-1] = null;
                        clients[clientIndex-1] = null;
                }
                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
                            String[] userLeaving = this.clientName.split("@",2);
                            threads[i].out.println("The user " + userLeaving[1] + " is leaving the dungeon");
                        }
                    }
                }
                System.out.println("DOD-Server: Client Disconnected " + socket.getRemoteSocketAddress());
                game.removeClient(clientIndex);
                in.close();
                out.close();
                socket.close();
            } catch (IOException f){
		        f.printStackTrace();
            }
        }
    }
}
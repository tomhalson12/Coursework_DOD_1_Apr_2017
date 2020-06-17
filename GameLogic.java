import java.util.ArrayList;

/**
*GameLogic. Controls client commands and logic of the game.
*
*@version 1.2
*@release 26/03/2017
*@see. Map.java
*/
public class GameLogic {
	
	private Map map;
	//private HumanPlayer human;
	//private BotPlayer bot;
	private int[] playerPosition = new int[20];
	private int[] collectedGold = new int[11];
	private String[] clientList = new String[11];
	private boolean active;
	private int numOfPlayers = 0;
	private ArrayList<Integer> outGame = new ArrayList<>();
	private ArrayList<Integer> inGame = new ArrayList<>();

	// who's turn is it?
	private int whatPlayer;
	private int currentIndex;
	private static final int HUMAN_PLAYER = 0;
	private static final int BOT_PLAYER = 1;
	
	public GameLogic(){
		map = new Map();
		map.readMap("maps/example_map.txt");
	
		//HumanPlayer class not used.
    	//human = new HumanPlayer();
    	
		//Bot Player Class not used by GameLogic
    	//bot = new BotPlayer();
    	
	}
	
	/**Creates a simple game between a human player and a bot
	public void playGame(){
    	// play game until someone wins
    	active = true;
    	while(gameRunning()){
    		whatPlayer = HUMAN_PLAYER;
    		String humanAction = human.getNextAction();
    		System.out.println(processCommand(humanAction));
    		whatPlayer = BOT_PLAYER;
    		String botAction = bot.getNextAction();
    		System.out.println(processCommand(botAction));
    	}
	}*/
		
	/*
	 *   Helper methods for when switching between human player and bot player
	 */
	 
	/**
	 *Inserts a new client on the map at a random free position
	 * Adds whether its a player or bot to the client list.
	 *
	 *@param player : trure if a player, false if a bot.
	 *		 index : player number.
	 */
	public void insertClient( boolean player , int index) {
        int[] position;
        position = map.randClientPos();

        playerPosition[((index * 2) - 2)] = position[0];
        playerPosition[((index * 2) - 1)] = position[1];

        if (player == true)
            clientList[index] = "player";
        else {
            clientList[index] = "bot";
        }

        //Adds the clients index to a list of client indexes in game
        inGame.add(index);
        if (outGame.contains(index)){
            for (int i = 0; i < outGame.size(); i++) {
                if (outGame.get(i) == index) {
                    outGame.remove(i);
                }
            }
        }
		numOfPlayers++;
	}

	/**
	 *Removes a client from the game by adding to the out of game list.
	 *
	 *@param index : the index of the disconnecting client.
	 */
	public void removeClient(int index){
        if (inGame.contains(index)){
            for (int i = 0; i < inGame.size(); i++) {
                if (inGame.get(i) == index) {
                    inGame.remove(i);
                    outGame.add(index);
                }
            }
        }
        numOfPlayers --;
    }

	/**
	 *Sets neccessay values for when a command is processed.
	 *
	 *@param player : true if a player, false if a bot.
	 *		 index : player number.
	 */
	public void currentClient(boolean player, int index){
		if(player == true){
			whatPlayer = HUMAN_PLAYER;
		}
		else{
			whatPlayer = BOT_PLAYER;
		}

		currentIndex = index;
	}
	
	/**
	 *Process clients command and returns its result.
	 *
	 *@param action : action client is performing
	 *       player : trure if a player, false if a bot.
	 *		 index : player number.
	 *@return : Result of the action.
	 */
	public String commandFromServer(String action, boolean player, int index){
		currentClient(player, index);
		return action + "\n" + processCommand(action);
	}
	
	
    // get current players total of collected gold
    private int getPlayersCollectedGold(){
    	return collectedGold[currentIndex];
    }
    
    // increment current players total of collected gold
    private void incrementPlayersCollectedGold(){
    	collectedGold[currentIndex] = collectedGold[currentIndex] + 1;
    }
    
    // get current players x coordinate using the index of the current client having a action processed
    private int getPlayersXCoordinate(){
    	return playerPosition[((currentIndex * 2) - 2)];
    }
    
    // set current players x coordinate using the index of the current client having a action processed
    private void setPlayersXCoordinate(int newX){
    	playerPosition[((currentIndex * 2) - 2)] = newX;
    }
    
    // get current opponent x coordinate using the index of the client found to be near the current client
    private int getOpponentXCoordinate(int index){
    	return playerPosition[((index * 2) - 2)];
    }
    
    // get current players y coordinate using the index of the current client having a action processed
    private int getPlayersYCoordinate(){
    	return playerPosition[((currentIndex * 2) - 1)];
    }
 
    // set current players y coordinate using the index of the current client having a action processed
    private void setPlayersYCoordinate(int newY){
    	playerPosition[((currentIndex * 2) - 1)] = newY;
    }
    
    // get current opponent y coordinate using the index of the client found to be near the current client
    private int getOpponentYCoordinate(int index){
    	return playerPosition[((index * 2) - 1)];
    }
	
	
	/**
	 *Works out the index of a client given its position.
	 *Searches for a matching set of coordinates in the playerPosition array.
	 *
	 *@param x : x coordinate of the client
	 *       y : y coordinate of the client
	 *@return : Index of the client
	 */
	private int getIndexOfOpponent(int x, int y){
		for(int i = 0; i!=19; i+=2){
			if(playerPosition[i] == x && playerPosition[i+1] == y){
				return (i+2)/2;
			}
		}
		return 0;
	}
    
    // current players icon
    private char getPlayersIcon(){
    	if(whatPlayer == HUMAN_PLAYER){
			return 'P';
		}else{
			return 'B';
		}
    }
    
    // opponent icon by checking the clientList with its index
    private char getOpponentIcon(int index){
    	if(clientList[index] == "player"){
			return 'P';
		}
		else{
			return 'B';
		}
    }
	
	/**
     * Processes the command. Works out which command should be called.
     *
     * @param action : action client wants to perform.
     * @return : Processed output of command.
     */
    public String processCommand(String action) {
    	String [] command = action.trim().split(" ");
		String answer = "FAIL";
		
		switch (command[0].toUpperCase()){
		case "HELLO":
			answer = hello();
			break;
		case "MOVE":
			if (command.length == 2 ){
				answer = move(command[1].toUpperCase().charAt(0));
			}
			break;
		case "PICKUP":
			answer = pickup();
			break;
		case "LOOK":
			answer = look();
			break;
		case "QUIT":
			quitGame();
		default:
			answer = "FAIL";
		}
		
		return answer;
    }

    /**
     * @return if the game is running.
     */
    private boolean gameRunning() {
        return active;
    }

    /**
     * @return : Returns back gold player requires to exit the Dungeon.
     */
    private String hello() {
        return "GOLD: " + (map.getGoldToWin() - getPlayersCollectedGold());
    }

    /**
     * Checks if movement is legal and updates player's location on the map.
     *
     * @param direction : The direction of the movement.
     * @return : Protocol if success or not.
     */
    protected String move(char direction) {
    	int newX = getPlayersXCoordinate();
    	int newY = getPlayersYCoordinate();
		switch (direction){
		case 'N':
			newY -=1;
			break;
		case 'E':
			newX +=1;
			break;
		case 'S':
			newY +=1;
			break;
		case 'W':
			newX -=1;
			break;
		default:
			break;
		}
		
		int xDistance = 1;
		int yDistance = 1;
		int i = 1;
		// check if the player can move to that tile on the map
		//while loop to check all of the 9 other possible clients
		while(xDistance != 0 && yDistance != 0){
            if(!outGame.contains(i)) {//checks if that index is in the game or not.
                if (i != currentIndex) {
                    xDistance = newX - getOpponentXCoordinate(i);
                    yDistance = newY - getOpponentYCoordinate(i);
                }
            }
		    i++;
			if(i == 11)
				break;
		}	
		if(xDistance == 0 && yDistance == 0){
			return "FAIL";
		}
		else if(map.getTile(newX, newY) != '#'){
			//System.out.println("moved from " + getPlayersXCoordinate() + ", " + getPlayersYCoordinate());
			setPlayersXCoordinate(newX);
			setPlayersYCoordinate(newY);
			//System.out.println("moved to " + getPlayersXCoordinate() + ", " + getPlayersYCoordinate());
			if (checkWin()){
				return "SUCCESS, " + winGame();
			}
			return "SUCCESS";
		} 
		else {
			return "FAIL";
		}
    }

    /**
     * Converts the map from a 2D char array to a single string.
     *
     * @return : A String representation of the game map.
     */
    private String look() {
    	// get look window for current player
    	char[][] look = map.look(getPlayersXCoordinate(), getPlayersYCoordinate());
    	// add current player's icon to look window
    	look[2][2] = getPlayersIcon();
    	// is opponent visible? if they are then add them to the look window
		if(numOfPlayers > 1){
			for(int i = 1; i != 11; i++){//For loop to check for all other 9 possible clients in look view
                if(inGame.contains(i)) {//Only if that index is in the game.
                    int xDistance = getPlayersXCoordinate() - getOpponentXCoordinate(i);
                    int yDistance = getPlayersYCoordinate() - getOpponentYCoordinate(i);
                    if (xDistance <= 2 && xDistance >= -2 && yDistance <= 2 && yDistance >= -2) {
                        look[2 - xDistance][2 - yDistance] = getOpponentIcon(getIndexOfOpponent(getOpponentXCoordinate(i), getOpponentYCoordinate(i)));
                    }
                }
			}
		}
    	// return look window as a String for printing
    	String lookWindow = "";
    	for(int i=0; i<look.length; i++){
    		for(int j=0; j<look[i].length; j++){
    			lookWindow += look[j][i];
    		}
    		lookWindow += "\n";
    	}
        return lookWindow;
    }

	/**
	 * Gets the whole map from Map.java, then adds in the location of any clients and send it to the server for the god view.
	 * @return the map as a character array.
	 */
	public char[][] gridView(){
        char[][] wholeMap = map.godView();

        if(numOfPlayers > 0) {
            for(int i = 0; i < inGame.size(); i++) {
                wholeMap[getOpponentXCoordinate(inGame.get(i))][getOpponentYCoordinate(inGame.get(i))] = getOpponentIcon(inGame.get(i));
            }
        }

        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 19; j++){
                //System.out.print(wholeMap[j][i]);
            }
            //System.out.println();
        }
        return wholeMap;
    }

    /**
     * Processes the player's pickup command, updating the map and the player's gold amount.
     *
     * @return If the player successfully picked-up gold or not.
     */
    protected String pickup() {
    	if (map.getTile(getPlayersXCoordinate(), getPlayersYCoordinate()) == 'G') {
    		incrementPlayersCollectedGold();
			map.replaceTile(getPlayersXCoordinate(), getPlayersYCoordinate(), '.');
			return "SUCCESS, GOLD COINS: " + getPlayersCollectedGold();
		}

		return "FAIL, There is nothing to pick up...";
    }

	/**
	 * Gets map height.
	 *
	 * @return map height
	 */
    public int mapHeight(){
        return map.getMapHeight();
    }

	/**
	 * Gets map width.
	 *
	 * @return map width
	 */
    public int mapWidth(){
        return map.getMapWidth();
    }

    /**
	 * checks if the player collected all GOLD and is on the exit tile
	 * @return True if all conditions are met, false otherwise
	 */
	protected boolean checkWin() {
		if (getPlayersCollectedGold() >= map.getGoldToWin() && 
			map.getTile(getPlayersXCoordinate(), getPlayersYCoordinate()) == 'E') {
			return true;
		}
		return false;
	}
	
	//Returns congratulating messages when a client wins the game.
	public String winGame(){
        active = false;
        if (inGame.contains(currentIndex)){
            for (int i = 0; i < inGame.size(); i++) {
                if (inGame.get(i) == currentIndex) {//removes the client from the game if they have won
                    inGame.remove(i);
                    outGame.add(currentIndex);
                }
            }
        }
        numOfPlayers --;
		return "Congratulations!!! You have escaped the Dungeon of Doom!!!!!! Thank you for playing!";
	}

	/**
	 * Quits the game when called
	 */
	public String quitGame() {
		active = false;
        if (inGame.contains(currentIndex)){
            for (int i = 0; i < inGame.size(); i++) {
                if (inGame.get(i) == currentIndex) {//removes the client from the game if they have quit.
                    inGame.remove(i);
                    outGame.add(currentIndex);
                }
            }
        }
        numOfPlayers --;
		return "Thank you for playing.\n The game will now exit.";
	}
    
	/*
    public static void main(String[] args) {
        GameLogic game = new GameLogic();
        game.playGame();
    }*/
}
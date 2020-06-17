import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Controls a human player.
 *Not used in the server implementation of the game.
 *
 */
public class HumanPlayer {

	private BufferedReader input;
	
	public HumanPlayer(){
		input = new BufferedReader(new InputStreamReader(System.in));
	}

    // Gets the next action from the human player.
    protected String getNextAction() {
    	String action = "";
    	try{
    		action = input.readLine();
    	}
        catch(IOException e){
        	System.err.println(e.toString());
        }
    	return action;
    }
}
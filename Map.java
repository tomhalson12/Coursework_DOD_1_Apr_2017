import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
*Map. Controls the map.
*
*@version 1.1
*@release 26/03/2017
*/
public class Map {
	
	private char[][] map;
	private String mapName;
	private int goldToWin;
	
	private static final int LOOK_RADIUS = 5;

    /**
     * @return : Gold required to exit the current map.
     */
    protected int getGoldToWin() {
        return goldToWin;
    }

    /**
     * @return : The look window around a players coordinates
     */
    protected char[][] look(int x, int y) {
    	char[][] reply = new char[LOOK_RADIUS][LOOK_RADIUS];
		for (int i = 0; i < LOOK_RADIUS; i++) {
			for (int j = 0; j < LOOK_RADIUS; j++) {
				int posX = x + j - LOOK_RADIUS/2;
				int posY = y + i - LOOK_RADIUS/2;
				if (posX >= 0 && posX < getMapWidth() && 
						posY >= 0 && posY < getMapHeight()){
					reply[j][i] = map[posY][posX];
				}
				else{
					reply[j][i] = '#';
				}
			}
		}
		return reply;
    }

    /**
     * Sends the map to GameLogic
     * @return map as a character array.
     */
    protected char[][] godView(){
        char[][] wholeMap = new char[getMapWidth()][getMapHeight()];
        for(int i = 0; i < getMapHeight(); i++){
            for(int j = 0; j < getMapWidth(); j++){
                wholeMap[j][i] = map[i][j];
            }
        }
        return wholeMap;
    }

    /**
     * @return : The name of the current map.
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @return : The width of the current map.
     */
    public int getMapWidth() {
    	return map[0].length;
    }
    
    /**
     * @return : The height of the current map.
     */
    public int getMapHeight() {
    	return map.length;
    }
	
	
    /**
     * Finds a random free tile on the map for a new client connecting.
     *
     * @return : x and y coordinates in ana int array
     */
	public int[] randClientPos(){
		Random rand = new Random();
		int[] position = new int[2];
		
		position[0] = rand.nextInt(getMapWidth()-1) + 1;
		position[1] = rand.nextInt(getMapHeight()-1) + 1;
		
		//Checks if the Tile selected is free, if not get a new random position.
		while(Arrays.asList('#' , 'G' , 'B' , 'E').contains(getTile(position[0],position[1]))){
			position[0] = rand.nextInt(getMapWidth()-1) + 1;
			position[1] = rand.nextInt(getMapHeight()-1) + 1;
		}
		
		return position;
	}
    
    /**
     * Retrieves a tile on the map. If the location requested is outside bounds of the map, it returns 'X' wall.
     *
     * @param x : x coordinates of the tile.
     * @param y : y coordinates of the tile.
     * @return : What the tile at the location requested contains.
     */
    public char getTile(int x, int y) {
    	if (y < 0 || x < 0 || y >= map.length || x >= map[0].length){
			return '#';
    	}
    	//System.out.println("TILE " + map[y][x]);
		return map[y][x];
    }
    
    /**
     * Replaces a tile on the map if the location requested is outside bounds of the map.
     *
     * @param x : x coordinates of the tile.
     * @param y : y coordinates of the tile.
     * @return : What the tile at the location requested contains.
     */
    public void replaceTile(int x, int y, char with) {
    	if (y < 0 || x < 0 || y >= map.length || x >= map[0].length){
    		// not in bounds
    	}
    	else{
    		map[y][x] = with;
    	}
    }

    /**
     * Reads the map from file.
     *
     * @param fileName: Name of the map's file.
     */
    public void readMap(String fileName) {
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(new File("maps","example_map.txt")));
    		map = loadMap(reader);
		} 
    	catch (FileNotFoundException e1) {
			System.err.println("no valid map name given and default file example_map.txt not found");
			System.exit(-1);
		} 
    	catch (IOException e) {
    		System.err.println("map not valid");
			System.exit(-1);
		}
    }
    
    /**
     * Reads the map from file.
     *
     * @param reader : BufferedReader for the map file.
     * @return : The map as a 2D char array 
     */
    private char[][] loadMap(BufferedReader reader) throws IOException{
		
		boolean error = false;
		ArrayList<char[]> tempMap = new ArrayList<char[]>();
		int width = -1;
		
		String in = reader.readLine();
		if (in.startsWith("name")){
			error = setName(in);
		}
		
		in = reader.readLine();
		if (in.startsWith("win")){
			error = setWin(in);
		}
		
		in = reader.readLine();
		if (in.charAt(0) == '#' && in.length() > 1){
			width = in.trim().length();
		}
		
		while (in != null && !error){

			char[] row = new char[in.length()];
			if  (in.length() != width){
				error = true;
			}
			
			for (int i = 0; i < in.length(); i++){
				row[i] = in.charAt(i);
			}

			tempMap.add(row);

			in = reader.readLine();
		}
		
		if (error) {
			setName("");
			setWin("");
			return null;
		}
		char[][] map = new char[tempMap.size()][width];
		
		for (int i=0;i<tempMap.size();i++){
			map[i] = tempMap.get(i);
		}
		return map;
	}
    
    /**
     * Sets the win condition for the game 
     * checking to make sure the line from the map file is valid 
     *
     * @param in : line of the map file which contains the win condition
     * @return : boolean value indicating if there was an error 
     */
    private boolean setWin(String in) {
		if (!in.startsWith("win ")){
			return true;
		}
		
		int win = 0;
		
		try { 
			win = Integer.parseInt(in.split(" ")[1].trim());
		} catch (NumberFormatException n){
			System.err.println("the map does not contain a valid win criteria!");
		}
		
		if (win < 0){ 
			return true;
		}
		
		this.goldToWin = win;
		return false;
	}
    
    /**
     * Sets the name of the maps 
     * checking to make sure the line from the map file is valid 
     *
     * @param in: line of the map file which contains the name of the map
     * @return : boolean value indicating if there was an error 
     */
	private boolean setName(String in) {
		if (!in.startsWith("name ") && in.length() < 4){
			return true;
		}
		
		String name = in.substring(4).trim();
		
		if (name.length() < 1){ 
			return true;
		}
		
		this.mapName = name;
		return false;
	}

}
	
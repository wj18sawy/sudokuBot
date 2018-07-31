package sBot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The sudokuSolve program reads in a text file containing an unsolved Sudoku file and creates a new text file of this puzzle solved.  
 * If the inputed file is named "newpuzzle.txt" the outputed file will be named newpuzzle.sln.txt.
 * 
 * @author Wyatt Sawyer
 * @version 7/31/18
 *
 */
public class sudokuSolve {
	
	public final boolean DEBUG = true; //final variable used for debugging program
	public int[][] grid; //2D array representing original unsolved puzzle
	public BufferedReader br;
	public Map<String, Boolean> originalNum; //hashmap to keep track of numbers from original unsolved puzzle (can't be changed)
	

	/**
	 * Main method begins solving the puzzle by calling the constructor. 
	 * 
	 * @param args Title of the text file for unsolved puzzle, ex: puzzle.txt
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		sudokuSolve s = new sudokuSolve(args[0]);
	}
	
	/**
	 * sudokuSolve constructor initializes instance variables, reads in unsolved puzzle text file,
	 * calls check method to begin recursively solving the puzzle and then finally creates a text file 
	 * for the solved puzzle.
	 * 
	 * @param fileName (name of file for unsolved puzzle)
	 * @throws IOException
	 */
	public sudokuSolve(String fileName) throws IOException{
		
		//initialize instance variables for 2D array of puzzle and hashmap of unchangeable values
		grid = new int[9][9];
		originalNum = new HashMap<String, Boolean>();

		//file is read in and fillGrid() method is called to fill 2D array with the unsolved puzzle's values
		File file = new File(fileName);
		br = new BufferedReader(new FileReader(file));
		fillGrid();
		
		//now call checking methods to solve the puzzle in grid 
		boolean result = check();
		if(!result)
		{
			System.out.println("Puzzle unsolvable");
		}
		else
		{
			String name = fileName.substring(0, fileName.length()-3);//removes the txt from the filename
			File sln = new File(name + "sln.txt");
			  
			//Create the file
			file.createNewFile();
			 
			//Write in solved puzzle
			FileWriter fw = new FileWriter(sln);
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i= 0; i<9;i++) {
				for(int j= 0; j<9; j++){
					bw.write(Integer.toString(grid[i][j]));
				}
				bw.newLine();
			}
			bw.close();
			fw.close();

		}
		
		//findFirstPossNum debug
		if(DEBUG)System.out.print(findFirstPossNum(0,0));
	}
	
	public boolean check(){
		return recursiveCheck(0,0,1);
	}
	
	/**
	 * 
	 * @param a
	 * @param r
	 * @param c
	 * @param direction exists because when skipping over a number from the original puzzle we need to know which direction we're going (retracing steps (-1) or advancing(1)) 
	 * @return
	 */
	public boolean recursiveCheck(int r, int c, int direction){
		boolean skip = false; //keep track of if this number is part of original puzzle
		boolean result;

		skip = originalNum.containsKey(Integer.toString(r) + ", " + Integer.toString(c));//see if this is original number
		if(skip) {//need to pass this number
			int thisR = r;
			int thisC = c + direction;
			
			if(thisC>8) {//if we need to go to next row
				thisR++;
				thisC=0;
			}
			else if(thisC<0) { //if we need to go to previous row
				thisR--;
				thisC=8;
			}
			
			result = recursiveCheck(thisR, thisC, direction);
		}
		else {
			int possNum = findFirstPossNum(r,c); //find the first possible number that could fit in our spot
			
			if(possNum == 0) { //if no possible number exists, one of the previous changes to the puzzle must be incorrect so we must go back
				int thisR;//used to check hashmap originalNum, and make next call to recursiveCheck
				int thisC;
				if(c == 0) {
					thisR = r-1;
					thisC = 8;
				}
				else {
					thisR = r;
					thisC = c-1;
				}
	
				grid[r][c] = 0;
				result = recursiveCheck(thisR, thisC, -1);//direction is -1 because we're retracing 
			}
			else if(r==8 && c==8) {//last spot (base case) recursion finished
				grid[8][8] = possNum;
				result = true;
			}
			else {
				grid[r][c] = possNum;
				if(c == 8) result = recursiveCheck(r+1, 0, 1); //if at end of the row we need to go down a row and start at the first column
				else result = recursiveCheck(r, c+1, 1);
			}
		}
		

		return result;
	}
	
	public int findFirstPossNum(int r, int c)
	{
		if(DEBUG) System.out.print("----> At " + r + " " + c);
		if(DEBUG)System.out.println(" Puzzle is currently...");
		if(DEBUG) {
			for(int i= 0; i<9;i++) {
				for(int j= 0; j<9; j++){
					System.out.print(grid[i][j]);
				}
				System.out.println();
			}
		}

		int possNum = 0;
		int current = grid[r][c];
		if(DEBUG) System.out.println("Current is " + current);
		
		//need to determine what change must be added to tempRow&Col depending on which 3x3 grid our spot is in to correctly to make checks
		int rowAddition = (int) Math.floor(r/3)*3;
		int colAddition = (int) Math.floor(c/3)*3;

		for(int i = current+1; i<=9 ; i++)//for every possible number it could be
		{
			if(DEBUG) System.out.print(" Trying " + i);
			boolean works = true;
			for(int j = 0; j<9; j++) {//check and make sure this number i isn't in the row
				if(grid[r][j] == i)
				{
					works = false;
					break;
				}
			}
			if(!works)continue;
			for(int j = 0; j<9; j++) {//check and make sure this number i isn't in the column
				if(grid[j][c] == i)
				{
					works = false;
					break;
				}
			}
			if(!works)continue;
			//check and make sure this number i isn't in the 3x3 grid (4 spots still unchecked)
			//need to know what spot in it's 3x3 grid it is to make the checks
			int row = r%3;
			int col = c%3;
			
			//row-1 & col-1
			int tempRow = row-1;
			tempRow = (((tempRow % 3) + 3) % 3);//calculation done as such because in Java, % returns remainder so the % won't give the modulus for negative numbers 
			int tempCol = col-1;
			tempCol = (((tempCol % 3) + 3) % 3);
			int actualRow = tempRow+rowAddition;
			int actualCol = tempCol+colAddition;
			if(grid[actualRow][actualCol] == i)continue;
			
			//row-1 & col-2
			tempCol = col-2;
			tempCol = (((tempCol % 3) + 3) % 3);
			actualRow = tempRow+rowAddition;
			actualCol = tempCol+colAddition;
			if(grid[actualRow][actualCol] == i)continue;
			
			//row-2 & col-2
			tempRow = row-2;
			tempRow = (((tempRow % 3) + 3) % 3);
			actualRow = tempRow+rowAddition;
			actualCol = tempCol+colAddition;
			if(grid[actualRow][actualCol] == i)continue;
			
			//row-2 & col-1
			tempCol = col-1;
			tempCol = (((tempCol % 3) + 3) % 3);
			actualRow = tempRow+rowAddition;
			actualCol = tempCol+colAddition;
			if(grid[actualRow][actualCol] == i)continue;
			
			//if still in loop this number i is a temporory fit for our puzzle
			possNum = i;
			break;
		}
		if(DEBUG) System.out.println(" Returning " + possNum);


		return possNum;
	}
	
	/**
	 * fillGrid method fills the 2D array grid with the unsolved puzzle from the text file. 
	 * 
	 * @throws IOException
	 */
	public void fillGrid() throws IOException
	{
		String st;
		int tempRowNum = 0; //variable used to keep track of what row the loop is on
		while ((st = br.readLine()) != null) { //loops through every row in the puzzle
			for(int i = 0; i<st.length(); i++) { //loops through every column in the row
				if(st.charAt(i) != 'X') { //if spot in puzzle isn't a blank
					int val = Character.getNumericValue(st.charAt(i));
					grid[tempRowNum][i] = val;//set this spot in the grid to be equal to the puzzle text file
					originalNum.put(Integer.toString(tempRowNum) + ", " + Integer.toString(i), true);//creates a mapping for row, col so we know this is a number from original puzzle
				}
			}
			tempRowNum++;		
		}
		
		if(DEBUG)System.out.println(Arrays.deepToString(grid));

	}
}

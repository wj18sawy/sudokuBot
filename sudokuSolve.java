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
 * If the input file is named "newpuzzle.txt", the output file will be named newpuzzle.sln.txt.
 * 
 * @author Wyatt Sawyer
 * @version 8/1/18
 *
 */
public class sudokuSolve {
	
	public final boolean DEBUG = false; //final variable used for debugging program
	public int[][] grid; //2D array representing original unsolved puzzle
	public BufferedReader br;
	

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

		//file is read in and fillGrid() method is called to fill 2D array with the unsolved puzzle's values
		File file = new File(fileName);
		br = new BufferedReader(new FileReader(file));
		fillGrid();
		
		//call recursive method to solve the puzzle in grid 
		boolean result = recursiveCheck(0,0);
		if(!result)//if puzzle couldn't be solved
		{
			System.out.println("Puzzle '"+ fileName + "' unsolvable");
		}
		else//puzzle solved
		{
			String name = fileName.substring(0, fileName.length()-3);//removes the txt from the filename
			System.out.println("Puzzle solved, look for file '" + name + "sln.txt'");
			File sln = new File(name + "sln.txt");
			  
			//Create the file
			sln.createNewFile();
			 
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
		
	}
	
	/**
	 * recursiveCheck method continuously calls itself to fill in the solved puzzle. If it hits a road block it will backtrack and 
	 * change the previous blocks number. This method will return true once a final solution is solved or false if there is no solution. 
	 * 
	 * @param r parameter is to keep track of row number
	 * @param c parameter is to keep track of column number
	 * @return boolean value whether this recursive path is the correct solve for the puzzle
	 */
	public boolean recursiveCheck(int r, int c){
		//find what the next r & c values would be so the next call can be made
		int nxtR=r;
		int nxtC=c;
		if(c==8)//if in last block of the row, row must increase and column must be at the start of the row
		{
			nxtR++;
			nxtC = 0;
		}
		else nxtC++;//else just move to next column in the same row
		
		if(r>8) { //if the puzzle is solved
			return true;
		}
		else if(grid[r][c]!=0){ //if block has a number from the original solved puzzle
			if(numWork(r,c,grid[r][c])) {//if block from original puzzle is valid
				if(recursiveCheck(nxtR,nxtC)){//check if the rest of the puzzle can be solved from this current grid
					return true;
				}
				else {
					return false;//if not must backtrack
				}	
			}
			else { //invalid puzzle
				System.out.println("Input puzzle isn't valid");
				System.exit(0);
				return false;
			}
		}
		else {
			for(int i = 1; i <=9; i++)//loop through every possible number for the block
			{
				if(numWork(r,c,i)) {//if number is temporarily valid
					grid[r][c] = i;//set the block to this number
					
					if(recursiveCheck(nxtR,nxtC)) {//check if the rest of the puzzle can be solved from this current grid
						return true;
					}
					else { //if not set the block back to blank (0) and try the next number in loop
						grid[r][c] = 0;
						continue;
					}
				}
			}
			return false;//no numbers fit in the block, so must backtrack
		}
		
	}
	
	/**
	 * numWork method checks if a number will be valid in a certain block of the puzzle.
	 * 
	 * @param r row being checked 
	 * @param c column being checked
	 * @param num number being checked for validity in grid[r][c]
	 * @return boolean describing whether or not the number is valid in the chosen block
	 */
	public boolean numWork(int r, int c, int num)
	{
		
		//need to determine what change must be added to tempRow&Col depending on which 3x3 grid our spot is in to correctly to make checks
		int rowAddition = (int) Math.floor(r/3)*3;
		int colAddition = (int) Math.floor(c/3)*3;

		
		if(DEBUG) System.out.print(" Trying " + num);
		for(int j = 0; j<9; j++) {//check and make sure this number i isn't in the row
			if(grid[r][j] == num)
			{
				if(DEBUG)System.out.print("numWork false bc match in row");
				if(j!=c)return false;
			}
		}
		for(int j = 0; j<9; j++) {//check and make sure this number i isn't in the column
			if(grid[j][c] == num)
			{
				if(DEBUG)System.out.print("numWork false bc match in col");
				if(j!=r)return false;
			}
		}
		//check and make sure this number i isn't in the 3x3 grid (4 spots still unchecked)
		//need to know what spot in it's 3x3 grid it is to make the checks
		int row = r%3;
		int col = c%3;
		
		//needed checks are always same distances away for r,c if you imagine only a 3x3 grid where the row and columns wrap around
		//row-1 & col-1
		int tempRow = row-1;
		tempRow = (((tempRow % 3) + 3) % 3);//calculation done as such because in Java, % returns remainder so the % won't give the modulus for negative numbers 
		int tempCol = col-1;
		tempCol = (((tempCol % 3) + 3) % 3);
		int actualRow = tempRow+rowAddition;//move to correct 3x3 square on the grid for the check
		int actualCol = tempCol+colAddition;
		if(grid[actualRow][actualCol] == num)return false;
		
		//row-1 & col-2
		tempCol = col-2;
		tempCol = (((tempCol % 3) + 3) % 3);
		actualRow = tempRow+rowAddition;
		actualCol = tempCol+colAddition;
		if(grid[actualRow][actualCol] == num)return false;
		
		//row-2 & col-2
		tempRow = row-2;
		tempRow = (((tempRow % 3) + 3) % 3);
		actualRow = tempRow+rowAddition;
		actualCol = tempCol+colAddition;
		if(grid[actualRow][actualCol] == num)return false;
		
		//row-2 & col-1
		tempCol = col-1;
		tempCol = (((tempCol % 3) + 3) % 3);
		actualRow = tempRow+rowAddition;
		actualCol = tempCol+colAddition;
		if(grid[actualRow][actualCol] == num)return false;
		
		//if still in loop, num is a temporary valid fit for the puzzle
		return true;
		
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
				}
			}
			tempRowNum++;		
		}
		
		if(DEBUG)System.out.println(Arrays.deepToString(grid));		

	}
}

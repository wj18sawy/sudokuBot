package sBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * class sudokuSolve takes in txt file of an unsolved sudoku puzzle and creates another text file of the puzzle solved. 
 * @author Wyatt Sawyer
 *
 */
public class sudokuSolve {
	
	public final boolean DEBUG = false;
	public int[][] grid;
	public BufferedReader br; 
	

	/**
	 * Main method reads in the text file and calls other methods
	 * @param args Title of text file for unsolved puzzle
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		sudokuSolve s = new sudokuSolve(args[0]);
	}
	
	public sudokuSolve(String fileName) throws IOException{
		
		//put input file into 2d array to be solved
		grid = new int[9][9];

		File file = new File(fileName);
		br = new BufferedReader(new FileReader(file));
		fillGrid();
		
		
	}
	
	/**
	 * fills in grid of unsolved sudoku puzzle
	 * @throws IOException
	 */
	public void fillGrid() throws IOException
	{
		String st;
		int tempRowNum = 0;
		while ((st = br.readLine()) != null) {
			for(int i = 0; i<st.length(); i++) {
				if(st.charAt(i) != 'X') { //if isn't blank square
					int val = Character.getNumericValue(st.charAt(i));
					grid[tempRowNum][i] = val;
				}
			}
			tempRowNum++;		
		}
		
		System.out.println(Arrays.deepToString(grid));
		
	}

}

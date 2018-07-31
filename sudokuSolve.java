package sBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * class sudokuSolve takes in txt file of an unsolved sudoku puzzle and creates another text file of the puzzle solved. 
 * @author Wyatt Sawyer
 *
 */
public class sudokuSolve {
	
	public final boolean DEBUG = true;
	public int[][] grid;
	public BufferedReader br; 
	public Map<String, Boolean> originalNum; //this is used to make sure we don't change a number from the original puzzle
	

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
		originalNum = new HashMap<String, Boolean>();

		File file = new File(fileName);
		br = new BufferedReader(new FileReader(file));
		fillGrid();
		
		//now call checking methods to fill out grid
		int[][] result = stepCheck();
		if(result == null)
		{
			System.out.println("Puzzle unsolvable");
		}
		else
		{
			//create new file with solution
			System.out.println(Arrays.deepToString(result));

		}
		
		//findFirstPossNum debug
		if(DEBUG)System.out.print(findFirstPossNum(grid,0,0));
	}
	
	public int[][] stepCheck(){
		return recursiveCheck(grid,0,0,1);
	}
	
	/**
	 * 
	 * @param a
	 * @param r
	 * @param c
	 * @param direction exists because when skipping over a number from the original puzzle we need to know which direction we're going (retracing steps (-1) or advancing(1)) 
	 * @return
	 */
	public int[][] recursiveCheck(int[][] a, int r, int c, int direction){
		boolean skip = false; //keep track of if this number is part of original puzzle
		
		int[][] currPuzzle = a; //current puzzle of this step
		int[][] result;
		
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
			
			result = recursiveCheck(currPuzzle, thisR, thisC, direction);
		}
		else {
			int possNum = findFirstPossNum(currPuzzle,r,c); //find the first possible number that could fit in our spot
			
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
	
				currPuzzle[r][c] = 0;
				result = recursiveCheck(currPuzzle, thisR, thisC, -1);//direction is -1 because we're retracing 
			}
			else if(r==8 && c==8) {//last spot (base case) recursion finished
				currPuzzle[8][8] = possNum;
				result = currPuzzle;
			}
			else {
				currPuzzle[r][c] = possNum;
				if(c == 8) result = recursiveCheck(currPuzzle, r+1, 0, 1); //if at end of the row we need to go down a row and start at the first column
				else result = recursiveCheck(currPuzzle, r, c+1, 1);
			}
		}
		

		return result;
	}
	
	public int findFirstPossNum(int[][] a, int r, int c)
	{
		if(DEBUG) System.out.print("----> At " + r + " " + c);
		if(DEBUG)System.out.println(" Puzzle is currently...");
		if(DEBUG)System.out.println(Arrays.deepToString(a));

		int possNum = 0;
		int current = a[r][c];
		if(DEBUG) System.out.println("Current is " + current);
		
		//need to determine what change must be added to tempRow&Col depending on which 3x3 grid our spot is in to correctly to make checks
		int rowAddition = (int) Math.floor(r/3)*3;
		int colAddition = (int) Math.floor(c/3)*3;

		for(int i = current+1; i<=9 ; i++)//for every possible number it could be
		{
			if(DEBUG) System.out.print(" Trying " + i);
			boolean works = true;
			for(int j = 0; j<9; j++) {//check and make sure this number i isn't in the row
				if(a[r][j] == i)
				{
					works = false;
					break;
				}
			}
			if(!works)continue;
			for(int j = 0; j<9; j++) {//check and make sure this number i isn't in the column
				if(a[j][c] == i)
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
			if(a[actualRow][actualCol] == i)continue;
			
			//row-1 & col-2
			tempCol = col-2;
			tempCol = (((tempCol % 3) + 3) % 3);
			actualRow = tempRow+rowAddition;
			actualCol = tempCol+colAddition;
			if(a[actualRow][actualCol] == i)continue;
			
			//row-2 & col-2
			tempRow = row-2;
			tempRow = (((tempRow % 3) + 3) % 3);
			actualRow = tempRow+rowAddition;
			actualCol = tempCol+colAddition;
			if(a[actualRow][actualCol] == i)continue;
			
			//row-2 & col-1
			tempCol = col-1;
			tempCol = (((tempCol % 3) + 3) % 3);
			actualRow = tempRow+rowAddition;
			actualCol = tempCol+colAddition;
			if(a[actualRow][actualCol] == i)continue;
			
			//if still in loop this number i is a temporory fit for our puzzle
			possNum = i;
			break;
		}
		if(DEBUG) System.out.println(" Returning " + possNum);


		return possNum;
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
					originalNum.put(Integer.toString(tempRowNum) + ", " + Integer.toString(i), true);//creates a mapping for row, col so we know this is a number from original puzzle
				}
			}
			tempRowNum++;		
		}
		
		if(DEBUG)System.out.println(Arrays.deepToString(grid));

	}
	


}

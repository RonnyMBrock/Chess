package Pieces;

import java.util.*;
import chess.*;
import java.io.FileReader;
import java.io.IOException;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pc-user
 */
public class King implements Piece{
    private final int value = 20000;
    private int colour;
    private int[] pos;
    private String pieceName;
    private int[][] squareValues = new int[8][8];
    public int movesDone = 0;
    
    public King(int c, int[] initPos){
        colour = c;
        pos = initPos;  
        pieceName = "king";
    }
    
    public String getPieceName(){
        return pieceName;
    }
    
    public int getPieceValue(){
        return value;
    }
    
    public int[] getPos(){
        return pos;
    }
    
    public int getColour(){
        return this.colour;
    }
    
    public boolean wasPromoted(){
        return false;
    }
    
    public int getSquareTableValue(int row, int col){
        return squareValues[row][col];
    }
    
    public ArrayList getAllMoves(Board board, boolean getCoverage){
        ArrayList<Integer[]> legalMoves = new ArrayList<>();

        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(i != 0 || j != 0){  //checks if it is a legal move for a king                  
                    int row = pos[0] + i;
                    int col = pos[1] + j;                    
                    
                    //checks if the move is within the bounds of the board and there is no piece occupying square
                    if(row >= 0 && row < 8 && col >= 0 && col < 8){
                      if(getCoverage || 
                            (!getCoverage && (board.getPieceOnSquare(row, col) == null || board.getPieceOnSquare(row, col).getColour() != this.colour))){
                            legalMoves.add(new Integer[]{row, col});                        
                        }
                    }
                }
            }
        }
        
        return legalMoves;
    }
    
    public boolean isThreatened(Board board){
        ArrayList<Integer[]> squares = new ArrayList();
        if(this.colour == 0){
            squares = board.getSquaresThreatenedByBlack();
        }
        else if(this.colour == 1){
            squares = board.getSquaresThreatenedByWhite();
        }
        
        //System.out.println(pos[0] + " " + pos[1]);
        for(Object obj: squares){
            Integer[] square = (Integer[]) obj;     
            //System.out.println(square[0] + " " + square[1]);
            if(pos[0] == square[0] && pos[1] == square[1]){
                return true;
            }
        }        
        
        return false;
    }
   
    
    public void setPos(int i, int j){
        pos[0] = i;
        pos[1] = j;
    }
    
    public void setSquareTableValues(){
        //reads in the piece-square table for this piece
        try{
            Scanner input = new Scanner(new FileReader("piece-square_tables/player_king.txt"));
            
            while(input.hasNext()){
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++){
                        if(colour == Main.playerSide){
                            squareValues[i][j] = input.nextInt();
                        }
                        else if(colour != Main.playerSide){
                            squareValues[7 - i][7 - j] = input.nextInt();
                        }
                    }
                }
            }
        }
        catch(IOException e){            
        }
        
    }
}

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
public class Knight implements Piece{
    private final int value = 320;
    private int colour;
    private int[] pos;
    private String pieceName;
    private int[][] squareValues = new int[8][8];
    private boolean promoted = false;
    
    public Knight(int c, int[] initPos, boolean promoted){
        colour = c;
        pos = initPos; 
        pieceName = "knight";
        this.promoted = promoted;
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
        return this.promoted;
    }
    
    public int getSquareTableValue(int row, int col){
        return squareValues[row][col];
    }
    
    public ArrayList getAllMoves(Board board, boolean getCoverage){
        ArrayList<Integer[]> legalMoves = new ArrayList<>();        

        for(int i = -2; i <= 2; i++){
            for(int j = -2; j <= 2; j++){
                if(i != 0 && j != 0 && (i + j)%2 != 0){  //checks if it is a legal move for a knight                  
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
    
    public void setPos(int i, int j){
        pos[0] = i;
        pos[1] = j;
    }
    
    public void setSquareTableValues(){
        //reads in the piece-square table for this piece
        try{
            Scanner input = new Scanner(new FileReader("piece-square_tables/player_knight.txt"));
            
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

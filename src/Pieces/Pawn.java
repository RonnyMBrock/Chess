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
public class Pawn implements Piece{
    private final int value = 100;
    private int colour;
    private int[] pos;
    private String pieceName;
    private int[][] squareValues = new int[8][8];
    public int movesDone = 0;
    
    public Pawn(int c, int[] initPos){
        colour = c;
        pos = initPos;  
        pieceName = "pawn";
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
        int pawnMove = 0;
        if(this.colour == Main.playerSide) pawnMove = -1;
        else if(this.colour != Main.playerSide) pawnMove = 1;
        
        int row = pos[0] + pawnMove;
        int col = pos[1];
        if(!getCoverage){            
            if(row >= 0 && row < 8 &&
                    board.getPieceOnSquare(row, col) == null){
                legalMoves.add(new Integer[]{row, col});            

                //checks if pawn can move two squares
                row = pos[0] + 2*pawnMove;
                if(row >= 0 && row < 8 && this.movesDone == 0 &&
                    board.getPieceOnSquare(row, col) == null){
                    legalMoves.add(new Integer[]{row, col});
                }
            } 
        }
        
        row = pos[0] + pawnMove;
        if(row >= 0 && row < 8){
            //checks if the pawn can attack to the user's left
            col = pos[1] - 1;
            if(col >= 0 && (getCoverage || 
                   (!getCoverage && board.getPieceOnSquare(row, col) != null && board.getPieceOnSquare(row, col).getColour() != this.colour))){
                legalMoves.add(new Integer[]{row, col});
            }

            //checks if the pawn can attack to the user's right
            col = pos[1] + 1;
            if(col < 8 && (getCoverage || 
                   (!getCoverage && board.getPieceOnSquare(row, col) != null && board.getPieceOnSquare(row, col).getColour() != this.colour))){
                legalMoves.add(new Integer[]{row, col});
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
            Scanner input = new Scanner(new FileReader("piece-square_tables/player_pawn.txt"));
            
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

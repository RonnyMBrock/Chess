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
public class Rook implements Piece{
    private final int value = 500;
    private int colour;
    private int[] pos;
    private String pieceName;
    private int[][] squareValues = new int[8][8];
    private boolean promoted = false;
    public int movesDone = 0;
    
    public Rook(int c, int[] initPos, boolean promoted){
        colour = c;
        pos = initPos;  
        pieceName = "rook";
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
        
        int row = pos[0] + 1;
        int col = pos[1];        
        while(row < 8 && (getCoverage || (!getCoverage && (board.getPieceOnSquare(row, col) == null ||
                    board.getPieceOnSquare(row, col).getColour() != this.colour)))){
            legalMoves.add(new Integer[]{row, col});
            //if the move has this piece capture an opponent's piece, stop checking along this row/column
            if(board.getPieceOnSquare(row, col) != null && 
                        (getCoverage || (!getCoverage && board.getPieceOnSquare(row, col).getColour() == (1 - this.colour)))) break;                
            row++;
        }
        
        row = pos[0] - 1;      
        while(row >= 0 && (getCoverage || (!getCoverage && (board.getPieceOnSquare(row, col) == null ||
                    board.getPieceOnSquare(row, col).getColour() != this.colour)))){
            legalMoves.add(new Integer[]{row, col});
            //if the move has this piece capture an opponent's piece, stop checking along this row/column
            if(board.getPieceOnSquare(row, col) != null && 
                        (getCoverage || (!getCoverage && board.getPieceOnSquare(row, col).getColour() == (1 - this.colour)))) break;
            row--;            
        }
        
        row = pos[0];
        col = pos[1] + 1;        
        while(col < 8 && (getCoverage || (!getCoverage && (board.getPieceOnSquare(row, col) == null ||
                     board.getPieceOnSquare(row, col).getColour() != this.colour)))){
            legalMoves.add(new Integer[]{row, col});
            //if the move has this piece capture an opponent's piece, stop checking along this row/column
            if(board.getPieceOnSquare(row, col) != null && 
                        (getCoverage || (!getCoverage && board.getPieceOnSquare(row, col).getColour() == (1 - this.colour)))) break;
            col++;            
        }
        
        col = pos[1] - 1;        
        while(col >= 0 && (getCoverage || (!getCoverage && (board.getPieceOnSquare(row, col) == null ||
                    board.getPieceOnSquare(row, col).getColour() != this.colour)))){
            legalMoves.add(new Integer[]{row, col});
            //if the move has this piece capture an opponent's piece, stop checking along this row/column
            if(board.getPieceOnSquare(row, col) != null && 
                        (getCoverage || (!getCoverage && board.getPieceOnSquare(row, col).getColour() == (1 - this.colour)))) break;
            col--;            
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
            Scanner input = new Scanner(new FileReader("piece-square_tables/player_rook.txt"));
            
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

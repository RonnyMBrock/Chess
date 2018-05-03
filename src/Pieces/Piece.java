package Pieces;

import chess.*;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pc-user
 */
public interface Piece {    
    public String getPieceName();
    public int getPieceValue();
    public int[] getPos();
    public int getColour();
    public boolean wasPromoted();
    public int getSquareTableValue(int row, int col);
    public ArrayList getAllMoves(Board board, boolean getCoverage);
    public void setPos(int i, int j);
    public void setSquareTableValues();
}

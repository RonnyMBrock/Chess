package chess;


import Pieces.*;
import java.awt.Color;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pc-user
 */
public class Square {
    public Piece piece;
    public boolean litUp;
    public Color colorOfSquare;
    public int[] pos;
    
    public Square(Piece piece, Color color, int[] pos){
        this.piece = piece;
        litUp = false;
        this.colorOfSquare = color;
        this.pos = pos;
    }
}

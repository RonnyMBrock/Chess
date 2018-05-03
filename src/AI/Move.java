package AI;

import Pieces.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pc-user
 */
public class Move {
    public Piece piece;
    public int[] newPos;
    public Move(Piece piece, int[] newPos){
        this.piece = piece;
        this.newPos = new int[]{newPos[0], newPos[1]};
    }
            
}

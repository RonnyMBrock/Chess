package chess;

import java.util.*;
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
public class Board {
    private Piece[][] board;
    private Main mainThread;
    
    public int sideToMove;
    public double boardScore;
    public Piece enPassentablePawn;
    public boolean playerWin;
    public boolean compWin;
    
    public Board(Piece[][] initBoard, Main mainThread){
        board = new Piece[8][8];
        sideToMove = 0;
        this.mainThread = mainThread;
        
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                board[i][j] = initBoard[i][j];                
            }
        }
    }
       
    public Piece getPieceOnSquare(int row, int col){
        if(board[row][col] == null){
            return null;
        }
        return board[row][col];
    }
    
    public void setPieceOnSquare(Piece piece, int row, int col){
        board[row][col] = piece;
    }
    
    public ArrayList getSquaresThreatenedByWhite(){
        ArrayList<Integer[]> squares = new ArrayList(); 
        Integer[] square;
        
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i][j] != null && board[i][j].getColour() == 0){
                    for(Object move: board[i][j].getAllMoves(this, true)){
                        square = (Integer[]) move;
                        squares.add(square);
                    }
                }
            }
        }
        
        return squares;
    }

    public ArrayList getSquaresThreatenedByBlack(){
        ArrayList<Integer[]> squares = new ArrayList(); 
        Integer[] square;
        
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i][j] != null && board[i][j].getColour() == 1){
                    for(Object move: board[i][j].getAllMoves(this, true)){
                        square = (Integer[]) move;
                        squares.add(square);
                    }
                }
            }
        }
        
        return squares;
    }
    
    public ArrayList<Piece> getRooksToCastleWith(Piece king){
        ArrayList<Piece> castlingRooks = new ArrayList<>();
        int[] kingPos = king.getPos();
        
        if(!((King)king).isThreatened(this)){
            for(int i = kingPos[1] + 1; i < 8; i++){
                Piece piece = board[kingPos[0]][i];
                if(piece != null){
                    if(piece.getPieceName().equals("rook") && ((King)king).movesDone == 0 && 
                            ((Rook)piece).movesDone == 0 && piece.getPos()[1] > king.getPos()[1] + 2){
                        castlingRooks.add(piece);
                        break;
                    }
                    else if(!piece.getPieceName().equals("rook")){
                        break;
                    }
                }
            }

            for(int i = kingPos[1] - 1; i >= 0; i--){
                Piece piece = board[kingPos[0]][i];
                if(piece != null){
                    if(piece.getPieceName().equals("rook") && ((King)king).movesDone == 0 && 
                            ((Rook)piece).movesDone == 0 && piece.getPos()[1] < king.getPos()[1] - 2){
                        castlingRooks.add(piece);
                        break;
                    }
                    else if(!piece.getPieceName().equals("rook")){
                        break;
                    }
                }
            }
        }
        
        return castlingRooks;
    }
    
    public void Castle(Piece king, Piece rook){
        int[] rookPos = new int[]{rook.getPos()[0], rook.getPos()[1]};
        int[] kingPos = new int[]{king.getPos()[0], king.getPos()[1]};
        
        if(rookPos[1] > kingPos[1]){
            king.setPos(kingPos[0], kingPos[1] + 2);
            this.setPieceOnSquare(king, king.getPos()[0], king.getPos()[1]);
            this.setPieceOnSquare(null, kingPos[0], kingPos[1]);
            
            rook.setPos(rookPos[0], king.getPos()[1] - 1);
            this.setPieceOnSquare(rook, rook.getPos()[0], rook.getPos()[1]);
            this.setPieceOnSquare(null, rookPos[0], rookPos[1]);
        }
        else if(rookPos[1] < kingPos[1]){
            king.setPos(kingPos[0], kingPos[1] - 2);
            this.setPieceOnSquare(king, king.getPos()[0], king.getPos()[1]);
            this.setPieceOnSquare(null, kingPos[0], kingPos[1]);
            
            rook.setPos(rookPos[0], king.getPos()[1] + 1);
            this.setPieceOnSquare(rook, rook.getPos()[0], rook.getPos()[1]);
            this.setPieceOnSquare(null, rookPos[0], rookPos[1]);
        }
        
    }
    
    
    public void enPassent(Piece attackingPawn){
        int[] atckPawnPos = new int[]{attackingPawn.getPos()[0], attackingPawn.getPos()[1]};
        int[] capPawnPos = new int[]{enPassentablePawn.getPos()[0], enPassentablePawn.getPos()[1]};
        
        if(attackingPawn.getColour() == mainThread.playerSide){
            attackingPawn.setPos(capPawnPos[0] - 1, capPawnPos[1]);
            board[capPawnPos[0] - 1][capPawnPos[1]] = attackingPawn;
        }
        else if(attackingPawn.getColour() != mainThread.playerSide){
            attackingPawn.setPos(capPawnPos[0] + 1, capPawnPos[1]);
            board[capPawnPos[0] + 1][capPawnPos[1]] = attackingPawn;
        }
        board[atckPawnPos[0]][atckPawnPos[1]] = null;
        board[capPawnPos[0]][capPawnPos[1]] = null;        
    }
    
    public void DoPieceMovedOperations(Piece pieceThatMoved){
        //if the piece moved is a rook, king or pawn, its firstMove flag is set to false
        if(pieceThatMoved.getPieceName().equals("rook")){
            ((Rook)pieceThatMoved).movesDone += 1;
            //System.out.println("r" + ((Rook)pieceThatMoved).movesDone);
        }
        else if(pieceThatMoved.getPieceName().equals("king")){
            ((King)pieceThatMoved).movesDone++;
            //System.out.println("k" + ((King)pieceThatMoved).movesDone);
        }
        else if(pieceThatMoved.getPieceName().equals("pawn")){
            ((Pawn)pieceThatMoved).movesDone++;
            int[] newPos = pieceThatMoved.getPos();

            if(pieceThatMoved.getColour() == Main.playerSide && Main.playerSide == this.sideToMove && newPos[0] == 0){
               mainThread.PromotePiece(pieceThatMoved);
            }
            else if((pieceThatMoved.getColour() != Main.playerSide && newPos[0] == 7) || 
                    (pieceThatMoved.getColour() != Main.playerSide && newPos[0] == 0)){
                Piece promotedPiece = new Queen(pieceThatMoved.getColour(), newPos, true);
                promotedPiece.setSquareTableValues();
                this.setPieceOnSquare(promotedPiece, newPos[0], newPos[1]);
            }
        }             

    }
    
    public void UndoPieceMovedOperations(Piece pieceThatMoved){
        if(pieceThatMoved.getPieceName().equals("rook")){
            ((Rook)pieceThatMoved).movesDone -= 1;
        }
        else if(pieceThatMoved.getPieceName().equals("king")){
            ((King)pieceThatMoved).movesDone--;
        }
        else if(pieceThatMoved.getPieceName().equals("pawn")){
            ((Pawn)pieceThatMoved).movesDone--;           
        } 
        
        int[] pos = pieceThatMoved.getPos();
        if(pieceThatMoved.wasPromoted() && pieceThatMoved.getColour() != Main.playerSide && pieceThatMoved.getPos()[0] == 7){
            Piece oldPiece = new Pawn(pieceThatMoved.getColour(), new int[]{pos[0] - 1, pos[1]});
            oldPiece.setSquareTableValues();
            this.setPieceOnSquare(oldPiece, pos[0] - 1, pos[1]);
            
        }
    }
}

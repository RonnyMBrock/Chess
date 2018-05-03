package AI;

import java.util.*;
import Pieces.*;
import chess.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pc-user
 */
public class ComputerAI implements Runnable{
    
    private Main mainThread;
    ArrayList<Piece> computerPieces;
    private int depth;
    public Piece pieceMoved;
    public int[] pieceMovedNewPos;
    public int[] pieceMovedOldPos;
    public boolean end;
    //public int[] pieceMovedNewPos = new int[2];
    
    public ComputerAI(Main mainThread, ArrayList<Piece> computerPieces, int depth){
        this.mainThread = mainThread;
        this.computerPieces = computerPieces;
        this.depth = depth;
        end = false;
    }
    
    public void updateComputerPieces(ArrayList<Piece> pieces){
        this.computerPieces = pieces;
    }
    
    public void removePieceFromMemory(Piece pieceToRemove){
        if(pieceToRemove != null){
            computerPieces.remove(pieceToRemove);
        }
    }
    
    private Board searchGameTree(Board board, int level, double alpha, double beta){
        Board bestBoard = null;
        if(level == depth){
            board.boardScore = evaluateBoard(board);
            return board;
        }
        else if(level < depth){                    
            Piece[][] newBoard = new Piece[8][8];
            //ArrayList<Piece> opposingPieces = new ArrayList();
            ArrayList<Piece> pieces = new ArrayList();
            King opposingKing = null;
            King king = null;
            
            double bestScore = 0;
            if(level%2 == 1) bestScore = Integer.MAX_VALUE;
            else bestScore = Integer.MIN_VALUE;

            //copies the board and all pieces on it
            //also finds and adds all opposing pieces on the board to an arraylist 
            //and finds and sets both opposing king and this side's own king
            for(int i = 0; i < newBoard.length; i++){
                for(int j = 0; j < newBoard.length; j++){
                    Piece pieceOnBoard = board.getPieceOnSquare(i, j); 
                    int color = 0;
                    if(pieceOnBoard != null) color = pieceOnBoard.getColour();
                    
                    if(pieceOnBoard != null && pieceOnBoard.getPieceName().equals("bishop")){
                        newBoard[i][j] = new Bishop(color, pieceOnBoard.getPos(), false);
                    }
                    else if(pieceOnBoard != null && pieceOnBoard.getPieceName().equals("rook")){
                        newBoard[i][j] = new Rook(color, pieceOnBoard.getPos(), false);
                        ((Rook)newBoard[i][j]).movesDone = ((Rook)pieceOnBoard).movesDone;
                    }
                    else if(pieceOnBoard != null && pieceOnBoard.getPieceName().equals("knight")){
                        newBoard[i][j] = new Knight(color, pieceOnBoard.getPos(), false);
                    }
                    else if(pieceOnBoard != null && pieceOnBoard.getPieceName().equals("pawn")){
                        newBoard[i][j] = new Pawn(color, pieceOnBoard.getPos());
                        ((Pawn)newBoard[i][j]).movesDone = ((Pawn)pieceOnBoard).movesDone;
                    }
                    else if(pieceOnBoard != null && pieceOnBoard.getPieceName().equals("king")){
                        newBoard[i][j] = new King(color, pieceOnBoard.getPos());
                        ((King)newBoard[i][j]).movesDone = ((King)pieceOnBoard).movesDone;
                    }
                    else if(pieceOnBoard != null && pieceOnBoard.getPieceName().equals("queen")){
                        newBoard[i][j] = new Queen(color, pieceOnBoard.getPos(), false);
                    }
                    
                    if(newBoard[i][j] != null){
                        newBoard[i][j].setPos(i, j);
                        newBoard[i][j].setSquareTableValues();
                        if((level%2 == 0 && color == mainThread.playerSide) || (level%2 == 1 && color != mainThread.playerSide)){
                            //opposingPieces.add(newBoard[i][j]);                        
                            if(newBoard[i][j].getPieceName().equals("king")){
                                opposingKing = (King)newBoard[i][j];                                
                            }
                        }                        
                        
                        if((level%2 == 0 && color != mainThread.playerSide) || (level%2 == 1 && color == mainThread.playerSide)){
                            pieces.add(newBoard[i][j]);
                            if(newBoard[i][j].getPieceName().equals("king")) {
                                king = (King)newBoard[i][j];
                            }
                        }                        
                    }
                    
                }
            }            
            
            HashMap<Board, Move> possibleBoards = new HashMap<Board, Move>();            
            //compute all possible moves that can be made from the current board
            for(Piece piece: pieces){
                if(beta <= alpha) break;
                
                int[] oldPos = new int[]{piece.getPos()[0], piece.getPos()[1]};                 
                
                for(Object obj: piece.getAllMoves(board, false)){  
                    //if(beta <= alpha) break;

                    Integer[] newPos = (Integer[]) obj;                    
                    Board possibleBoard = new Board(newBoard, mainThread);
                    possibleBoard.sideToMove = mainThread.chessBoard.sideToMove;
                    //Piece pieceToCapture = possibleBoard.getPieceOnSquare(newPos[0], newPos[1]);                                   
                                        
                    possibleBoard.setPieceOnSquare(piece, newPos[0], newPos[1]);
                    possibleBoard.setPieceOnSquare(null, oldPos[0], oldPos[1]);                 
                    //if(pieceToCapture != null) opposingPieces.remove(pieceToCapture);
                    //System.out.println(level + ": " + piece.getPieceName() + " " + newPos[0] + " " + newPos[1]);
                    piece.setPos(newPos[0], newPos[1]);                    
                    //possibleBoard.DoPieceMovedOperations(piece);
                    
                    if(!king.isThreatened(possibleBoard)){
                        Move move = new Move(piece, new int[]{newPos[0], newPos[1]});
                        if(piece.getPieceName().equals("pawn") && ((Pawn)piece).movesDone == 1 &&
                                (piece.getPos()[0] == oldPos[0] - 2 || piece.getPos()[0] == oldPos[0] + 2)){
                            possibleBoard.enPassentablePawn = piece;
                        }
                        
                        possibleBoards.put(possibleBoard, move);        
                    }
                    piece.setPos(oldPos[0], oldPos[1]); 
                    //if(pieceToCapture != null) opposingPieces.add(pieceToCapture);                     
                    //possibleBoard.UndoPieceMovedOperations(piece);
                }
                
                
                if(piece.getPieceName().equals("king")){
                    ArrayList<Piece> castlingRooks = board.getRooksToCastleWith(piece);
                    for(Piece rook: castlingRooks){
                        int[] rookPos = new int[]{rook.getPos()[0], rook.getPos()[1]};
                        
                        Board possibleBoard = new Board(newBoard, mainThread);
                        possibleBoard.Castle(piece, rook);
                        Move move = new Move(piece, piece.getPos());
                        possibleBoards.put(possibleBoard, move);
                        
                        piece.setPos(oldPos[0], oldPos[1]);
                        rook.setPos(rookPos[0], rookPos[1]);
                    }
                }
                
                if(piece.getPieceName().equals("pawn") && board.enPassentablePawn != null && board.enPassentablePawn.getPos()[0] == piece.getPos()[0] &&
                        (board.enPassentablePawn.getPos()[1] == piece.getPos()[1] + 1 || board.enPassentablePawn.getPos()[1] == piece.getPos()[1] - 1)){
                    Board possibleBoard = new Board(newBoard, mainThread);
                    possibleBoard.enPassentablePawn = board.enPassentablePawn;
                    
                    possibleBoard.enPassent(piece);                    
                    Move move = new Move(piece, piece.getPos());
                    possibleBoards.put(possibleBoard, move);
                    
                    piece.setPos(oldPos[0], oldPos[1]);
                }
            }
            
            for(Map.Entry<Board, Move> entry: possibleBoards.entrySet()){
                if(beta <= alpha) break; 
                
                Board possibleBoard = entry.getKey();
                Move move = entry.getValue();
                
                int[] oldPos = new int[]{move.piece.getPos()[0], move.piece.getPos()[1]};      
                move.piece.setPos(move.newPos[0], move.newPos[1]);
                possibleBoard.DoPieceMovedOperations(move.piece);
//                System.out.println(level + ": " );
//                    for(int i = 0; i < 8; i++){
//                        for(int j = 0; j < 8; j++){
//                            Piece p = possibleBoard.getPieceOnSquare(i, j);
//                            if(p != null)System.out.print("[" + p.getPieceName().charAt(0) + "]");
//                            else System.out.print("[ ]");
//                        }
//                        System.out.println();
//                    }
//                    System.out.println();
                
                Board bestMoveForPossibleBoard = searchGameTree(possibleBoard, level + 1, alpha, beta);
                if(bestMoveForPossibleBoard == null){
                    bestMoveForPossibleBoard = possibleBoard;
                    if(opposingKing.isThreatened(possibleBoard)){
                        if(opposingKing.getColour() == mainThread.playerSide){                            
                            bestMoveForPossibleBoard.boardScore = Integer.MAX_VALUE;
                        }
                        else if(opposingKing.getColour() != mainThread.playerSide){
                            bestMoveForPossibleBoard.boardScore = Integer.MIN_VALUE;    
                        }
                    }
                    else{
                        bestMoveForPossibleBoard.boardScore = evaluateBoard(bestMoveForPossibleBoard);
                    }
                }

                if(level%2 == 0){
                    if(bestMoveForPossibleBoard.boardScore >= bestScore){
                        bestScore = bestMoveForPossibleBoard.boardScore;
                        bestBoard = possibleBoard;
                        bestBoard.boardScore = bestScore;
                        if(level == 0){
                            pieceMoved = move.piece;
                            pieceMovedNewPos = new int[]{move.piece.getPos()[0], move.piece.getPos()[1]};
                            pieceMovedOldPos = new int[]{oldPos[0], oldPos[1]};
                        }
                    }                            

                    if(bestScore > alpha){
                        alpha = bestScore;
                    }
                }
                else if(level%2 == 1){
                    if(bestMoveForPossibleBoard.boardScore <= bestScore){
                      bestScore = bestMoveForPossibleBoard.boardScore;
                      bestBoard = possibleBoard;
                      bestBoard.boardScore = bestMoveForPossibleBoard.boardScore;                              
                    }

                    if(bestScore < beta){
                        beta = bestScore;
                    }
                }
                possibleBoard.UndoPieceMovedOperations(move.piece);
                move.piece.setPos(oldPos[0], oldPos[1]);
            }            
             
        }  
        
        return bestBoard;
    }   
    
    private double evaluateBoard(Board board){
        int compColour = computerPieces.get(0).getColour();
        //positive values mean that square is protected by 1 or more computer piece
        //negative values mean theat square is threatened by 1 or more player piece
        double[][] threatMap = new double[8][8]; 
        ArrayList<Piece> playerPieces = new ArrayList();
        ArrayList<Piece> compPieces = new ArrayList();

        int playerBishops = 0;
        int compBishops = 0;
        int materialScore = 0;
        int piecePosScore = 0;
        int mobilityScore = 0;
        double threatScore = 0;
        
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Piece piece = board.getPieceOnSquare(i, j);
                if(piece != null){                                     
                    if(piece.getColour() == compColour){
                        compPieces.add(piece);
                        
                        piecePosScore += piece.getSquareTableValue(i, j);
                        materialScore += piece.getPieceValue();

                        //increments number of computer bishops and if there is more than 1 bishop, adds
                        //a pawn's worth to the material score since 2 bishops are more valuable
                        if(piece.getPieceName().equals("bishop")){
                            compBishops++;
                            if(compBishops > 1){
                                materialScore += 100;
                            }
                        }
                        for(Object obj: piece.getAllMoves(board, true)){
                            Integer[] move = (Integer[])obj;
                            mobilityScore += 1;
                            threatMap[move[0]][move[1]] += 1;
                        }

                    }

                    else if(piece.getColour() != compColour){
                        playerPieces.add(piece);
                        
                        piecePosScore -= piece.getSquareTableValue(i, j);
                        materialScore -= piece.getPieceValue();

                        //increments number of player bishops and if there is more than 1 bishop, subtracts
                        //a pawn's worth to the material score since 2 bishops are more valuable
                        if(piece.getPieceName().equals("bishop")){
                            playerBishops++;
                            if(playerBishops > 1){
                                materialScore -= 100;
                            }
                        }
                        for(Object obj: piece.getAllMoves(board, true)){
                            Integer[] move = (Integer[])obj;
                            mobilityScore -= 1;
                            threatMap[move[0]][move[1]] -= 1;
                        }
                    }                    
                }
                
            }
        }
        
        for(Piece compPiece: compPieces){
            if(!compPiece.getPieceName().equals("king")){
                int[] compPos = compPiece.getPos();
                threatScore += threatMap[compPos[0]][compPos[1]] * compPiece.getPieceValue()/20;
            }
        }
        for(Piece playerPiece: playerPieces){
            if(!playerPiece.getPieceName().equals("king")){
                int[] playerPos = playerPiece.getPos();
                threatScore += threatMap[playerPos[0]][playerPos[1]] * playerPiece.getPieceValue()/20;
            }
        }
        
        return materialScore + piecePosScore + mobilityScore + threatScore;
    }
    
    public void run(){
        for( ; ; ){
            System.out.print(""); //This line may do nothing but it is required to make the whole thing work. Strange I know
            if(end) break;
            else{
                if(mainThread.chessBoard.sideToMove != mainThread.playerSide){ 
                    try{
                        Thread.sleep(250);
                    }
                    catch(Exception e){                    
                    }              

                    Board nextMove = searchGameTree(mainThread.chessBoard, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    if(nextMove == null){                    
                        mainThread.chessBoard.compWin = false;
                        King compKing = null;
                        for(Piece piece: computerPieces){
                            if(piece.getPieceName().equals("king")){
                                compKing = (King)piece;
                            }
                        }
                        if(compKing.isThreatened(mainThread.chessBoard)){
                            mainThread.chessBoard.playerWin = true;
                        }
                        else{
                            mainThread.chessBoard.playerWin = false;
                        }
                        break;
                    }    
                    
                    mainThread.lightUpSquare(pieceMovedNewPos[0], pieceMovedNewPos[1]);
                    mainThread.lightUpSquare(pieceMovedOldPos[0], pieceMovedOldPos[1]);
                    if(mainThread.playerKing.isThreatened(mainThread.chessBoard)){
                        mainThread.lightUpSquare(mainThread.playerKing.getPos()[0], mainThread.playerKing.getPos()[1]);
                    }
                    mainThread.chessBoard = nextMove;

                    mainThread.chessBoard.DoPieceMovedOperations(pieceMoved);
                    mainThread.chessBoard.enPassentablePawn = null;
                    if(pieceMoved.getPieceName().equals("pawn") && ((Pawn)pieceMoved).movesDone == 1){
                        mainThread.chessBoard.enPassentablePawn = pieceMoved;
                    }                    
                    
                    
                    computerPieces = new ArrayList();
                    mainThread.UpdateDisplay();            

                    ArrayList<Integer[]> playerMoves = new ArrayList();
                    for(int i = 0; i < 8; i++){
                        for(int j = 0; j < 8; j++){
                            Piece piece = mainThread.chessBoard.getPieceOnSquare(i, j);
                            if(piece != null && piece.getColour() == mainThread.playerSide){
                                for(Object obj: piece.getAllMoves(mainThread.chessBoard, false)){
                                    Integer[] move = (Integer[]) obj;
                                    Piece capturedPiece = mainThread.chessBoard.getPieceOnSquare(move[0], move[1]);
                                    mainThread.chessBoard.setPieceOnSquare(piece, move[0], move[1]);
                                    mainThread.chessBoard.setPieceOnSquare(null, i, j);
                                    piece.setPos(move[0], move[1]);

                                    if(!mainThread.playerKing.isThreatened(mainThread.chessBoard)){
                                        playerMoves.add(move);
                                    } 

                                    mainThread.chessBoard.setPieceOnSquare(capturedPiece, move[0], move[1]);
                                    mainThread.chessBoard.setPieceOnSquare(piece, i, j);
                                    piece.setPos(i, j);
                                }                            
                            }
                        }
                    }
                    if(playerMoves.isEmpty()){                    
                        mainThread.chessBoard.playerWin = false;
                        if(mainThread.playerKing.isThreatened(mainThread.chessBoard)){
                            mainThread.chessBoard.compWin = true;
                        }
                        else{
                            mainThread.chessBoard.compWin = false;
                        }
                        break;
                    }                 

                    mainThread.chessBoard.sideToMove = mainThread.playerSide;
                }
            }
        }
        
        mainThread.endGame();
    }
    
    
}

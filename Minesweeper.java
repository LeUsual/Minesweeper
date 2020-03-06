import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.MouseEvent;

//Written by Keegan Pereira
//Stu# 30028768

public class Minesweeper extends Application{
	static int[][] board;
	static BorderPane maxPane = new BorderPane();
	static GridPane tPane = new GridPane();
	static HBox heavyPane = new HBox(10);
	
	//Game Difficulty
	static int xSize = 8;
	static int ySize = 8;
	static int bombs = 10;
	static double size = 75;
	
	static int spots = 25;
	
	static Label minesLeft;
	static Label timeLBL;
	
	static int flagCounter;
	static int totalSeconds;
	static boolean gameOver;
	
    static Timeline timeLine = new Timeline(
    		new KeyFrame(Duration.seconds(1), e ->  {
    			totalSeconds++;
    			timeLBL.setText(labelFormatting(totalSeconds));
    		})
    	);

	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage theStage) {
		flagCounter = bombs;
		gameOver = false;
		totalSeconds = 0;

        timeLine.setCycleCount(Animation.INDEFINITE);
		
		MSButton[][] buttons = new MSButton[xSize][ySize];
		MSButton faceButt = new MSButton(size);
		
		heavyPane.setAlignment(Pos.CENTER);
        faceButt.setPadding(Insets.EMPTY);
        faceButt.setGraphic(faceButt.imageFaceSmile);
        
        
        timeLBL = new Label(labelFormatting(totalSeconds));
        timeLBL.setFont(Font.font("Times New Roman", FontWeight.BOLD, 35));

        minesLeft = new Label(labelFormatting(bombs));
        minesLeft.setFont(Font.font("Times New Roman", FontWeight.BOLD, 35));
        
        heavyPane.getChildren().add(timeLBL);
        heavyPane.getChildren().add(faceButt);
        heavyPane.getChildren().add(minesLeft);

        dummyBoard(buttons, faceButt);
        
		faceButt.setOnAction(p ->{
			resetGame(buttons, faceButt);
		});
		
        faceButt.setGraphic(faceButt.imageFaceSmile);
		
		maxPane.setCenter(tPane);
		maxPane.setTop(heavyPane);
		
		Scene scene = new Scene(maxPane);
		theStage.setTitle("Minesweeper");
		theStage.setScene(scene);
		theStage.show();
	}

	public static void resetGame(MSButton[][] buttons, MSButton faceButt) {
		timeLine.stop();
		MSButton.totalSpots = -1;
		bombs = 10;
		flagCounter = bombs; 
		gameOver = false;
		totalSeconds = 0;
		timeLBL.setText(labelFormatting(totalSeconds));
		minesLeft.setText(labelFormatting(flagCounter));
        faceButt.setGraphic(faceButt.imageFaceSmile);
        dummyBoard(buttons, faceButt);
	}

	public static void dummyReset(MSButton[][]buttons, MSButton faceButt, int row, int col) {
		board = newBoard(xSize, ySize, bombs, row, col);
		createGame(buttons, faceButt);
		MSButton b = buttons[row][col];
		b.setGraphic(b.image0);
		openBoard(buttons, b);
		timeLine.play();
	}
	
	public static void dummyBoard(MSButton[][] buttons, MSButton faceButt) {
		for(int i = 0; i < xSize; i++) {
			for(int j = 0; j < ySize; j++) {
				board = new int[xSize][ySize];
				buttons[i][j] = new MSButton(board[i][j], j, i, size);
				MSButton b = buttons[i][j];
				if(b.getState() == 0){
					b.setGraphic(b.imageCover);
				}					
				b.setOnMousePressed(e ->{
					MouseButton button = e.getButton();
					if(button == MouseButton.PRIMARY || button == MouseButton.SECONDARY && !gameOver) {
						faceButt.setGraphic(faceButt.imageFaceWinO);
					}
				});
				b.setOnMouseClicked(e ->{
					int row = b.col;
					int col = b.row;
					dummyReset(buttons, faceButt, row, col);
				});
				b.setOnMouseReleased(e -> {
					MouseButton button = e.getButton();
					if((button == MouseButton.PRIMARY || button == MouseButton.SECONDARY) && !gameOver) {
						faceButt.setGraphic(faceButt.imageFaceSmile);
					}
				});
				
				tPane.add(buttons[i][j], i, j);
			}
		}
	}
	
	public static void createGame(MSButton[][] buttons, MSButton faceButt) {
		for(int i = 0; i < xSize; i++) {
			for(int j = 0; j < ySize; j++) {
				buttons[i][j] = new MSButton(board[j][i], j, i, size);
				MSButton b = buttons[i][j];
				if(b.getState() == 0){
					b.setGraphic(b.imageCover);
				}
				b.setOnMousePressed(e ->{
					MouseButton button = e.getButton();
					if((button == MouseButton.PRIMARY || button == MouseButton.SECONDARY) && !gameOver) {
						faceButt.setGraphic(faceButt.imageFaceWinO);
					}
				});
				b.setOnMouseReleased(e -> {
					if(!gameOver)
						faceButt.setGraphic(faceButt.imageFaceSmile);
				});
				b.setOnMouseClicked(e ->{
					if(e.getButton() == MouseButton.PRIMARY) {
						switch(b.state){
						case 0:
							if(b.value == 0) {
								b.setGraphic(b.image0);
								openBoard(buttons, b);
								MSButton.totalSpots--;
								winCheck(buttons, faceButt);
							}else if(b.value == 1) {
								b.setGraphic(b.image1);
								b.state = 1;
								MSButton.totalSpots--;
								winCheck(buttons, faceButt);
							}else if(b.value == 2) {
								b.setGraphic(b.image2);
								b.state = 1;
								MSButton.totalSpots--;
								winCheck(buttons, faceButt);
							}else if(b.value == 3) {
								b.setGraphic(b.image3);
								b.state = 1;
								MSButton.totalSpots--;
								winCheck(buttons, faceButt);
							}else if(b.value == 4) {
								b.setGraphic(b.image4);
								b.state = 1;
								MSButton.totalSpots--;
								winCheck(buttons, faceButt);
							}else if(b.value == 5) {
								b.setGraphic(b.image5);
								b.state = 1;
								MSButton.totalSpots--;
								winCheck(buttons, faceButt);
							}else if(b.value == 6) {
								b.setGraphic(b.image6);
								b.state = 1;
								MSButton.totalSpots--;
								winCheck(buttons, faceButt);
							}else if(b.value == 7) {
								b.setGraphic(b.image7);
								b.state = 1;
								MSButton.totalSpots--;
								winCheck(buttons,faceButt);
							}else if(b.value == 8) {
								b.setGraphic(b.image8);
								b.state = 1;
								MSButton.totalSpots--;
								winCheck(buttons,faceButt);
							}else if(b.value == 9) {
								b.setGraphic(b.imageMineRed);
								faceButt.setGraphic(faceButt.imageFaceDead);
								b.state = 1;
								gameLoss(board,buttons);
							}
							break;
						case 1:
							if(b.value <= countFlags(buttons, b)) {
								openSurrounding(buttons, b, faceButt);
							}
							break;
						}
					}else if(e.getButton() == MouseButton.SECONDARY){
						if(b.getGraphic() == b.imageCover && b.state == 0) {
							b.setGraphic(b.imageFlag);
							b.state = 2;
							if(b.value == 9) {
								bombs--;
							}
							
							flagCounter--;
							String flagsInStr = labelFormatting(flagCounter);
							minesLeft.setText(flagsInStr);
						}else if(b.getGraphic() == b.imageFlag && b.state == 2){
							b.setGraphic(b.imageCover);
							b.state = 0;
							if(b.value == 9) {
								bombs++;
							}
							flagCounter++;
							String flagsInStr = labelFormatting(flagCounter);
							minesLeft.setText(flagsInStr);
						}
					}
					winCheck(buttons, faceButt);
				});
				
				MSButton.totalSpots = xSize * ySize - bombs;
				tPane.add(buttons[i][j], i, j);
			}
		}
	}
	
	public static ImageView determineImg(int val, MSButton b) {

		//System.out.println("Showing " + val);
		switch(val) {
		case 0:
			return b.image0;
		case 1:
			return b.image1;
		case 2:
			return b.image2;
		case 3:
			return b.image3;
		case 4:
			return b.image4;
		case 5:
			return b.image5;
		case 6:
			return b.image6;
		case 7:
			return b.image7;
		case 8:
			return b.image8;
		default:
			return b.imageCover;
		}
	} 

	public static void openSurrounding(MSButton[][] buttons, MSButton tar, MSButton faceButt) {
		
		int row = tar.col;
		int col = tar.row;
		
		if(isValid(row - 1, col - 1) && buttons[row - 1][col - 1].state == 0) {
			MSButton b = buttons[row - 1][col - 1];
			int val = buttons[row - 1][col - 1].value;
			if(val == 9) {
				buttons[row - 1][col - 1].setGraphic(b.imageMineRed);
				faceButt.setGraphic(faceButt.imageFaceDead);
				buttons[row - 1][col - 1].state = 1;
				gameLoss(board,buttons);
			}else if(val == 0){
				openBoard(buttons, b);
			}else{
				buttons[row - 1][col - 1].setGraphic(determineImg(val, b));
				buttons[row - 1][col - 1].state = 1;
				MSButton.totalSpots--;
			}
		}
		if(isValid(row - 1, col) && buttons[row - 1][col].state == 0) {
			MSButton b = buttons[row - 1][col];
			int val = buttons[row - 1][col].value;
			if(val == 9) {
				buttons[row - 1][col].setGraphic(b.imageMineRed);
				faceButt.setGraphic(faceButt.imageFaceDead);
				buttons[row - 1][col].state = 1;
				gameLoss(board,buttons);
			}else if(val == 0){
				openBoard(buttons, b);
			}else{
				buttons[row - 1][col].setGraphic(determineImg(val, b));
				buttons[row - 1][col].state = 1;
				MSButton.totalSpots--;
			}
		}
		if(isValid(row - 1, col + 1) && buttons[row - 1][col + 1].state == 0) {
			MSButton b = buttons[row - 1][col + 1];
			int val = buttons[row - 1][col + 1].value;
			if(val == 9) {
				buttons[row - 1][col + 1].setGraphic(b.imageMineRed);
				faceButt.setGraphic(faceButt.imageFaceDead);
				buttons[row - 1][col + 1].state = 1;
				gameLoss(board,buttons);
			}else if(val == 0){
				openBoard(buttons, b);
			}else{
				buttons[row - 1][col + 1].setGraphic(determineImg(val, b));
				buttons[row - 1][col + 1].state = 1;
				MSButton.totalSpots--;
			}
		}
		if(isValid(row, col - 1) && buttons[row][col - 1].state == 0) {
			MSButton b = buttons[row][col - 1];
			int val = buttons[row][col - 1].value;
			if(val == 9) {
				buttons[row][col - 1].setGraphic(b.imageMineRed);
				faceButt.setGraphic(faceButt.imageFaceDead);
				buttons[row][col - 1].state = 1;
				gameLoss(board,buttons);
			}else if(val == 0){
				openBoard(buttons, b);
			}else{
				buttons[row][col - 1].setGraphic(determineImg(val, b));
				buttons[row][col - 1].state = 1;
				MSButton.totalSpots--;
			}
		}
		if(isValid(row, col + 1) && buttons[row][col + 1].state == 0) {
			MSButton b = buttons[row][col + 1];
			int val = buttons[row][col + 1].value;
			if(val == 9) {
				buttons[row][col + 1].setGraphic(b.imageMineRed);
				faceButt.setGraphic(faceButt.imageFaceDead);
				buttons[row][col + 1].state = 1;
				gameLoss(board,buttons);
			}else if(val == 0){
				openBoard(buttons, b);
			}else{
				buttons[row][col + 1].setGraphic(determineImg(val, b));
				buttons[row][col + 1].state = 1;
				MSButton.totalSpots--;
			}
		}
		if(isValid(row + 1, col - 1) && buttons[row + 1][col - 1].state == 0) {
			MSButton b = buttons[row + 1][col - 1];
			int val = buttons[row + 1][col - 1].value;
			if(val == 9) {
				buttons[row + 1][col - 1].setGraphic(b.imageMineRed);
				faceButt.setGraphic(faceButt.imageFaceDead);
				buttons[row + 1][col - 1].state = 1;
				gameLoss(board,buttons);
			}else if(val == 0){
				openBoard(buttons, b);
			}else{
				buttons[row + 1][col - 1].setGraphic(determineImg(val, b));
				buttons[row + 1][col - 1].state = 1;
				MSButton.totalSpots--;
			}
		}
		if(isValid(row + 1, col) && buttons[row + 1][col].state == 0) {
			MSButton b = buttons[row + 1][col];
			int val = buttons[row + 1][col].value;
			if(val == 9) {
				buttons[row + 1][col].setGraphic(b.imageMineRed);
				faceButt.setGraphic(faceButt.imageFaceDead);
				buttons[row + 1][col].state = 1;
				gameLoss(board,buttons);
			}else if(val == 0){
				openBoard(buttons, b);
			}else{
				buttons[row + 1][col].setGraphic(determineImg(val, b));
				buttons[row + 1][col].state = 1;
				MSButton.totalSpots--;
			}
		}
		if(isValid(row + 1, col + 1) && buttons[row + 1][col + 1].state == 0) {
			MSButton b = buttons[row + 1][col + 1];
			int val = buttons[row + 1][col + 1].value;
			if(val == 9) {
				buttons[row + 1][col + 1].setGraphic(b.imageMineRed);
				faceButt.setGraphic(faceButt.imageFaceDead);
				buttons[row + 1][col + 1].state = 1;
				gameLoss(board,buttons);
			}else if(val == 0){
				openBoard(buttons, b);
			}else{
				buttons[row + 1][col + 1].setGraphic(determineImg(val, b));
				buttons[row + 1][col + 1].state = 1;
				MSButton.totalSpots--;
			}
			
		}
	}
	
	public static int countFlags(MSButton[][] buttons, MSButton b) {
		int flagCount = 0;
		int row = b.col;
		int col = b.row;
		
		if(isValid(row - 1, col - 1) && buttons[row - 1][col - 1].state == 2) {
			flagCount++;
		}
		if(isValid(row - 1, col) && buttons[row - 1][col].state == 2) {
			flagCount++;
		}
		if(isValid(row - 1, col + 1) && buttons[row - 1][col + 1].state == 2) {
			flagCount++;
		}
		if(isValid(row, col - 1) && buttons[row][col - 1].state == 2) {
			flagCount++;
		}
		if(isValid(row, col + 1) && buttons[row][col + 1].state == 2) {
			flagCount++;
		}
		if(isValid(row + 1, col - 1) && buttons[row + 1][col - 1].state == 2) {
			flagCount++;
		}
		if(isValid(row + 1, col) && buttons[row + 1][col].state == 2) {
			flagCount++;
		}
		if(isValid(row + 1, col + 1) && buttons[row + 1][col + 1].state == 2) {
			flagCount++;
		}
		return flagCount;
	}
	
	public static int[][] newBoard(int xSize, int ySize, int bombs, int y, int x){
		int bomba = bombs;
		int[][] nBoard = new int[xSize][ySize];
		while(bomba > 0) {
			int row = (int)(Math.random() * xSize);
			int col = (int)(Math.random() * ySize);
			int randomNumb = (int)(Math.random() * 100);
	
			if(randomNumb % 10 == 0 && nBoard[row][col] != 9 && !aroundFirst(x, y, row, col)) {
				nBoard[row][col] = 9;
				bomba--;
				nBoard = surroundBomb(nBoard, xSize, ySize, row, col);
			}
		}
		return nBoard;
	}

	public static String labelFormatting(int numb) {
		String sentence;
		if(numb >= 999) {
			sentence = Integer.toString(999);
		}else if(numb >= 100) {
			sentence = Integer.toString(numb);
		}else if(numb >= 10) {
			sentence = "0" + Integer.toString(numb);
		}else if(numb >= (0)) {
			sentence = "00" + Integer.toString(numb);
		}else if(numb >= (-9)){
			sentence = "-0" + Integer.toString(-1 * numb);
		}else{
			sentence = "-" + Integer.toString(-1 * numb);
		}
		return sentence;
	}
	
	public static void gameLoss(int[][] board, MSButton[][] buttons) {
		timeLine.stop();
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col++) {
				MSButton b = buttons[col][row]; //Backwards because JavaFX
				if(b.value == 9 && b.state == 0) {
					//b.state = 1;
					b.setGraphic(b.imageMineGrey);
				}else if(b.getGraphic() == b.imageFlag && b.value != 9) {
					b.setGraphic(b.imageMineMisplaced);
				}
				b.state = 3;
			}
		}
		gameOver = true;
	}
	
	public static void openBoard(MSButton[][] buttons, MSButton b) {
		int row = b.col;
		int col = b.row;
		if(b.state != 0)
			return;
		
		if(b.value != 0) {
			ImageView img;
			if(b.value == 9) {
				System.out.println("Wrong at " + b.row + ", " + b.col);
			}else {
				img = (new ImageView(new Image("file:res/" + b.value + ".png")));
				img.setFitWidth(size);
				img.setFitHeight(size);
				b.setGraphic(img);
			}
			b.state = 1;
			MSButton.totalSpots--;
		}else{
			//System.out.println("0 at " + row + ", " + col);
			ImageView img = (new ImageView(new Image("file:res/" + b.value + ".png")));
			//b.setGraphic(b.image0);
			img.setFitWidth(size);
			img.setFitHeight(size); 
			b.setGraphic(img);
			b.state = 1;
			MSButton.totalSpots--;
			
			if(isValid(row - 1, col - 1)) {
				openBoard(buttons, buttons[row - 1][col - 1]);
			}
			if(isValid(row - 1, col)) {
				openBoard(buttons, buttons[row - 1][col]);
			}
			if(isValid(row - 1, col + 1)) {
				openBoard(buttons, buttons[row - 1][col + 1]);
			}	
			if(isValid(row, col - 1)) {
				openBoard(buttons, buttons[row][col - 1]);
			}
			if(isValid(row, col + 1)) {
				openBoard(buttons, buttons[row][col + 1]);
			}
			if(isValid(row + 1, col - 1)) {
				openBoard(buttons, buttons[row + 1][col - 1]);
			}
			if(isValid(row + 1, col)) {
				openBoard(buttons, buttons[row + 1][col]);
			}
			if(isValid(row + 1, col + 1)) {
				openBoard(buttons, buttons[row + 1][col + 1]);
			}
		}
	}
	
	public static boolean aroundFirst(int row, int col, int bRow, int bCol) {
		if(isValid(row - 1, col - 1) && !(bRow != row - 1 || bCol != col - 1)){
			return true;
		}else if(isValid(row - 1, col) && !(bRow != row - 1  || bCol != col)){
			return true;
		}else if(isValid(row - 1, col + 1) && !(bRow != row - 1 || bCol != col + 1)){
			return true;
		}else if(isValid(row, col - 1) && !(bRow != row || bCol != col - 1)){
			return true;
		}else if(isValid(row, col) && !(bRow != row || bCol != col)){
			return true;
		}else if(isValid(row, col + 1) && !(bRow != row || bCol != col + 1)){
			return true;
		}else if(isValid(row + 1 , col - 1) && !(bRow != row + 1 || bCol != col - 1)){
			return true;
		}else if(isValid(row + 1, col) && !(bRow != row + 1|| bCol != col)){
			return true;
		}else if(isValid(row + 1, col + 1) && !(bRow != row + 1|| bCol != col + 1)){
			return true;
		}else{
			return false;
		}
	}
	
	public static int[][] surroundBomb(int[][] board, int xSize, int ySize, int row, int col){
		int[][] nBoard = board;
		if(isValid(row - 1, col - 1) && nBoard[row - 1][col - 1] != 9) {
			nBoard[row - 1][col - 1]++;
		}
		if(isValid(row - 1, col) && nBoard[row - 1][col] != 9) {
			nBoard[row - 1][col]++;
		}
		if(isValid(row - 1, col + 1) && nBoard[row - 1][col + 1] != 9) {
			nBoard[row - 1][col + 1]++;
		}
		if(isValid(row, col - 1) && nBoard[row][col - 1] != 9) {
			nBoard[row][col - 1]++;
		}
		if(isValid(row, col + 1) && nBoard[row][col + 1] != 9) {
			nBoard[row][col + 1]++;
		}
		if(isValid(row + 1, col - 1) && nBoard[row + 1][col - 1] != 9) {
			nBoard[row + 1][col - 1]++;
		}
		if(isValid(row + 1, col) && nBoard[row + 1][col] != 9) {
			nBoard[row + 1][col]++;
		}
		if(isValid(row + 1, col + 1) && nBoard[row + 1][col + 1] != 9) {
			nBoard[row + 1][col + 1]++;
		}
		return nBoard;
	}
	
	public static boolean isValid(int row, int col) {
		return (row >= 0 && row < xSize) && (col >= 0  && col < ySize);
	}
	
	public static void winCheck(MSButton[][] buttons, MSButton faceButt) {
		if(MSButton.totalSpots <= 0 && bombs == 0) {
			timeLine.stop();
			faceButt.setGraphic(faceButt.imageFaceWin);
			for(int row = 0; row < board.length; row++) {
				for(int col = 0; col < board[row].length; col++) {
					MSButton b = buttons[col][row]; //Backwards because JavaFX
					b.state = 3;
				}
			}
			gameOver = true;
		}
	}
	
	public static void pBoard() {
		for(int i = 0; i < xSize; i++) {
			for(int j = 0; j < ySize; j++) {
				System.out.print(board[i][j] + ", ");
			}
			System.out.println();
		}
	}
}

class CustomPane extends StackPane {
	public CustomPane(MSButton faceButt) {
		getChildren().add(faceButt);
		setPadding(new Insets(15,15,15,15));
	}
	public CustomPane(String info) {
		getChildren().add(new Label(info));
		setPadding(new Insets(15,15,15,15));
	}
}

class MSButton extends Button {
	double size;
	int row, col, value;
	int state; // 0 = unfound, 1 = found, 2 = flagged
	static int totalSpots;
	ImageView image0, image1, image2, image3, image4, image5, image6, image7, image8;
	ImageView imageMineRed, imageMineGrey,imageMineRevealed;
	ImageView imageCover;
	ImageView imageFaceSmile, imageFaceWin, imageFaceWinO, imageFaceDead;
	ImageView imageFlag, imageMineMisplaced;
	
	public MSButton(double size) {
		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);
		imageFaceSmile = new ImageView(new Image("file:res/face-smile.png"));
		imageFaceWin = new ImageView(new Image("file:res/face-win.png"));
		imageFaceDead = new ImageView(new Image("file:res/face-dead.png"));
		imageFaceWinO = new ImageView(new Image("file:res/face-O.png"));

		imageFaceSmile.setFitWidth(size);
		imageFaceSmile.setFitHeight(size);
		imageFaceWin.setFitWidth(size);
		imageFaceWin.setFitHeight(size);
		imageFaceDead.setFitWidth(size);
		imageFaceDead.setFitHeight(size);
		imageFaceWinO.setFitWidth(size);
		imageFaceWinO.setFitHeight(size);
	}
	public MSButton(int value, int row, int col, double size) {
		this.value = value;
		this.state = 0;
		this.row = row;
		this.col = col;
		
		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);
		if(totalSpots == -1) {
			totalSpots = 1;
		}else if(value != 9) {
			totalSpots++;
		}
		
		imageMineRed = new ImageView(new Image("file:res/mine-red.png"));
		imageMineGrey = new ImageView(new Image("file:res/mine-grey.png"));
		imageCover = new ImageView(new Image("file:res/cover.png"));
		imageFlag = new ImageView(new Image("file:res/flag.png"));
		imageMineMisplaced = new ImageView(new Image("file:res/mine-misflagged.png"));
		
		imageMineRed.setFitWidth(size);
		imageMineRed.setFitHeight(size);
		imageMineGrey.setFitWidth(size);
		imageMineGrey.setFitHeight(size);
		imageCover.setFitWidth(size);
		imageCover.setFitHeight(size);
		imageMineMisplaced.setFitWidth(size);
		imageMineMisplaced.setFitHeight(size);
		imageFlag.setFitWidth(size);
		imageFlag.setFitHeight(size);
		
		image0 = new ImageView(new Image("file:res/0.png"));
		image1 = new ImageView(new Image("file:res/1.png"));
		image2 = new ImageView(new Image("file:res/2.png"));
		image3 = new ImageView(new Image("file:res/3.png"));
		image4 = new ImageView(new Image("file:res/4.png"));
		image5 = new ImageView(new Image("file:res/5.png"));
		image6 = new ImageView(new Image("file:res/6.png"));
		image7 = new ImageView(new Image("file:res/7.png"));
		image8 = new ImageView(new Image("file:res/8.png"));
		
		

		image0.setFitWidth(size);
		image0.setFitHeight(size);
		image1.setFitWidth(size);
		image1.setFitHeight(size);
		image2.setFitWidth(size);
		image2.setFitHeight(size);
		image3.setFitWidth(size);
		image3.setFitHeight(size);
		image4.setFitWidth(size);
		image4.setFitHeight(size);
		image5.setFitWidth(size);
		image5.setFitHeight(size);
		image6.setFitWidth(size);
		image6.setFitHeight(size);
		image7.setFitWidth(size);
		image7.setFitHeight(size);
		image8.setFitWidth(size);
		image8.setFitHeight(size);
	}
	
	public int getState() {
		return state;
	}
	public void resetGame(int xSize, int ySize) {
		state = 0;
		setGraphic(imageCover);
	}
}
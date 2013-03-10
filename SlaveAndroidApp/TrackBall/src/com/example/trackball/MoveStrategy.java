package com.example.trackball;

import java.util.ArrayList;

public class MoveStrategy {
	private float centerX;
	private float centerY;
	private int step = 5;

	public MoveStrategy(float centerX, float centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
	}

	public ArrayList<Move> getMoves(float positionX, float positionY) {
		ArrayList<Move> moves = new ArrayList<Move>();
		float positionXCentered = positionX - centerX;
		float positionYCentered = positionY - centerY;
		Move move;
		if (positionXCentered < 0) {
			move = new MoveLeft(-positionXCentered);
			moves.add(move);
		}
		if (positionXCentered > 0) {
			move = new MoveRight(positionXCentered);
			moves.add(move);
		}
		if (positionYCentered > 0) {
			move = new MoveBack(-positionYCentered);
			moves.add(move);
		}
		if (positionYCentered < 0) {
			move = new MoveFront(positionYCentered);
			moves.add(move);
		}
		return moves;
	}

}

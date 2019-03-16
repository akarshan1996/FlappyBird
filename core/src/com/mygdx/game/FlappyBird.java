package com.mygdx.game;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;
public class FlappyBird extends ApplicationAdapter {

	SpriteBatch batch;

	Texture background;
	Texture gameover;
	Texture[] birds;
	Texture topTube;
	Texture bottomTube;

	Circle birdCircle;
	BitmapFont font;		//to display score

	Random randomGenerator;

	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	int score = 0;
	int scoringTube = 0;
	int gameState = 0;
	float gravity = 2;
	float gap = 370;
	float maxTubeOffset;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	@Override
	public void create () {
		batch = new SpriteBatch();

		background = new Texture("bg.png");
		gameover = new Texture("gameover.png");

		birdCircle = new Circle();

		font = new BitmapFont();	//font of score
		font.setColor(Color.WHITE);	//color of score
		font.getData().setScale(10);	//scale of score

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;	//the tube can start from the top or bottom end of screen
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;	//distance between two tubes

		randomGenerator = new Random();


		startGame();
	}

	public void startGame() {
		//at starting
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;	//to take the bird in middle of screen

		for (int i = 0; i < numberOfTubes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);	//random tubes with different position
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;	//first tube will start from right of screen

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {

				if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

					score++;

				Gdx.app.log("Score", String.valueOf(score));

				if (scoringTube < numberOfTubes - 1) {

							scoringTube++;
				} else {
					scoringTube = 0;
				}

			}

			if (Gdx.input.justTouched()) {	//to take the bird upwards as we tap
				velocity = -25;
			}

			for (int i = 0; i < numberOfTubes; i++) {

					if (tubeX[i] < - topTube.getWidth()) {

						tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
						tubeX[i] += numberOfTubes * distanceBetweenTubes;		//to take the tube to right as soon it moves to left

				} else {
						tubeX[i] = tubeX[i] - tubeVelocity;	//moving the tube to left
					}

					batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);//drawing top tube
					batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);	//drawing bottom tube

					topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());	//drawing rectangle on top tube
					bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());	//drawing rectangle on bottom tube
			}

			if (birdY > 0) {		//to take the bird downwards as we leave
				velocity = velocity + gravity;	//till the bird is above screen
				birdY -= velocity;
			} else {				//game will be over as bird we go below the screen
				gameState = 2;
			}



		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		}



		else if (gameState == 2) {

			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

			if (Gdx.input.justTouched()) {

				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;

			}
		}


		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}


		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);	//to draw both birds with diff wings acc to flapstate

		font.draw(batch, String.valueOf(score), 70, 250);	//to draw the score on the screen

		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);	//setting circle as bird

		for (int i = 0; i < numberOfTubes; i++) {
			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {	//detecting collision
				gameState = 2;	//game over
			}
		}
		batch.end();
	}

}

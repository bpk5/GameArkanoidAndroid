package com.mygdx.bpk5;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * @author Bartłomiej Kulesa
 */
public class MyGdxGame extends ApplicationAdapter {

	//private int touchX; // Pozycja dotknięcia
	//private boolean isTouched; // Czy zostało dotknięte
	private boolean go;
	private boolean gameOver;
	private Integer live; // Zycia
	private int xMin; // Pozycja minimalna paddle
	private int xMax; // Pozycja maksymalna paddle
	private float stepBallX; // Krok o jaki będzie poruszać się piłka
	private float stepBallY; // Krok o jaki będzie poruszać się piłka
	private int stepPaddle; // Krok o jaki będzie poruszać się paletka


	private BitmapFont font, font2;
	private Music musicBall, musicMusic;
	private SpriteBatch batch;
	private Texture paddleTexture, blockTexture, ballTexture, backgroundTexture;
	private GameObject gameObjectPaddle, gameObjectBall, gameObjectBackground, gameObjectBlock;
	private Array<GameObject> gameObjectBlockArray; // Tablica obiektów, bloków.


	@Override
	public void create () {
		loadData();
		batch = new SpriteBatch();
		gameOver = false;
		initial();
	}

	private void initial() {
		go = false;
		live = 3;
		xMin = 0;
		xMax = 480;
		stepBallX = 100;
		stepBallY = 100;
		stepPaddle = 200;
		font = new BitmapFont();
		font2 = new BitmapFont();
		gameObjectBackground = new GameObject(backgroundTexture, null);
		gameObjectPaddle = new GameObject(paddleTexture, null);
		gameObjectPaddle.x = 250;
		gameObjectPaddle.y = 150;
		gameObjectBall = new GameObject(ballTexture, musicBall);
		gameObjectBall.x = 275;
		gameObjectBall.y = 175;
		gameObjectBlockArray = new Array<GameObject>();

		// Czyszczenie listy.
		gameObjectBlockArray.clear();
		// Przygotowanie bloków.
		for (int i = 0; i <= 6; i++) {
			for (int j = 0; j <= 5; j++) {
				gameObjectBlock = new GameObject(blockTexture, null);
				// gameObjectBlock.width + margin + x
				gameObjectBlock.x = ((gameObjectBlock.width + 10) * i) + 70;
				// gameObjectBlock.width + margin + y
				gameObjectBlock.y = ((gameObjectBlock.height + 10) * j) + 700;
				gameObjectBlockArray.add(gameObjectBlock);
			}
		}

		musicMusic.play(); // Włączenie muzyki
	}

	/**
	 * Ładowanie obrazków, muzyki.
	 */
	private void loadData() {
		musicBall = Gdx.audio.newMusic(Gdx.files.internal("przelacznik.mp3"));
		musicMusic = Gdx.audio.newMusic(Gdx.files.internal("music1.mp3"));
		paddleTexture = new Texture("paletka2.png");
		blockTexture = new Texture("klocek.png");
		ballTexture = new Texture("small_ball.png");
		backgroundTexture = new Texture("tlo.jpg");
	}

	@Override
	public void render () {
		update();

		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin(); // Początek
		batch.draw(gameObjectBackground.getTexture(), 0, 0);
		batch.draw(gameObjectPaddle.getTexture(), gameObjectPaddle.x, gameObjectPaddle.y);
		batch.draw(gameObjectBall.getTexture(), gameObjectBall.x, gameObjectBall.y);
		font.draw(batch, live.toString() , 30, 930);


		for (int i = 0; i < gameObjectBlockArray.size; i++) {
			batch.draw(gameObjectBlockArray.get(i).getTexture(), gameObjectBlockArray.get(i).x, gameObjectBlockArray.get(i).y);
		}

		// Koniec gry
		if (gameOver) {
			font2.draw(batch, "GAME OVER", 230, 300);
		}

		batch.end(); // Koniec
	}

	private void update() {
		if (live == 0) {
			gameOver = true;
			initial();
		}
		// Jeżeli dotknięte to uruchom wszystko.
		boolean isTouched = Gdx.input.isTouched();
		if (isTouched) {
			go = true;
			gameOver = false;
		}

		if (go) {

			// Obsługa dotyku
			int touchX = Gdx.input.getX(); // pozycja x touch
			isTouched = Gdx.input.isTouched(); // czy zostało dotknięte
			// Dotknięte w odpowiednim miejscu i dotknięte i paddle jest większe od xMin
			if ((touchX < 200) && (isTouched) && (gameObjectPaddle.x > xMin)) {
				gameObjectPaddle.x -= stepPaddle * Gdx.graphics.getDeltaTime();
			}
			// Dotknięte w odpowiednim miejscu i dotknięte i paddle jest mniejsze od xMax
			if ((touchX > 250) && (isTouched) && (gameObjectPaddle.x < xMax)) {
				gameObjectPaddle.x += stepPaddle * Gdx.graphics.getDeltaTime();
			}

			// Poruszanie ball
			gameObjectBall.x += stepBallX * Gdx.graphics.getDeltaTime();
			gameObjectBall.y += stepBallY * Gdx.graphics.getDeltaTime();

			collisionDetection();
		}
	}

	/**
	 * Wykrywanie kolizji.
	 */
	private void collisionDetection() {
		// Wykrywanie kolizji: ball vs paddle ******************************************************
		if (gameObjectPaddle.overlaps(gameObjectBall)) {
			//stepBallY = -stepBallY;
			//gameObjectBall.playMusic();
			float stepBasicX = 250; // Wartość bazowa - największa
			float mno = stepBasicX / (gameObjectPaddle.width / 2);
			float centerBallX = gameObjectBall.getX() + gameObjectBall.width / 2; // Środek piłki

			// Spr., w którą część uderzyło: lewa, prawa
			if ((gameObjectBall.getX() + gameObjectBall.width / 2) <=
					(gameObjectPaddle.getX() + gameObjectPaddle.width / 2)) {
				// Odbicie po lewej.

				// Ustalenie innego kąta odbicia
				stepBallX = stepBasicX - ((centerBallX - gameObjectPaddle.getX()) * mno);

				// Piłka nadlatuje od lewej strony.
				if (stepBallX > 0) {
					stepBallX = -stepBallX;
				}
			}
			else {
				// Odbicie po prawej.

				// Ustalenie innego kąta odbicia
				stepBallX = ((centerBallX - gameObjectPaddle.getX())
						- (gameObjectPaddle.width / 2)) * mno;

				// Piłka nadlatuje od prawej strony.
				if (stepBallX < 0) {
					stepBallX = -stepBallX;
				}
			}
			stepBallY = -stepBallY;
		}
		// Koniec kolizji: ball vs paddle

		// Wykrywanie kolizji: ball vs ściany ******************************************************
		if (gameObjectBall.x <= gameObjectBackground.getX()) {
			// Wykrycie po lewej stronie.
			stepBallX = -stepBallX;
			gameObjectBall.playMusic();
		}
		else if (gameObjectBall.x >= gameObjectBackground.width - gameObjectBall.width) {
			// Wykrycie po prawej stronie.
			stepBallX = -stepBallX;
			gameObjectBall.playMusic();
		}
		else if (gameObjectBall.y >= gameObjectBackground.height - gameObjectBall.height) {
			// Wykrywanie na górze
			stepBallY = -stepBallY;
			gameObjectBall.playMusic();
		}
		else if (gameObjectBall.y <= gameObjectBackground.getY()) {
			// Wykrywanie na dole
			go = false;
			gameObjectPaddle.x = 250;
			gameObjectPaddle.y = 150;

			gameObjectBall.x = 275;
			gameObjectBall.y = 175;
			live--;
		}


		// Wykrywanie kolizji: ball vs block *******************************************************
		for (int i = 0; i < gameObjectBlockArray.size; i++) {
			if (gameObjectBall.overlaps(gameObjectBlockArray.get(i))) {
				gameObjectBlockArray.removeIndex(i);
				stepBallY = -stepBallY;
				gameObjectBall.playMusic();

			}
		}
	}

	@Override
	public void dispose () {
		font2.dispose();
		font.dispose();
		musicBall.dispose();
		musicMusic.dispose();
		batch.dispose();
		paddleTexture.dispose();
		blockTexture.dispose();
		ballTexture.dispose();
		backgroundTexture.dispose();
	}
}

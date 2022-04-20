package ca.dracula.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.css.Rect;


import java.util.Iterator;

public class GameScreen implements Screen {
    final Drop game;

    Sprite enemyImage;
    Sprite vampireImage;
    Sound dropSound;
    Sprite batImage;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle vampire;
    Array<Enemy> enemies;
    Array<Rectangle> bats;
    Vector3 touchPos = new Vector3();
    long lastSpawnTime;
    long lastProjTime;
    long attackInterval;
    double attackSpeed;
    int damage;
    int seconds;
    int minutes;
    String time;
    int health;
    double speed;
    double screenMoveX;
    double screenMoveY;
    int upgrades;

    public GameScreen(final Drop game) {
        this.game = game;

        // load the images for the droplet and the bucket, 64x64 pixels each
        enemyImage = new Sprite(new Texture(Gdx.files.internal("garlic.png")));
        vampireImage = new Sprite(new Texture(Gdx.files.internal("vampire.png")));
        batImage = new Sprite(new Texture((Gdx.files.internal("bat.png"))));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket
        vampire = new Rectangle();
        vampire.x = 800 / 2 - 36 / 2; // center the bucket horizontally
        vampire.y = 20; // bottom left corner of the bucket is 20 pixels above
                                        // the bottom screen edge

        // initialize variables
        seconds = 0;
        minutes = 0;
        time = "0:00";
        health = 10;
        attackInterval = 1200000000;
        upgrades = 0;
        attackSpeed = 125;
        damage = 5;


        vampire.width = 36;
        vampire.height = 64;

        // create the bats array and spawn the first bat
        bats = new Array<Rectangle>();
        spawnBat();

        // create the raindrops array and spawn the first enemy
        enemies = new Array<Enemy>();
        spawnEnemy();
    }

    private void spawnEnemy() {
        Enemy enemy = new Enemy(10 + (int)Math.pow(minutes, 2), 2, 0, 1.5, vampireImage);
        enemy.spawn();
        enemies.add(enemy);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnBat() {
        Rectangle bat = new Rectangle(vampire.getX(), vampire.getY(), 31, 21);
        bats.add(bat);
        lastProjTime = TimeUtils.nanoTime();
    }

    private void attack(Rectangle bat) {
        double temp = attackSpeed;
        Enemy closestEnemy = enemies.first();
        for (Iterator<Enemy> iter = enemies.iterator(); iter.hasNext(); ) {
            Enemy enemy = iter.next();
            if(Math.sqrt(Math.pow(enemy.getX() - bat.x, 2) + Math.pow(enemy.getY() - bat.y, 2)) < Math.sqrt(Math.pow(closestEnemy.getX() - bat.x, 2) + Math.pow(closestEnemy.getY() - bat.y, 2))) {
                closestEnemy = enemy;
            }
        }
        if (bat.x != closestEnemy.x && bat.y != closestEnemy.y) {
            attackSpeed = Math.sqrt(Math.pow(attackSpeed, 2) * 2) / 2;
        }

        if (bat.y > closestEnemy.y) {
            bat.y -= attackSpeed * Gdx.graphics.getDeltaTime();
        } else {
            bat.y += attackSpeed * Gdx.graphics.getDeltaTime();
        }

        if (bat.x > closestEnemy.x) {
            bat.x -= attackSpeed * Gdx.graphics.getDeltaTime();
        } else {
            bat.x += attackSpeed * Gdx.graphics.getDeltaTime();
        }
        attackSpeed = temp;
    }

    @Override
    public void render(float delta) {
        double speedMulti = 1.0;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            speedMulti = 2.5;
            vampireImage.setColor(Color.RED);
        } else {
            vampireImage.setColor(Color.WHITE);
        }

        speed = 200;
        if((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
            speed = Math.sqrt(Math.pow(speed, 2) * 2) / 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) vampire.x -= speed * speedMulti * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) vampire.x += speed * speedMulti * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) vampire.y += speed * speedMulti * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) vampire.y -= speed * speedMulti * Gdx.graphics.getDeltaTime();

        if(upgrades > 0) {
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                health += 5;
                upgrades--;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                speed *= 1.1;
                upgrades--;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                attackInterval *= 0.95;
                upgrades--;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                attackSpeed += 5;
                upgrades--;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
                damage *= 1.2;
                upgrades--;
            }

        }

        screenMoveX = 0;
        screenMoveY = 0;
        if (vampire.x < 0) {
            screenMoveX = speed * speedMulti * Gdx.graphics.getDeltaTime();
            vampire.x = 0;
        }
        if (vampire.x > 800 - 36) {
            screenMoveX = -1 * speed * speedMulti * Gdx.graphics.getDeltaTime();
            vampire.x = 800 - 36;
        }
        if (vampire.y < 0) {
            screenMoveY = speed * speedMulti * Gdx.graphics.getDeltaTime();
            vampire.y = 0;
        }
        if (vampire.y > 480 - 64) {
            screenMoveY = -1 * speed * speedMulti * Gdx.graphics.getDeltaTime();
            vampire.y = 480 - 64;
        }

        if (TimeUtils.nanoTime() - lastProjTime > attackInterval) {
            spawnBat();
        }

        if (TimeUtils.nanoTime() - lastSpawnTime > 1000000000) {
            spawnEnemy();
            seconds++;
            if(seconds % 30 == 0){
                upgrades++;
            }
            if (seconds >= 60) {
               seconds -= 60;
               minutes++;
            }
            if(seconds < 10) {
                time = minutes + ":0" + seconds;
            } else {
                time = minutes + ":" + seconds;
            }
        }
        for (Iterator<Enemy> iter = enemies.iterator(); iter.hasNext(); ) {
            Enemy enemy = iter.next();

            if (TimeUtils.nanoTime() - lastSpawnTime > 500000000 && TimeUtils.nanoTime() - lastSpawnTime < 510000000 ) {
                enemy.changeDirection();
            }
            enemy.move(vampire, screenMoveX, screenMoveY);

            if (enemy.y + 64 < 0) iter.remove();
            if (enemy.overlaps(vampire)) {
                health--;
                dropSound.play();
                iter.remove();
                if(health <= 0) game.create();
            }
        }
        for (Iterator<Rectangle> iter = bats.iterator(); iter.hasNext(); ) {
            Rectangle bat = iter.next();
            attack(bat);
            for (Iterator<Enemy> iter1 = enemies.iterator(); iter1.hasNext(); ) {
                Enemy enemy = iter1.next();
                if(bat.overlaps(enemy)) {
                    enemy.health -= damage;
                    if(enemy.health <= 0) {
                        iter1.remove();
                    }
                    iter.remove();
                }
            }
        }

        ScreenUtils.clear(139,0,0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.draw(game.batch, "Lives: " + health, 0, 400);
        game.font.draw(game.batch, time, 400, 460);
        game.font.draw(game.batch, "Upgrades Available: " + upgrades, 600, 460);
        game.batch.draw(vampireImage, vampire.x, vampire.y, vampire.width, vampire.height);
        for (Enemy enemy : enemies) {
            game.batch.draw(enemyImage, enemy.x, enemy.y);
        }
        for (Rectangle bat : bats) {
            game.batch.draw(batImage, bat.x, bat.y);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        // dropImage.dispose();
        // bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}

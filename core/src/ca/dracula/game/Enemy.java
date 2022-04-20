package ca.dracula.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;


public class Enemy extends Rectangle {
    int health;
    int collisonDmg;
    int projectileDmg;
    int direction;
    double speed;
    Sprite image;

    public Enemy(int health, int collisionDmg, int projectileDmg, double speed, Sprite image) {
        this.health = health;
        this.collisonDmg = collisionDmg;
        this.projectileDmg = projectileDmg;
        this.speed = speed;
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        direction = MathUtils.random(1, 8);
    }

    public void changeDirection() {
        direction = MathUtils.random(1, 8);
    }

    // collides enemy with player
    public int collide(int playerHealth) {
        return playerHealth - collisonDmg;
    }

    // moves enemy towards player
    public void move(Rectangle player, double screenMoveX, double screenMoveY) {
        x += screenMoveX;
        y += screenMoveY;
        int percentSmart = 0;
        if(Math.sqrt(Math.pow(player.getX() - x, 2) + Math.pow(player.getY() - y, 2)) < 300) {
            percentSmart = 85;
        }
        double speed = this.speed;
        if (MathUtils.random(0, 100) < percentSmart) {
            if (x != player.x && y != player.y) {
                speed = Math.sqrt(Math.pow(this.speed, 2) * 2) / 2;
            }

            if (y > player.y) {
                y -= speed * height * Gdx.graphics.getDeltaTime();
            } else {
                y += speed * height * Gdx.graphics.getDeltaTime();
            }

            if (x > player.x) {
                x -= speed * width * Gdx.graphics.getDeltaTime();
            } else {
                x += speed * width * Gdx.graphics.getDeltaTime();
            }
        } else {
            switch (direction) {
                case 1:
                    x -= speed * width * Gdx.graphics.getDeltaTime();
                    break;
                case 2:
                    x += speed * width * Gdx.graphics.getDeltaTime();
                    break;
                case 3:
                    y -= speed * height * Gdx.graphics.getDeltaTime();
                    break;
                case 4:
                    y += speed * height * Gdx.graphics.getDeltaTime();
                    break;
                default:
                    speed = Math.sqrt(Math.pow(this.speed, 2) * 2) / 2;
                case 5:
                    x -= speed * width * Gdx.graphics.getDeltaTime();
                    y -= speed * height * Gdx.graphics.getDeltaTime();
                    break;
                case 6:
                    x += speed * width * Gdx.graphics.getDeltaTime();
                    y += speed * height * Gdx.graphics.getDeltaTime();
                    break;
                case 7:
                    y -= speed * height * Gdx.graphics.getDeltaTime();
                    x += speed * width * Gdx.graphics.getDeltaTime();
                    break;
                case 8:
                    y += speed * height * Gdx.graphics.getDeltaTime();
                    x -= speed * width * Gdx.graphics.getDeltaTime();
                    break;
            }

        }
    }

    public void spawn() {
        if (MathUtils.random(0, 1) < 0.5) {
            x = MathUtils.random(0, 800 - width);
            y = MathUtils.random(0, 1) * (480 - height);
        } else {
            x = MathUtils.random(0, 1) * (800 - width);
            y = MathUtils.random(0, 480 - height);
        }
    }
}

package com.mygdx.bpk5;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;


/**
 * Created by bpk on 16.06.17.
 * @author Bartłomiej Kulesa
 *
 */

public class GameObject extends Rectangle {

    private Texture texture;
    private Music music;

    public GameObject(Texture texture, Music music) {
        this.texture = texture;
        this.width = texture.getWidth();
        this.height = texture.getHeight();

        this.music = music;
    }

    public Texture getTexture() {
        return texture;
    }

    public void playMusic() {
        music.play();
    }
}

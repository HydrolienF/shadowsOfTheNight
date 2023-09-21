package fr.formiko.shadowsofthenigth;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Obstacle extends Image {
    private Body body;
    public Obstacle(String textureName, int width, int height, int x, int y) {
        super(new Texture("images/" + textureName + ".png"));
        createBody(width, height, x, y);
        body.setUserData(this);
        setBounds(x, y, width, height);
    }
    public Obstacle(int width, int height, int x, int y) { this(null, width, height, x, y); }


    public void createBody(int width, int height, int x, int y) {
        // Create our body definition
        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(x + width / 2f, y + height / 2f));
        // Create a body from the definition and add it to the world
        body = ShadowsOfTheNight.world.createBody(groundBodyDef);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(width / 2f, height / 2f);
        // Create a fixture from our polygon shape and add it to our ground body
        body.createFixture(groundBox, 0.0f);

        // Clean up after ourselves
        groundBox.dispose();
    }
}

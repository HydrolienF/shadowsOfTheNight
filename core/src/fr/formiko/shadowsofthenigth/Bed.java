package fr.formiko.shadowsofthenigth;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Bed extends Image {
    private Body body;
    public Bed(float x, float y, float width, float height) {
        super(new Texture("images/null.png"));
        createBody(x, y, width, height);
        body.setUserData(this);
    }

    public void createBody(float x, float y, float width, float height) {
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
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = groundBox;
        fixtureDef.density = 0.0f;

        // Create a fixture from our polygon shape and add it to our ground body
        body.createFixture(fixtureDef);

        // Clean up after ourselves
        groundBox.dispose();

        // on hit with Shadow remove shadow
    }
}

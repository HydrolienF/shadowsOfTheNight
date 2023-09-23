package fr.formiko.shadowsofthenigth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.esotericsoftware.spine.Slot;

public class Shadow extends SActor {
    private Color bodyColor;
    public boolean isPlayer;
    public static final float MAX_VELOCITY = 20000f;
    public static final float MAX_INPULSE = 5000f;
    Vector2 targetedPos;
    private int visionRadius;

    public Shadow(int visionRadius) {
        super("shadow");
        this.visionRadius = visionRadius;
        bodyColor = new Color(0, 0, 0, 0.7f);
        Slot colorSlot = getSkeleton().findSlot("shadow");
        colorSlot.getColor().set(bodyColor);
        getSkeleton().setSkin("bad");
        createBody();
        body.setUserData(this);
    }

    public void createBody() {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        //
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyType.DynamicBody;
        // Set our body's starting position in the world
        bodyDef.position.set(getX(), getY());

        // Create our body in the world using our body definition
        body = ShadowsOfTheNight.world.createBody(bodyDef);

        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(40f * ShadowsOfTheNight.getWidthRacio());

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isPlayer) {
            applyPlayerMove(delta);
        } else {
            tryToReachBed(delta);
        }
        // if(delete){
        // world.destroyBody(body);
        // this.remove();
        // }
        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x - this.getWidth() / 2, body.getPosition().y - this.getHeight() / 2);
    }

    public void applyPlayerMove(float delta) {
        Vector2 vel = body.getLinearVelocity();
        Vector2 pos = body.getPosition();
        float racio = delta * 60f;
        // TODO TOFIX shadow should move with same speed whatever the framerate is
        // body.applyForceToCenter(1.0f, 0.0f, true);

        // apply left impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Keys.LEFT) && vel.x > -MAX_VELOCITY) {
            body.applyLinearImpulse(-MAX_INPULSE * racio, 0, pos.x, pos.y, true);
        }

        // apply right impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Keys.RIGHT) && vel.x < MAX_VELOCITY) {
            body.applyLinearImpulse(MAX_INPULSE * racio, 0, pos.x, pos.y, true);
        }

        if (Gdx.input.isKeyPressed(Keys.UP) && vel.y < MAX_VELOCITY) {
            body.applyLinearImpulse(0, MAX_INPULSE * racio, pos.x, pos.y, true);
        }

        if (Gdx.input.isKeyPressed(Keys.DOWN) && vel.y > -MAX_VELOCITY) {
            body.applyLinearImpulse(0, -MAX_INPULSE * racio, pos.x, pos.y, true);
        }

        // set rotation of the body from the direction of the velocity
        body.setTransform(body.getPosition(), vel.angleRad());
    }

    private void tryToReachBed(float delta) {
        Bed bed = ShadowsOfTheNight.game.bed;
        Vector2 vel = body.getLinearVelocity();
        Vector2 pos = body.getPosition();
        if (pos.dst(bed.body.getPosition()) < visionRadius) {
            targetedPos = bed.body.getPosition();
        } else if (targetedPos == null) { // when it hit an obstacle targetedPos is set to null
            targetedPos = getRandomPosOutsideOfObstacle();
        } else if (pos.dst(targetedPos) < 100) { // when it reach the targetedPos, get a new one
            targetedPos = getRandomPosOutsideOfObstacle();
        }
        // TODO avoid light

        body.applyLinearImpulse((targetedPos.x - pos.x) * 1000f * delta, (targetedPos.y - pos.y) * 1000f * delta, pos.x, pos.y, true);


        // float racio = delta * 60f;

        // set rotation of the body from the direction of the velocity
        body.setTransform(body.getPosition(), vel.angleRad());
    }

    private Vector2 getRandomPosOutsideOfObstacle() {
        // while (true) {
        // Vector2 randomPos = new Vector2(MathUtils.random(0, Gdx.graphics.getWidth()), MathUtils.random(0, Gdx.graphics.getHeight()));
        // // TODO if is outside of an obstacle
        // // for ()
        // return randomPos;
        // }
        return new Vector2(MathUtils.random(120, Gdx.graphics.getWidth() - 120), MathUtils.random(120, Gdx.graphics.getHeight() - 120));
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        body.setTransform(x, y, 0);
    }

    @Override
    public boolean remove() {
        super.remove();
        ShadowsOfTheNight.world.destroyBody(body);
        return true;
    }
}

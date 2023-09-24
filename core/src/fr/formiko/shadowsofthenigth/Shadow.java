package fr.formiko.shadowsofthenigth;

import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.esotericsoftware.spine.Slot;

public class Shadow extends SActor {
    private Color bodyColor;
    public boolean isPlayer;
    public static final float MAX_VELOCITY = 3f;
    public static final float MAX_INPULSE = 5f;
    Vector2 targetedPos;
    private int visionRadius;
    private static final float SIZE = 30f;

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

    public float getRadius() { return SIZE * ShadowsOfTheNight.getWidthRacio() / ShadowsOfTheNight.PIXEL_PER_METER; }

    public List<Vector2> getHitPoints() {
        List<Vector2> hitPoints = new LinkedList<>();
        hitPoints.add(new Vector2(body.getPosition().x + getRadius(), body.getPosition().y));
        hitPoints.add(new Vector2(body.getPosition().x - getRadius(), body.getPosition().y));
        hitPoints.add(new Vector2(body.getPosition().x, body.getPosition().y + getRadius()));
        hitPoints.add(new Vector2(body.getPosition().x, body.getPosition().y - getRadius()));
        hitPoints.add(new Vector2(body.getPosition().x + getRadius(), body.getPosition().y + getRadius()));
        hitPoints.add(new Vector2(body.getPosition().x - getRadius(), body.getPosition().y - getRadius()));
        hitPoints.add(new Vector2(body.getPosition().x + getRadius(), body.getPosition().y - getRadius()));
        hitPoints.add(new Vector2(body.getPosition().x - getRadius(), body.getPosition().y + getRadius()));
        return hitPoints;
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
        circle.setRadius(getRadius());

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 5f;
        // fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our fixture and attach it to the body
        body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
    }
    float timeForNextMove = 0;
    @Override
    public void act(float delta) {
        super.act(delta);
        timeForNextMove += delta;
        while (timeForNextMove > ShadowsOfTheNight.TIME_STEP) {
            timeForNextMove -= ShadowsOfTheNight.TIME_STEP;
            if (isPlayer) {
                applyPlayerMove(delta);
            } else {
                tryToReachBed(delta);
            }
            this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        }
        this.setPosition(body.getPosition().x * ShadowsOfTheNight.PIXEL_PER_METER - this.getWidth() / 2,
                body.getPosition().y * ShadowsOfTheNight.PIXEL_PER_METER - this.getHeight() / 2);
    }

    public void applyPlayerMove(float delta) {
        Vector2 vel = body.getLinearVelocity();

        // apply left impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Keys.LEFT) && vel.x > -MAX_VELOCITY) {
            body.applyForceToCenter(-MAX_INPULSE, 0, true);
        }

        // apply right impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Keys.RIGHT) && vel.x < MAX_VELOCITY) {
            body.applyForceToCenter(MAX_INPULSE, 0, true);
        }

        if (Gdx.input.isKeyPressed(Keys.UP) && vel.y < MAX_VELOCITY) {
            body.applyForceToCenter(0, MAX_INPULSE, true);
        }

        if (Gdx.input.isKeyPressed(Keys.DOWN) && vel.y > -MAX_VELOCITY) {
            body.applyForceToCenter(0, -MAX_INPULSE, true);
        }

        // set rotation of the body from the direction of the velocity
        body.setTransform(body.getPosition(), vel.angleRad());
    }

    private void tryToReachBed(float delta) {
        float multiplier = 0.2f + 3 * ShadowsOfTheNight.chrono.getPercentElapsedTime();
        float aiMaxVelocity = MAX_VELOCITY * multiplier;
        float aiMaxInpulse = MAX_INPULSE * multiplier;

        Bed bed = ShadowsOfTheNight.game.bed;
        Vector2 vel = body.getLinearVelocity();
        Vector2 pos = body.getPosition();
        if (pos.dst(bed.body.getPosition()) < visionRadius / ShadowsOfTheNight.PIXEL_PER_METER) {
            System.out.println("Target bed");
            targetedPos = bed.body.getPosition();
        } else if (targetedPos == null) { // when it hit an obstacle targetedPos is set to null
            targetedPos = getRandomPosOutsideOfObstacle();
        } else if (pos.dst(targetedPos) < 50 / ShadowsOfTheNight.PIXEL_PER_METER) { // when it reach the targetedPos, get a new one
            System.out.println("reach target");
            targetedPos = getRandomPosOutsideOfObstacle();
            // if it will reach light in 100 pixels, get a new target position
        }
        // TODO avoid light
        // else if (ShadowsOfTheNight.game.cl.contains(body.getPosition().x + vel.x * 300 / ShadowsOfTheNight.PIXEL_PER_METER,
        // body.getPosition().y + vel.y * 100 / ShadowsOfTheNight.PIXEL_PER_METER)) {
        // System.out.println("will hit light");
        // targetedPos = getRandomPosOutsideOfObstacle();
        // }

        // body.applyLinearImpulse((targetedPos.x - pos.x) * 1000f * delta, (targetedPos.y - pos.y) * 1000f * delta, pos.x, pos.y, true);
        // if (vel.len() < MAX_VELOCITY) {
        // body.applyForceToCenter(MAX_INPULSE * (targetedPos.x - pos.x), MAX_INPULSE * (targetedPos.y - pos.y), true);
        // Apply force to reach targetedPos
        // calculate distance between body and targetedPos
        float distance = pos.dst(targetedPos);
        float vx = (targetedPos.x - pos.x) / distance;
        float vy = (targetedPos.y - pos.y) / distance;
        body.applyForceToCenter(aiMaxInpulse * vx, aiMaxInpulse * vy, true);

        if (vel.len() > aiMaxVelocity) {
            body.applyForceToCenter(vel.x * -1, vel.y * -1, true);
        }


        // float racio = delta * 60f;

        // set rotation of the body from the direction of the velocity
        body.setTransform(body.getPosition(), vel.angleRad());
    }

    private Vector2 getRandomPosOutsideOfObstacle() {
        // will work only for walls
        return new Vector2(MathUtils.random(120, Gdx.graphics.getWidth() - 120) / ShadowsOfTheNight.PIXEL_PER_METER,
                MathUtils.random(120, Gdx.graphics.getHeight() - 120) / ShadowsOfTheNight.PIXEL_PER_METER);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        body.setTransform(x / ShadowsOfTheNight.PIXEL_PER_METER, y / ShadowsOfTheNight.PIXEL_PER_METER, 0);
    }

    @Override
    public boolean remove() {
        super.remove();
        ShadowsOfTheNight.world.destroyBody(body);
        return true;
    }
}

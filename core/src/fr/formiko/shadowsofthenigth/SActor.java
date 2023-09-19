package fr.formiko.shadowsofthenigth;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.utils.SkeletonActor;

public class SActor extends SkeletonActor {

    public SActor(String textureName) {
        super();
        Skeleton skeleton = new Skeleton(ShadowsOfTheNight.getAssets().getSkeletonData(textureName));

        skeleton.setPosition(getWidth() / 2, getHeight() / 2);
        SkeletonRenderer skeletonRenderer = new SkeletonRenderer(); // TODO maybe we can use a static one
        skeletonRenderer.setPremultipliedAlpha(true);

        AnimationStateData stateData = new AnimationStateData(ShadowsOfTheNight.getAssets().getSkeletonData(textureName));

        AnimationState animationState = new AnimationState(stateData);

        setRenderer(skeletonRenderer);
        setSkeleton(skeleton);
        setAnimationState(animationState);
    }
    /**
     * {@summary Move in x &#38; y}
     * 
     * @param x x
     * @param y y
     */
    public void translate(float x, float y) {
        setX(getX() + x);
        setY(getY() + y);
    }
    /**
     * {@summary Move in the facing direction.}
     * 
     * @param distance distance to move
     */
    public void moveFront(float distance, Vector2 direction) {
        float facingAngle = direction.angleDeg();
        translate((float) (distance * Math.cos(Math.toRadians(facingAngle))), (float) (distance * Math.sin(Math.toRadians(facingAngle))));
    }


    /**
     * {@summary Update this actor.}
     * It update skeleton to the same rotation and scale than the actor.
     * 
     * @param delta time since last update
     */
    @Override
    public void act(float delta) {
        if (getSkeleton() != null) {
            getSkeleton().findBone("root").setRotation(getRotation());
            getSkeleton().setScale(getScaleX(), getScaleY());
            // if (mapItem instanceof Creature c && getAnimationState() != null && getAnimationState().getCurrent(0) != null) {
            // getAnimationState().getCurrent(0).setTimeScale((c).getCurrentSpeed() * 0.3f * c.getAnimationSpeedMultiplier());
            // }
            super.act(delta);
        }
    }
}

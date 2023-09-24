package fr.formiko.shadowsofthenigth;

import com.badlogic.gdx.graphics.Color;
import box2dLight.ConeLight;
import box2dLight.RayHandler;

public class BoyLight extends ConeLight {
    private boolean moveUp;
    public BoyLight(RayHandler rayHandler) {
        super(rayHandler, 128, new Color(1, 250f / 255f, 204f / 255f, 0.8f), 5000 * ShadowsOfTheNight.getWidthRacio(), 0, 0, 0, 2.5f);
        setActive(true);
        setSoft(true);
        setSoftnessLength(2f);
    }

    public void act() {
        float maxMoveSpeed = 0.1f;
        if (ShadowsOfTheNight.game.isPlayerBoy()) {
            // TODO move ligth from arrows keys
        } else {
            maxMoveSpeed = 0.05f * (1 + 2 * (ShadowsOfTheNight.game.toysAtStart - ShadowsOfTheNight.game.toysLeft));
            if (getDirection() > 90) {
                moveUp = false;
            } else if (getDirection() < -90) {
                moveUp = true;
            }
            // TODO move as AI
            if (moveUp) {
                setDirection(getDirection() + maxMoveSpeed);
            } else {
                setDirection(getDirection() - maxMoveSpeed);
            }
        }
    }
}

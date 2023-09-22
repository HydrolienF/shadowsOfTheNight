package fr.formiko.shadowsofthenigth;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class BedShadowContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Shadow shadow = null;
        if (contact.getFixtureA().getBody().getUserData() instanceof Bed
                && contact.getFixtureB().getBody().getUserData() instanceof Shadow) {
            shadow = (Shadow) contact.getFixtureB().getBody().getUserData();
        } else if (contact.getFixtureB().getBody().getUserData() instanceof Shadow
                && contact.getFixtureA().getBody().getUserData() instanceof Bed) {
            shadow = (Shadow) contact.getFixtureA().getBody().getUserData();
        }

        if (shadow != null) {
            ShadowsOfTheNight.game.addShadowToRemove(shadow, false);
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

}

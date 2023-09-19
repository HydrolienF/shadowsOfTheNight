package fr.formiko.shadowsofthenigth;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.spine.Slot;

public class Shadow extends SActor {
    private Color bodyColor;
    public Shadow() {
        super("shadow");
        bodyColor = new Color(0, 0, 0, 0.7f);
        Slot colorSlot = getSkeleton().findSlot("shadow");
        colorSlot.getColor().set(bodyColor);
        getSkeleton().setSkin("bad");
    }
}

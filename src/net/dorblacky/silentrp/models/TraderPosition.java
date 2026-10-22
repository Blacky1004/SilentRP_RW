package net.dorblacky.silentrp.models;

import net.risingworld.api.utils.Quaternion;
import net.risingworld.api.utils.Vector3f;

public class TraderPosition {
    private Vector3f position;
    private Quaternion rotation;

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }
}

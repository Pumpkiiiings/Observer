package com.observer.api.sound;

public class SoundDefinition {
    private final String soundId;
    private final String namespace;
    private final float volume;
    private final float pitch;
    private final boolean loop;
    private final String category;
    private final double x;
    private final double y;
    private final double z;
    private final double maxDistance;
    private final float attenuation;

    private SoundDefinition(Builder builder) {
        this.soundId = builder.soundId;
        this.namespace = builder.namespace;
        this.volume = builder.volume;
        this.pitch = builder.pitch;
        this.loop = builder.loop;
        this.category = builder.category;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.maxDistance = builder.maxDistance;
        this.attenuation = builder.attenuation;
    }

    public String getSoundId() { return soundId; }
    public String getNamespace() { return namespace; }
    public float getVolume() { return volume; }
    public float getPitch() { return pitch; }
    public boolean isLoop() { return loop; }
    public String getCategory() { return category; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getMaxDistance() { return maxDistance; }
    public float getAttenuation() { return attenuation; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String soundId;
        private String namespace = "minecraft";
        private float volume = 1.0f;
        private float pitch = 1.0f;
        private boolean loop = false;
        private String category = "master";
        private double x = 0;
        private double y = 0;
        private double z = 0;
        private double maxDistance = 16.0;
        private float attenuation = 1.0f;

        public Builder sound(String sound) {
            if (sound != null && sound.contains(":")) {
                String[] parts = sound.split(":", 2);
                this.namespace = parts[0];
                this.soundId = parts[1];
            } else {
                this.soundId = sound;
            }
            return this;
        }

        public Builder soundId(String soundId) {
            this.soundId = soundId;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder volume(float volume) {
            this.volume = volume;
            return this;
        }

        public Builder pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Builder maxDistance(double maxDistance) {
            this.maxDistance = maxDistance;
            return this;
        }

        public Builder attenuation(float attenuation) {
            this.attenuation = attenuation;
            return this;
        }

        public SoundDefinition build() {
            if (soundId == null || soundId.isEmpty()) {
                throw new IllegalStateException("Sound ID must be set");
            }
            return new SoundDefinition(this);
        }
    }
}

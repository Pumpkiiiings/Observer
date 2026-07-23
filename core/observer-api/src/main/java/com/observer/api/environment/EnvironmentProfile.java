package com.observer.api.environment;

public class EnvironmentProfile {

    public static final EnvironmentProfile DEFAULT = builder().id("DEFAULT").build();
    public static final EnvironmentProfile NIGHTMARE = builder().id("NIGHTMARE")
            .skyColor(0x330000)
            .fogColor(0x110000)
            .fogDensity(0.1f)
            .trueDarkness(true)
            .build();
    public static final EnvironmentProfile FOGGY = builder().id("FOGGY")
            .fogDensity(0.05f)
            .build();
    public static final EnvironmentProfile BLOOD_MOON = builder().id("BLOOD_MOON")
            .skyColor(0x550000)
            .fogColor(0x330000)
            .build();

    private final String id;
    private final Integer skyColor;
    private final Integer fogColor;
    private final Float fogDensity;
    private final Boolean trueDarkness;

    private EnvironmentProfile(Builder builder) {
        this.id = builder.id;
        this.skyColor = builder.skyColor;
        this.fogColor = builder.fogColor;
        this.fogDensity = builder.fogDensity;
        this.trueDarkness = builder.trueDarkness;
    }

    public String getId() { return id; }
    public Integer getSkyColor() { return skyColor; }
    public Integer getFogColor() { return fogColor; }
    public Float getFogDensity() { return fogDensity; }
    public Boolean getTrueDarkness() { return trueDarkness; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Integer skyColor;
        private Integer fogColor;
        private Float fogDensity;
        private Boolean trueDarkness;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder skyColor(Integer skyColor) {
            this.skyColor = skyColor;
            return this;
        }

        public Builder fogColor(Integer fogColor) {
            this.fogColor = fogColor;
            return this;
        }

        public Builder fogDensity(Float fogDensity) {
            this.fogDensity = fogDensity;
            return this;
        }

        public Builder trueDarkness(Boolean trueDarkness) {
            this.trueDarkness = trueDarkness;
            return this;
        }

        public EnvironmentProfile build() {
            if (id == null) {
                id = "CUSTOM_" + System.currentTimeMillis();
            }
            return new EnvironmentProfile(this);
        }
    }
}

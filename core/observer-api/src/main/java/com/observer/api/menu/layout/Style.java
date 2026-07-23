package com.observer.api.menu.layout;

/**
 * Defines the visual styling for a MenuElement.
 */
public record Style(
        Integer color,
        Integer backgroundColor,
        Integer borderColor,
        Integer borderWidth,
        Float fontScale,
        Float opacity,
        Integer padding,
        Integer margin,
        String alignment // "left", "center", "right"
) {
    public static final Style DEFAULT = new Style(null, null, null, null, null, null, null, null, null);
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Integer color;
        private Integer backgroundColor;
        private Integer borderColor;
        private Integer borderWidth;
        private Float fontScale;
        private Float opacity;
        private Integer padding;
        private Integer margin;
        private String alignment;
        
        public Builder color(int color) { this.color = color; return this; }
        public Builder backgroundColor(int bg) { this.backgroundColor = bg; return this; }
        public Builder border(int color, int width) { this.borderColor = color; this.borderWidth = width; return this; }
        public Builder fontScale(float scale) { this.fontScale = scale; return this; }
        public Builder opacity(float op) { this.opacity = op; return this; }
        public Builder padding(int p) { this.padding = p; return this; }
        public Builder margin(int m) { this.margin = m; return this; }
        public Builder alignment(String align) { this.alignment = align; return this; }
        
        public Style build() {
            return new Style(color, backgroundColor, borderColor, borderWidth, fontScale, opacity, padding, margin, alignment);
        }
    }
}

package com.observer.api.menu.state;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores temporary session data, key-value variables, and player-specific values.
 */
public class MenuState {
    
    private final Map<String, Object> variables = new HashMap<>();
    
    public void set(String key, Object value) {
        variables.put(key, value);
    }
    
    public Object get(String key) {
        return variables.get(key);
    }
    
    public String getString(String key, String def) {
        Object val = get(key);
        return val != null ? val.toString() : def;
    }
    
    public int getInt(String key, int def) {
        Object val = get(key);
        if (val instanceof Number n) return n.intValue();
        if (val instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        }
        return def;
    }
    
    public void remove(String key) {
        variables.remove(key);
    }
    
    public boolean has(String key) {
        return variables.containsKey(key);
    }
    
    public void clear() {
        variables.clear();
    }
}

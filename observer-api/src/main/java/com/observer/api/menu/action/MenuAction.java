package com.observer.api.menu.action;

public record MenuAction(String actionId, MenuActionType actionType, String actionData) {
    public static MenuAction custom(String id) {
        return new MenuAction(id, MenuActionType.CUSTOM, null);
    }
    public static MenuAction close() {
        return new MenuAction("close", MenuActionType.CLOSE, null);
    }
}

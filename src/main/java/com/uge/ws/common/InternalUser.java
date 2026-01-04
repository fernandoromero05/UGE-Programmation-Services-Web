// src/main/java/com/uge/ws/common/InternalUser.java
package com.uge.ws.common;

public class InternalUser {
    private long id;
    private String name;
    private String email;
    private UserType type;

    public InternalUser() {}

    public InternalUser(long id, String name, String email, UserType type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
    }
    // getters & setters …
}

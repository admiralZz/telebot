package com.admiral.telebot.gpt;

public class GPTMessage {
    private Role role;
    private String message;

    public GPTMessage(Role role, String message) {
        this.role = role;
        this.message = message;
    }

    public Role getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }

    public enum Role {
        USER("user"),
        ASSISTANT("assistant"),
        SYSTEM("system")
        ;

        private String role;
        Role(String role) {
            this.role = role;
        }

        public String getValue() {
            return role;
        }
    }

    @Override
    public String toString() {
        return "GPTMessage{" +
                "role=" + role +
                ", message='" + message + '\'' +
                '}';
    }
}

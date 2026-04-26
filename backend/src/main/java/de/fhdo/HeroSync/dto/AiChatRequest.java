package de.fhdo.HeroSync.dto;

public class AiChatRequest {
    private String message;
    private String mode; // chat, onboarding, coaching, diagnostics, greeting

    public AiChatRequest() {}

    public AiChatRequest(String message, String mode) {
        this.message = message;
        this.mode = mode;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
}

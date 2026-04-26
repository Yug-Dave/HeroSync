package de.fhdo.HeroSync.dto;

public class AiChatResponse {
    private String reply;
    private String companion;
    private String provider;

    public AiChatResponse() {}

    public AiChatResponse(String reply, String companion, String provider) {
        this.reply = reply;
        this.companion = companion;
        this.provider = provider;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getCompanion() { return companion; }
    public void setCompanion(String companion) { this.companion = companion; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}

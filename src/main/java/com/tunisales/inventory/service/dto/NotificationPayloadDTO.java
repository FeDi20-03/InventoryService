package com.tunisales.inventory.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Body sent to the PlatformService notification endpoint.
 *
 * <p>Note that {@code recipientLogin} may be a special wildcard such as
 * {@code "*ADMIN_COMMERCIAL*"} which the PlatformService expands to all users
 * holding the matching role.</p>
 */
public class NotificationPayloadDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 255)
    private String recipientLogin;

    @NotNull
    @Size(max = 100)
    private String type;

    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String body;

    private String payloadJson;

    public NotificationPayloadDTO() {}

    public NotificationPayloadDTO(String recipientLogin, String type, String title, String body, String payloadJson) {
        this.recipientLogin = recipientLogin;
        this.type = type;
        this.title = title;
        this.body = body;
        this.payloadJson = payloadJson;
    }

    public String getRecipientLogin() {
        return recipientLogin;
    }

    public void setRecipientLogin(String recipientLogin) {
        this.recipientLogin = recipientLogin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationPayloadDTO)) return false;
        NotificationPayloadDTO that = (NotificationPayloadDTO) o;
        return (
            Objects.equals(recipientLogin, that.recipientLogin) &&
            Objects.equals(type, that.type) &&
            Objects.equals(title, that.title) &&
            Objects.equals(body, that.body) &&
            Objects.equals(payloadJson, that.payloadJson)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipientLogin, type, title, body, payloadJson);
    }

    @Override
    public String toString() {
        return (
            "NotificationPayloadDTO{" +
            "recipientLogin='" +
            recipientLogin +
            '\'' +
            ", type='" +
            type +
            '\'' +
            ", title='" +
            title +
            '\'' +
            "}"
        );
    }
}

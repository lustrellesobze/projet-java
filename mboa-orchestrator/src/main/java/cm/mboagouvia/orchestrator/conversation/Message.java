package cm.mboagouvia.orchestrator.conversation;

import java.time.LocalDateTime;

import cm.mboagouvia.orchestrator.compte.Langue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Une bulle de la discussion (composition : Conversation 1 contient 1..* Message). */
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleMessage role;

    @Column(columnDefinition = "text")
    private String contenu;

    @Enumerated(EnumType.STRING)
    private Langue langue;

    private LocalDateTime horodatage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    protected Message() {
        // requis par JPA
    }

    public Message(Conversation conversation, RoleMessage role, String contenu, Langue langue,
            LocalDateTime horodatage) {
        this.conversation = conversation;
        this.role = role;
        this.contenu = contenu;
        this.langue = langue;
        this.horodatage = horodatage;
    }

    public Long getId() {
        return id;
    }

    public RoleMessage getRole() {
        return role;
    }

    public String getContenu() {
        return contenu;
    }

    public Langue getLangue() {
        return langue;
    }

    public LocalDateTime getHorodatage() {
        return horodatage;
    }

    public Conversation getConversation() {
        return conversation;
    }
}

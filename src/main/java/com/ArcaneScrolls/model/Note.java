package com.ArcaneScrolls.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "note", nullable = false)
    private String note;

    @Column(name = "headline", nullable = false)
    private String headline;

    @CreationTimestamp
    @Column(name = "created_at")
    private long createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private long updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Note() {

    }

    public Note(String note, String headline) {
        this.note = note;
        this.headline = headline;
    }

    public Note(String note) {
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getCreatedAtFormatted() {
        return formatTimestamp(String.valueOf(createdAt));
    }

    public String getUpdatedAtFormatted() {
        return formatTimestamp(String.valueOf(updatedAt));
    }

    private String formatTimestamp(String timestamp) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormatter);
        return dateTime.format(outputFormatter);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", note='" + note + '\'' +
                ", headline='" + headline + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", user=" + user +
                '}';
    }
}

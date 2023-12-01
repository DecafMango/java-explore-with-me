package ru.practicum.ewm.comment.model;

import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text")
    private String text;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @ManyToOne
    @JoinColumn(name = "commentator_id")
    private User commentator;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}

package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    private String name;
    @Column(length = 512)
    private String description;
    private Boolean available;
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User owner;
    @CollectionTable(name = "comments", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "text")
    @ElementCollection(fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<String> comments;
}

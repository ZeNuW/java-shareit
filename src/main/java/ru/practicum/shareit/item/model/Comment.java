package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    /*
    Builder не хочет работать без @AllArgsConstructor, а без @NoArgsConstructor ошибка
    "Class 'Comment' should have [public, protected] no-arg constructor"
    Поэтому решил оставить так, если это не критично и так можно оставить, если что переделаю

    И у меня есть вопрос вообще не по теме спринта и учёбы. Если так можно. :)
    Я тут игрался с JavaFX вот и возник вопрос, есть ли в ней вообще востребованность,
    т.е имеет ли смысл более углубленно изучать и т.д? Или как инструмент довольно редко применяется?
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "commentator_id")
    private User commentator;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    @Column(name = "create_time")
    private LocalDateTime created;
}
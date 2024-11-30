package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private final Set<Long> likes = new HashSet<>();
    private final Set<Genre> genres = new HashSet<>();
    private Mpa mpa;

    public void like(long id) {
        likes.add(id);
    }

    public void dislike(long id) {
        likes.remove(id);
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }

    public void deleteGener(Genre genre) {
        this.genres.remove(genre);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Mpa {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Genre {
        private Integer id;
        private String name;
    }
}

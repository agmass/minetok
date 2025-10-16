package org.agmas.minetok.client.render.sprites.minetok;

import java.util.List;

public record ParsedMetadata(String title, String author, int likes, int followers, List<Comment> comments) {
    public record Comment(String body, String author, String thumbnail, int likes) {}
}

package com.ataulm.wutson.seasons;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

class Season implements Iterable<Season.Episode>, Comparable<Season> {

    private final String airDate; // TODO: this should be typed
    private final int seasonNumber;
    private final String overview;
    private final URI posterPath;
    private final List<Episode> episodes;

    Season(String airDate, int seasonNumber, String overview, URI posterPath, List<Episode> episodes) {
        this.airDate = airDate;
        this.seasonNumber = seasonNumber;
        this.overview = overview;
        this.posterPath = posterPath;
        this.episodes = episodes;
    }

    @Override
    public Iterator<Episode> iterator() {
        return episodes.iterator();
    }

    public int size() {
        return episodes.size();
    }

    public Episode get(int position) {
        return episodes.get(position);
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    @Override
    public int compareTo(Season o) {
        return getSeasonNumber() - o.getSeasonNumber();
    }

    static class Episode {

        private final String airDate; // TODO: should be typed
        private final int episodeNumber;
        private final String name;
        private final String overview;
        private final URI stillPath;

        Episode(String airDate, int episodeNumber, String name, String overview, URI stillPath) {
            this.airDate = airDate;
            this.episodeNumber = episodeNumber;
            this.name = name;
            this.overview = overview;
            this.stillPath = stillPath;
        }

        public int getEpisodeNumber() {
            return episodeNumber;
        }

        public URI getStillPath() {
            return stillPath;
        }

        public String getName() {
            return name;
        }

        public String getAirDate() {
            return airDate;
        }
    }

}
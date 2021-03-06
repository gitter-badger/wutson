package com.ataulm.wutson.seasons;

import java.util.Iterator;
import java.util.List;

public class Seasons implements Iterable<Season> {

    private final com.ataulm.wutson.shows.ShowId showId;
    private final String showName;
    private final List<Season> seasons;

    public Seasons(com.ataulm.wutson.shows.ShowId showId, String showName, List<Season> seasons) {
        this.showId = showId;
        this.showName = showName;
        this.seasons = seasons;
    }

    public com.ataulm.wutson.shows.ShowId getShowId() {
        return showId;
    }

    public String getShowName() {
        return showName;
    }

    @Override
    public Iterator<Season> iterator() {
        return seasons.iterator();
    }

    public Season get(int position) {
        return seasons.get(position);
    }

    public int size() {
        return seasons.size();
    }
}

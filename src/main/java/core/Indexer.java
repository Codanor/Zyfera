package core;

import java.util.ArrayDeque;
import java.util.HashSet;

public class Indexer {

    public Indexer() {
        _nextId = 0;

        _IDS = new HashSet<>();
        _FREE_IDS = new ArrayDeque<>();
    }

    private int _nextId;

    private final HashSet<Integer> _IDS;
    private final ArrayDeque<Integer> _FREE_IDS;

    public int get() {
        Integer id;

        id = _FREE_IDS.poll();
        if (id == null) id = _nextId++;

        _IDS.add(id);

        return id;
    }
    public boolean free(int id) {
        if (!_IDS.remove(id)) return false;

        _FREE_IDS.add(id);

        return true;
    }

}
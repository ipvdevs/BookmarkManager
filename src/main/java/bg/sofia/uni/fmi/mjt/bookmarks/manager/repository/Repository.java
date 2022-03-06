package bg.sofia.uni.fmi.mjt.bookmarks.manager.repository;

import java.util.Collection;
import java.util.Optional;

public interface Repository<K, V> {

    void add(K key, V value);

    void remove(K key);

    Optional<V> get(K key);

    boolean contains(K key);

    Collection<V> getAll();

}

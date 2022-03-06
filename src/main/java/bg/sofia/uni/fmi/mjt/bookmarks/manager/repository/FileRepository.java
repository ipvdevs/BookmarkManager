package bg.sofia.uni.fmi.mjt.bookmarks.manager.repository;

import java.io.Reader;
import java.io.Writer;

interface FileRepository<K, V> extends Repository<K, V> {
    void store(String file);

    void store(Writer writer);

    void load(String file);

    void load(Reader writer);
}

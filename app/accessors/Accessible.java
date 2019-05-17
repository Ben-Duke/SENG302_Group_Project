package accessors;

import io.ebean.Model;

public interface Accessible {
    void insert(Class<? extends Model> object);

    void update(Class<? extends Model> object);

    void delete(Class<? extends Model> object);

    void getById(Integer id);
}

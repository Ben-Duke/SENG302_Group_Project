package models;

import io.ebean.Model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseModel extends Model {
  @Id
  private Long id;

  private Long getId() {
    return id;
  }

  private void setId(Long id) {
    this.id = id;
  }
}

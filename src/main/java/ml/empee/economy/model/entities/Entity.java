package ml.empee.economy.model.entities;

/**
 * Entity
 */

public interface Entity {
  Long getId();

  /**
   * @return a copy of this entity with the given id
   */
  Entity withId(Long id);
}

package model.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Abstract base class for all domain entities.
 * Provides common id and basic contract.
 */
public abstract class Entity implements Serializable {

    protected String id;

    protected Entity() {
        this.id = UUID.randomUUID().toString();
    }

    protected Entity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /** Each entity must be able to describe itself. */
    public abstract String printInfo();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return printInfo();
    }
}

package de.offene_pflege.entity;

/**
 * Created by tloehr on 22.06.15.
 */
public interface  NotRemovableEvaluation<T> {

    boolean isRemovable(T checkMe);

}

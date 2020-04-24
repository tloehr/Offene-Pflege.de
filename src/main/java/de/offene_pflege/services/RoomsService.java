package de.offene_pflege.services;

import de.offene_pflege.entity.building.Floors;
import de.offene_pflege.entity.building.Rooms;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 03.11.12 Time: 14:05 To change this template use File | Settings | File
 * Templates.
 */
public class RoomsService {

    public static Rooms create(String text, Boolean single, Boolean bath, Floors floor) {
        Rooms rooms = new Rooms();
        rooms.setText(text);
        rooms.setSingle(single);
        rooms.setBath(bath);
        rooms.setActive(true);
        rooms.setFloor(floor);
        return rooms;

    }
}

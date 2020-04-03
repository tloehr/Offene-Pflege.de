package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.Floors;
import de.offene_pflege.backend.entity.done.Homes;
import de.offene_pflege.op.tools.SYSTools;

import java.util.ArrayList;

public class FloorService {

    public static Floors create(Homes home, String name) {
        Floors floors = new Floors();

        floors.setHome(home);
        floors.setName(name);
        floors.setLevel(2); // EG
        floors.setLift(0);
        floors.setRooms(new ArrayList<>());
        floors.getRooms().add(RoomsService.create(SYSTools.xx("opde.settings.home.btnAddRoom"), true, true, floors));
        return floors;
    }

}

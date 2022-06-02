package de.offene_pflege.services.qdvs;

import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.gui.events.AddTextListener;
import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface QdvsService {

    String get_DESCRIPTION();

    String get_DAS_SPEZIFIKATION();

    String get_DAS_REGELN_CSV();

    void setParameters(LocalDate stichtag, LocalDate beginn_erfassungszeitraum, Homes home, List<Resident> listeAlleBewohnerAmStichtag, File target);

    Map<Resident, QdvsResidentInfoObject> getResidentInfoObjectMap();

    boolean ergebniserfassung() throws JAXBException, IOException;

    void kommentierung(String kommentar);
}

package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.HInsurance;

public class HInsuranceService {
    public HInsurance create(String name, String strasse, String plz, String ort, String tel, String fax, String kNr) {
        HInsurance hInsurance = new HInsurance();
        hInsurance.setName(name);
        hInsurance.setStrasse(strasse);
        hInsurance.setPlz(plz);
        hInsurance.setOrt(ort);
        hInsurance.setTel(tel);
        hInsurance.setFax(fax);
        hInsurance.setkNr(kNr);
        return hInsurance;
    }

}

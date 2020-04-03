package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.ICD;

public class ICDService {

    public ICD create(String icd10, String text){
        ICD icd = new ICD();
        icd.setIcd10(icd10);
        icd.setText(text);
        return icd;
    }
}

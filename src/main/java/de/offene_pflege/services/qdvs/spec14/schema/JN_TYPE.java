package de.offene_pflege.services.qdvs.spec14.schema;

public class JN_TYPE {
    boolean ja;

    public JN_TYPE() {
        this.ja = true;
    }

    public JN_TYPE(boolean ja) {
        this.ja = ja;
    }

    public boolean isJa() {
        return ja;
    }

    public void setJa(boolean ja) {
        this.ja = ja;
    }

    public int getJN(){
        return ja ? 1 : 0;
    }
}

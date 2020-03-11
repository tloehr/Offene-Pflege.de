package de.offene_pflege.services.qdvs;

public class DAS_REGELN {

    String dataset;
    int rule_id;
    String assert_test;
    String rule_text;
    String rule_type;

    public DAS_REGELN(String dataset, int rule_id, String assert_test, String rule_text, String rule_type) {
        this.dataset = dataset;
        this.rule_id = rule_id;
        this.assert_test = assert_test;
        this.rule_text = rule_text;
        this.rule_type = rule_type;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public int getRule_id() {
        return rule_id;
    }

    public void setRule_id(int rule_id) {
        this.rule_id = rule_id;
    }

    public String getAssert_test() {
        return assert_test;
    }

    public void setAssert_test(String assert_test) {
        this.assert_test = assert_test;
    }

    public String getRule_text() {
        return rule_text;
    }

    public void setRule_text(String rule_text) {
        this.rule_text = rule_text;
    }

    public String getRule_type() {
        return rule_type;
    }

    public void setRule_type(String rule_type) {
        this.rule_type = rule_type;
    }
}

package entity.roster;

import java.math.BigDecimal;

public class StatsPerDay {

    public static final int EXAM = 0;
    public static final int HELPER = 1;
    public static final int SOCIAL = 2;

    BigDecimal exam_early, exam_late, exam_night, social_early, social_late, social_night, helper_early, helper_late, helper_night;

    public StatsPerDay() {
        clear();
    }

    public void add(int shift, int type, BigDecimal val) {
        if (shift == Symbol.SHIFT_NONE) return;

        if (shift == Symbol.SHIFT_EARLY) {
            if (type == EXAM) {
                exam_early = exam_early.add(val);
            } else if (type == SOCIAL) {
                social_early = social_early.add(val);
            } else if (type == HELPER) {
                helper_early = helper_early.add(val);
            }
        } else if (shift == Symbol.SHIFT_LATE) {
            if (type == EXAM) {
                exam_late = exam_late.add(val);
            } else if (type == SOCIAL) {
                social_late = social_late.add(val);
            } else if (type == HELPER) {
                helper_late = helper_late.add(val);
            }
        } else {
            if (type == EXAM) {
                exam_night = exam_night.add(val);
            } else if (type == SOCIAL) {
                social_night = social_night.add(val);
            } else if (type == HELPER) {
                helper_night = helper_night.add(val);
            }
        }
    }

    public void clear() {
        this.exam_early = BigDecimal.ZERO;
        this.exam_late = BigDecimal.ZERO;
        this.exam_night = BigDecimal.ZERO;
        this.social_early = BigDecimal.ZERO;
        this.social_late = BigDecimal.ZERO;
        this.social_night = BigDecimal.ZERO;
        this.helper_early = BigDecimal.ZERO;
        this.helper_late = BigDecimal.ZERO;
        this.helper_night = BigDecimal.ZERO;
    }

    public void set(int shift, int type, BigDecimal val) {
        if (shift == Symbol.SHIFT_NONE) return;

        if (shift == Symbol.SHIFT_EARLY) {
            if (type == EXAM) {
                exam_early = val;
            } else if (type == SOCIAL) {
                social_early = val;
            } else if (type == HELPER) {
                helper_early = val;
            }
        } else if (shift == Symbol.SHIFT_LATE) {
            if (type == EXAM) {
                exam_late = val;
            } else if (type == SOCIAL) {
                social_late = val;
            } else if (type == HELPER) {
                helper_late = val;
            }
        } else {
            if (type == EXAM) {
                exam_night = val;
            } else if (type == SOCIAL) {
                social_night = val;
            } else if (type == HELPER) {
                helper_night = val;
            }
        }
    }

    public void add(int type, Symbol symbol) {
        add(symbol.getShift1(), type, symbol.getStatval1());
        add(symbol.getShift2(), type, symbol.getStatval2());
    }

    public void replace(int type, Symbol oldSymbol, Symbol newSymbol) {
        subtract(type, oldSymbol);
        add(type, newSymbol);
    }

    public void subtract(int type, Symbol symbol) {
        subtract(symbol.getShift1(), type, symbol.getStatval1());
        subtract(symbol.getShift2(), type, symbol.getStatval2());
    }

    public void subtract(int shift, int type, BigDecimal val) {
        if (shift == Symbol.SHIFT_NONE) return;

        if (shift == Symbol.SHIFT_EARLY) {
            if (type == EXAM) {
                exam_early = exam_early.subtract(val);
            } else if (type == SOCIAL) {
                social_early = social_early.subtract(val);
            } else if (type == HELPER) {
                helper_early = helper_early.subtract(val);
            }
        } else if (shift == Symbol.SHIFT_LATE) {
            if (type == EXAM) {
                exam_late = exam_late.subtract(val);
            } else if (type == SOCIAL) {
                social_late = social_late.subtract(val);
            } else if (type == HELPER) {
                helper_late = helper_late.subtract(val);
            }
        } else {
            if (type == EXAM) {
                exam_night = exam_night.subtract(val);
            } else if (type == SOCIAL) {
                social_night = social_night.subtract(val);
            } else if (type == HELPER) {
                helper_night = helper_night.subtract(val);
            }
        }
    }

}
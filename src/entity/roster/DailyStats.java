package entity.roster;

import java.math.BigDecimal;

public class DailyStats {

    BigDecimal exam_early, exam_late, exam_night, social_early, social_late, social_night, helper_early, helper_late, helper_night;


    public DailyStats() {
        clear();
    }

    public void add(int shift, boolean exam, BigDecimal val) {
        if (shift == Symbol.SHIFT_NONE) return;

        if (shift == Symbol.SHIFT_EARLY) {
            if (exam) {
                exam_early = exam_early.add(val);
            } else {
                helper_early = helper_early.add(val);
            }
        } else if (shift == Symbol.SHIFT_LATE) {
            if (exam) {
                exam_late = exam_late.add(val);
            } else {
                helper_late = helper_late.add(val);
            }
        } else {
            if (exam) {
                exam_night = exam_night.add(val);
            } else {
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

    public void set(int shift, boolean exam, BigDecimal val) {
        if (shift == Symbol.SHIFT_NONE) return;

        if (shift == Symbol.SHIFT_EARLY) {
            if (exam) {
                exam_early = val;
            } else {
                helper_early = val;
            }
        } else if (shift == Symbol.SHIFT_LATE) {
            if (exam) {
                exam_late = val;
            } else {
                helper_late = val;
            }
        } else {
            if (exam) {
                exam_night = val;
            } else {
                helper_night = val;
            }
        }
    }

    public void add(boolean exam, Symbol symbol) {
        add(symbol.getShift1(), exam, symbol.getStatval1());
        add(symbol.getShift2(), exam, symbol.getStatval2());
    }

    public void replace(boolean exam, Symbol oldSymbol, Symbol newSymbol) {
        subtract(exam, oldSymbol);
        add(exam, newSymbol);
    }

    public void subtract(boolean exam, Symbol symbol) {
        subtract(symbol.getShift1(), exam, symbol.getStatval1());
        subtract(symbol.getShift2(), exam, symbol.getStatval2());
    }

    public void subtract(int shift, boolean exam, BigDecimal val) {
        if (shift == Symbol.SHIFT_NONE) return;

        if (shift == Symbol.SHIFT_EARLY) {
            if (exam) {
                exam_early = exam_early.subtract(val);
            } else {
                helper_early = helper_early.subtract(val);
            }
        } else if (shift == Symbol.SHIFT_LATE) {
            if (exam) {
                exam_late = exam_late.subtract(val);
            } else {
                helper_late = helper_late.subtract(val);
            }
        } else {
            if (exam) {
                exam_night = exam_night.subtract(val);
            } else {
                helper_night = helper_night.subtract(val);
            }
        }
    }

}
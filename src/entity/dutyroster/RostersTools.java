package entity.dutyroster;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 22.07.13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class RostersTools {

    public static BigDecimal getValue(String key, DateMidnight day, boolean holiday, BigDecimal symbolvalue, BigDecimal targethoursperweek, BigDecimal hoursperyear) {
        BigDecimal value = null;

        if (key.equalsIgnoreCase("awert")) {
            if (holiday & day.getDayOfWeek() != DateTimeConstants.SUNDAY) {
                BigDecimal workinghoursperweek = hoursperyear.divide(new BigDecimal(52));
                BigDecimal dayvalue = workinghoursperweek.divide(targethoursperweek);
                value = symbolvalue.add(dayvalue);
            } else {
                value = symbolvalue;
            }
        } else if (key.equalsIgnoreCase("xwert")) {
            if (holiday & day.getDayOfWeek() != DateTimeConstants.SUNDAY) {
                BigDecimal workinghoursperweek = hoursperyear.divide(new BigDecimal(52));
                BigDecimal dayvalue = workinghoursperweek.divide(targethoursperweek);
                value = dayvalue;
            } else {
                value = BigDecimal.ZERO;
            }
        } else if (key.equalsIgnoreCase("kwert")) {
            BigDecimal workinghoursperweek = hoursperyear.divide(new BigDecimal(52));
            BigDecimal dayvalue = workinghoursperweek.divide(targethoursperweek);
            value = dayvalue;
        }

        return value;
    }

}

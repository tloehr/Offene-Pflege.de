package securetoken;

import org.apache.commons.lang3.SystemUtils;
import securetoken.impl.Hardware4Mac;
import securetoken.impl.Hardware4Nix;
import securetoken.impl.Hardware4Win;


public class Hardware {

	/**
	 * Return computer serial number.
	 * 
	 * @return Computer's SN
	 */
	public static final String getSerialNumber() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return Hardware4Win.getSerialNumber();
		}
		if (SystemUtils.IS_OS_LINUX) {
			return Hardware4Nix.getSerialNumber();
		}
		if (SystemUtils.IS_OS_MAC_OSX) {
			return Hardware4Mac.getSerialNumber();
		}
		return null;
	}

}

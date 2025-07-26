package supercritical.api.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import supercritical.SCValues;

@UtilityClass
public final class SCLog {

    public static Logger logger = LogManager.getLogger(SCValues.MODID);
}

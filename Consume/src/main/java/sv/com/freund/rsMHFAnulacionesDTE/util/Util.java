package sv.com.freund.rsMHFAnulacionesDTE.util;

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Sault
 */
public class Util {
    
    static Logger log = Logger.getLogger(Util.class);

    public static boolean isNumeric(String cadena) {
        try {
            if (cadena == null) {
                return false;
            } else if (cadena.length() > 1 && cadena.startsWith("0")) {
                return false;
            } else {
                Integer.parseInt(cadena);
                return true;
            }
        } catch (NumberFormatException nfe) {
            log.trace(nfe);
            return false;
        }
    }
    
    public static String getString(Object o) {
        try {
            if (o == null) {
                return "";
            } else {
                return o.toString();
            }
        } catch (Exception e) {
            log.trace(e);
            return "";
        }
    }
    
    public static BigDecimal getBigDecimal(Object o) {
        try {
            if (o == null) {
                return BigDecimal.ZERO;
            } else {
                return (BigDecimal) o;
            }
        } catch (Exception e) {
            log.trace(e);
            return BigDecimal.ZERO;
        }
    }
    
    public static Long getLong(String o) {
        try {
            if (o == null) {
                return 0l;
            } else {
                return Long.valueOf(o);
            }
        } catch (Exception e) {
            log.trace(e);
            return 0l;
        }
    }
    
}

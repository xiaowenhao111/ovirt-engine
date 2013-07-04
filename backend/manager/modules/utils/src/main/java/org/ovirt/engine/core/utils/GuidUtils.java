package org.ovirt.engine.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.ovirt.engine.core.compat.Guid;

public class GuidUtils {

    public static byte[] toByteArray(UUID uuid) {
        byte[] data = new byte[16];
        long msb = uuid.getMostSignificantBits();
        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (msb & 0xff);
            msb >>= 8;
        }
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 15; i >= 8; i--) {
            data[i] = (byte) (lsb & 0xff);
            lsb >>= 8;
        }
        return data;
    }

    private static final String SEPARATOR = ",";

    /**
     * Gets a string containing multiple <code>Guid</code> values separated by a comma and returns an ArrayList of
     * <code>Guid</code>. If the String is null/empty returns an empty array.
     *
     * @param str
     *            - String which contains list of <code>Guid</code>.
     * @return - Array of <code>Guid</code> type.
     */
    public static ArrayList<Guid> getGuidListFromString(String str) {
        if (StringUtils.isEmpty(str)) {
            return new ArrayList<Guid>();
        }
        return getGuidListFromStringArray(Arrays.asList(str.split(SEPARATOR)));
    }

    /**
     * Gets a List of strings containing multiple <code>Guid</code> values and returns an ArrayList of <code>Guid</code>
     * . If the list is null/empty returns an empty ArrayList.
     *
     * @param strings
     *            - Array of Strings which contains <code>Guid</code> values.
     * @return - Array of <code>Guid</code> type.
     */
    public static ArrayList<Guid> getGuidListFromStringArray(List<String> strings) {
        ArrayList<Guid> guidList = new ArrayList<Guid>();
        if (strings != null && !strings.isEmpty()) {
            for (String guidString : strings) {
                guidList.add(Guid.createGuidFromStringDefaultEmpty(guidString));
            }
        }
        return guidList;
    }
}

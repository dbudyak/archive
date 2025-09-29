package com.thetvdb.util;

import com.thetvdb.model.SerialBasic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by dbudyak on 28.06.16.
 */
public class SerialDateComparator implements Comparator<SerialBasic> {

    private final String DEF_DATE = "1970-01-01";

    @Override
    public int compare(SerialBasic lhs, SerialBasic rhs) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String rawLDate = lhs.getFirstAired();
            String rawRDate = rhs.getFirstAired();
            String defaultDate = DEF_DATE;

            Date datelhs = dateFormat.parse(rawLDate.trim().isEmpty() ? defaultDate : rawLDate);
            Date daterhs = dateFormat.parse(rawRDate.trim().isEmpty() ? defaultDate : rawRDate);

            if (datelhs.getTime() < daterhs.getTime()) return -1;
            if (datelhs.getTime() > daterhs.getTime()) return 1;
            if (datelhs.getTime() == daterhs.getTime()) return 0;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

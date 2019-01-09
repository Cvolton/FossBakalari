package cz.michaelbrabec.fossbakalari;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UkolItem {
    public String predmet;
    public String popis;
    public String status;
    public String nakdy;

    public Date getNakdyDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        Date d = new Date();
        try {
            d = sdf.parse(nakdy);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return d;
    }

    public String getNakdyString(){
        Date d = getNakdyDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(d);
    }
}

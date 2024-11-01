package com.example.matata;

import java.sql.Date;
import java.sql.Time;

public interface QR_displayListener {
    void onDataRead(String Title, String description, String Poster_id, Date date, Time time);


}

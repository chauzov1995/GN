package com.nchauzov.gn;

import java.io.Serializable;

public class class_mtara implements Serializable {

    int ID;
    String NAME;
    int MTARATYPE_ID;
    int STATUS_ID;

    class_mtara(int _ID, String _NAME, int _MTARATYPE_ID, int _STATUS_ID) {

        ID = _ID;
        NAME = _NAME;
        MTARATYPE_ID = _MTARATYPE_ID;
        STATUS_ID = _STATUS_ID;
    }
}

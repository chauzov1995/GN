package com.nchauzov.gn;

import java.io.Serializable;

public class class_operac implements Serializable {


    int ID;
    String NAME;
    Double OPERTIME;
    int MTEXPROCID;
    int MOPERID;
    int NN;
    boolean IS_CHECK_POINT;


    class_operac(int _ID, String _NAME, Double _OPERTIME, int _MTEXPROCID, int _MOPERID, int _NN, boolean _IS_CHECK_POINT) {

        ID = _ID;
        NAME = _NAME;
        OPERTIME = _OPERTIME;
        MTEXPROCID = _MTEXPROCID;
        MOPERID = _MOPERID;
        NN = _NN;
        IS_CHECK_POINT = _IS_CHECK_POINT;
    }
}



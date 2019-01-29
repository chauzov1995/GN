package com.nchauzov.gn;

import java.io.Serializable;

public class class_detal implements Serializable {


    int ID;
    String NAME;
    int CUSTOMID;
    String V;
    String S;
    String G;
    int MTARA_ID;
    String PREF;
    String SV;
    int MOTDELKA_ID_POKR;
    int MOTDELKA_ID_ZVET;
   int  MPARTSGROUPS_ID;
  String  MPARTSGROUPS_NAME;


    class_detal(int _ID, String _NAME, int _CUSTOMID, String _V, String _S, String _G, int _MTARA_ID, String _PREF,
                String _SV, int _MOTDELKA_ID_POKR, int _MOTDELKA_ID_ZVET, int _MPARTSGROUPS_ID, String _MPARTSGROUPS_NAME) {

        ID = _ID;
        NAME = _NAME;
        CUSTOMID = _CUSTOMID;
        V = _V;
        S = _S;
        G = _G;
        MTARA_ID = _MTARA_ID;
        PREF = _PREF;
        SV = _SV;
        MOTDELKA_ID_POKR = _MOTDELKA_ID_POKR;
        MOTDELKA_ID_ZVET = _MOTDELKA_ID_ZVET;
        MPARTSGROUPS_ID=_MPARTSGROUPS_ID;
        MPARTSGROUPS_NAME=_MPARTSGROUPS_NAME;
    }
}



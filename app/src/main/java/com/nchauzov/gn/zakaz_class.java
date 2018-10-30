package com.nchauzov.gn;


public class zakaz_class {

    int id;
    String zakaz;
    String fio;
    String model;
    String ned_izgot;
    String status;

    zakaz_class(int _id, String _zakaz, String _fio, String _model, String _ned_izgot, String _status) {

        id = _id;
        zakaz = _zakaz;
        fio = _fio;
        model = _model;
        ned_izgot = _ned_izgot;
        status = _status;
    }
}
package com.example.joseluis.claustrorecyclerview;

/**
 * Created by Jose Luis on 17/12/2016.
 */
public class Item {

    private String name;
    private Boolean firmado=false;

    public Item(String nome, boolean firma ) {
        name = nome;
        firmado = firma;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFirmado() {
        return firmado;
    }

    public void setFirmado(Boolean firmado) {
        this.firmado = firmado;
    }
}
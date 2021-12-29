package com.example.climalert.Foro;

public class Mensaje {
    private String mensaje;
    private String nombre;
    private int id;
    private int idParent;
    private boolean esDeIncidencia;
    private boolean esDeLogeado;

    public Mensaje() {
    }

    public Mensaje(String mensaje, String nombre, int id, int idParent, boolean esDeIncidencia, boolean esDeLogeado) {
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.id = id;
        this.idParent = idParent;
        this.esDeIncidencia = esDeIncidencia;
        this.esDeLogeado = esDeLogeado;
    }

    public int getId() {
        return id;
    }

    public Mensaje(String mensaje, String nombre) {
        this.mensaje = mensaje;
        this.nombre = nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdParent() {
        return idParent;
    }

    public void setIdParent(int idParent) {
        this.idParent = idParent;
    }

    public boolean isEsDeIncidencia() {
        return esDeIncidencia;
    }

    public void setEsDeIncidencia(boolean esDeIncidencia) {
        this.esDeIncidencia = esDeIncidencia;
    }

    public boolean isEsDeLogeado() {
        return esDeLogeado;
    }

    public void setEsDeLogeado(boolean esDeLogeado) {
        this.esDeLogeado = esDeLogeado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

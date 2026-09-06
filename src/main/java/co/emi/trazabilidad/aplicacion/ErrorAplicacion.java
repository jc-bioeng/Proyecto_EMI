package co.emi.trazabilidad.aplicacion;

public final class ErrorAplicacion extends RuntimeException {
    public enum Codigo { DATO_INVALIDO, NO_ENCONTRADO, CONFLICTO, PERSISTENCIA }
    private final Codigo codigo;

    public ErrorAplicacion(Codigo codigo, String mensaje) {
        this(codigo, mensaje, null);
    }

    public ErrorAplicacion(Codigo codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public Codigo codigo() { return codigo; }
}

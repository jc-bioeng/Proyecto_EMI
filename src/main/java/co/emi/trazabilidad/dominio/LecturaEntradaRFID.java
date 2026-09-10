package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Observacion de entrada sin identidad persistida ni interpretacion operativa.
 * timestamp es el instante de recepcion/generacion en EMI, no de captura RF.
 * EPC conserva literalmente ceros, espacios y capitalizacion, igual que I1:
 * solo se rechazan null y cadenas en blanco. No se impone longitud ni formato
 * hexadecimal estricto sin una regla aprobada. La metadata opcional se expresa
 * con un mapa vacio; claves y valores textuales no pueden ser null.
 */
public record LecturaEntradaRFID(String epc, Instant timestamp, OrigenDatos origenDatos,
                                Map<String, String> metadata) {
    public LecturaEntradaRFID {
        if (epc == null || epc.isBlank()) throw new IllegalArgumentException("epc es obligatorio");
        Objects.requireNonNull(timestamp, "timestamp es obligatorio");
        Objects.requireNonNull(origenDatos, "origenDatos es obligatorio");
        metadata = Map.copyOf(Objects.requireNonNull(metadata, "metadata es obligatoria; puede estar vacia"));
    }
}

#!/bin/sh
set -e

# Compilación de cero
sh compilar.sh

# Reglas del enunciado: sin colecciones de java.util ni arreglos fuera de los TAD
if [ -d src/juego ]; then
    prohibidas=$(grep -rn "java\.util\." src/juego | grep -vE "java\.util\.(Random|Iterator);" || true)
    if [ -n "$prohibidas" ]; then
        echo "Colecciones de java.util fuera de los TAD:"
        echo "$prohibidas"
        exit 1
    fi
    arreglos=$(grep -rnE "\[\]|new [A-Za-z0-9_<>]+ *\[" src/juego | grep -v "main(String\[\] args)" || true)
    if [ -n "$arreglos" ]; then
        echo "Arreglos fuera de los TAD:"
        echo "$arreglos"
        exit 1
    fi
fi

# Verificador del kit y pruebas propias
java -cp out verificador.PruebaMisEstructuras
if [ -d src/pruebas ]; then
    for clase in $(cd src && find pruebas -name "Prueba*.java" | sed 's#\.java$##; s#/#.#g' | sort); do
        java -cp out "$clase"
    done
fi

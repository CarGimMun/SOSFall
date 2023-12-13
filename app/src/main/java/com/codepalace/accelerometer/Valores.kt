package com.codepalace.accelerometer;

data class Valores (
    val listaX: MutableList<Float> = mutableListOf(),
    val listaY: MutableList<Float> = mutableListOf(),
    val listaZ: MutableList<Float> = mutableListOf()){


    fun agregarValores(x: Float, y: Float, z: Float) {
            listaX.add(x)
            listaY.add(y)
            listaZ.add(z)
        }

        //Elvis operator (?:)==> nullable reference but method/attribute supports only non-null.
        // It returns the expression from the left and if it is null the one form the right.
    fun getMaximoX(): Float = listaX.maxOrNull() ?: 0.0f
    fun getMaximoY(): Float = listaY.maxOrNull() ?: 0.0f
    fun getMaximoZ(): Float = listaZ.maxOrNull() ?: 0.0f

    fun limpiarVentanaTiempo(tiempoLimite: Long) {
        while (listaX.isNotEmpty() && (listaX.firstOrNull() ?: 0.0f) < tiempoLimite) {
            listaX.removeAt(0)
        }
    }
}


package com.codepalace.accelerometer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    private lateinit var botonPopup: ImageButton
    private lateinit var valores: Valores
    private val ventanaTiempo = 30000L  // 30 segundos en milisegundos
    private val handler = Handler()
    private var tiempoInicioCondicion: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        botonPopup = findViewById(R.id.warning) // En lugar de val botonpopup: ImageButton = findViewById(R.id.warning)
        botonPopup.visibility = View.GONE
        botonPopup.setOnClickListener {
            // Ocultar el botón pop-up al hacer clic
            botonPopup.visibility = View.GONE
            limpiarVentanaTiempo()
        }

        square = findViewById(R.id.tv_square)
        valores = Valores()
        setUpSensorStuff()

        // Programar la tarea para limpiar la ventana de tiempo cada segundo
        // handler.postDelayed({ limpiarVentanaTiempo() }, ventanaTiempo)
    }
    private fun setUpSensorStuff() {
        // Create the sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            ) } }

    override fun onSensorChanged(event: SensorEvent?) {

        //BOTÓN DE LLAMADA//
        val callButton:Button = findViewById(R.id.callButton)
        callButton.setOnClickListener {
            callButton.visibility = View.GONE
            limpiarVentanaTiempo()

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                val it=intent
                val username=it.getStringExtra("username")
                val password=it.getStringExtra("password")
                val db = DataBase(applicationContext,"SOSFall",null,1)
                val telf = db.getContact(username = username, password = password) // Tu número de teléfono
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$telf")
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                Toast.makeText(this, "No se encontró una aplicación para realizar la llamada", Toast.LENGTH_SHORT).show()
            }}

        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val X = event.values[0]
            val Y = event.values[1]
            val Z = event.values[2]

            // Agregar valores a la lista
            valores.agregarValores(X, Y, Z)
            square.apply {
                translationZ = Z
                translationX = X
                translationY = Y
            }

            // Changes the colour of the square if it's completely flat
            if (valores.getMaximoX().toInt() > 25 || valores.getMaximoY().toInt() > 25 || valores.getMaximoZ().toInt() > 25) {
                // if (valores.getMaximoX().toFloat() == 0.toFloat()) {
                botonPopup.visibility = View.VISIBLE
                callButton.visibility = View.VISIBLE
                val color = Color.RED
                square.setBackgroundColor(color)

                ///////////////////////////////////////////
                // var tiempoInicioCondicion: Long = System.currentTimeMillis()
                if (tiempoInicioCondicion == 1000L) {
                    // Si es la primera vez que se cumple la condición, guardar el tiempo de inicio
                    // Iniciar el temporizador para cambiar el color después de 2 minutos
                    handler.postDelayed({ runOnUiThread {
                        square.setBackgroundColor(Color.parseColor("#FFC0CB")) // Cambiar a color rosa
                    } }, 1 * 15 * 1000)
                    ///////////////////////////////////////////
                } } else {
                botonPopup.visibility = View.GONE
                val color = Color.GREEN
                square.setBackgroundColor(color)
                // Si la condición no se cumple, restablecer el tiempo de inicio de la condición
                tiempoInicioCondicion = 0L
            }
        }

        square.text = "Máximo X: ${valores.getMaximoX().format(2)}\n" +
                "Máximo Y: ${valores.getMaximoY().format(2)}\n" +
                "Máximo Z: ${valores.getMaximoZ().format(2)}"
    }

    override fun onAccuracyChanged(p0: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.

    }
    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
    private fun limpiarVentanaTiempo() {
        // Limpiar valores fuera de la ventana de tiempo
        valores.limpiarVentanaTiempo(System.currentTimeMillis() - ventanaTiempo)
        // Programar la próxima limpieza después de un segundo
        handler.postDelayed({ limpiarVentanaTiempo() }, ventanaTiempo)
    }
}
private fun Float.format(digits: Int) = "%.${digits}f".format(this)
data class Valores(
    private val listaX: MutableList<Float> = mutableListOf(),
    private val listaY: MutableList<Float> = mutableListOf(),
    private val listaZ: MutableList<Float> = mutableListOf()
) {
    fun agregarValores(x: Float, y: Float, z: Float) {
        listaX.add(x)
        listaY.add(y)
        listaZ.add(z)
    }
    fun getMaximoX(): Float = listaX.maxOrNull() ?: 0.0f
    fun getMaximoY(): Float = listaY.maxOrNull() ?: 0.0f
    fun getMaximoZ(): Float = listaZ.maxOrNull() ?: 0.0f

    fun limpiarVentanaTiempo(tiempoLimite: Long) {
        while (listaX.isNotEmpty() && listaX.firstOrNull() ?: 0.0f < tiempoLimite) {
            listaX.removeAt(0)
        }
        while (listaY.isNotEmpty() && listaY.firstOrNull() ?: 0.0f < tiempoLimite) {
            listaY.removeAt(0)
        }
        while (listaZ.isNotEmpty() && listaZ.firstOrNull() ?: 0.0f < tiempoLimite) {
            listaZ.removeAt(0)
        } } }
package com.codepalace.accelerometer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
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
import android.view.WindowManager
import android.os.CountDownTimer
import java.util.Calendar


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    private lateinit var botonPopup: ImageButton
    private lateinit var valores: Valores
    private val ventanaTiempo = 30000L  // 30 segundos en milisegundos
    private val handler = Handler()
    private var tiempoInicioCondicion: Long = 100
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            ) } }
    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        fun suena_alarma(){
            val resourceId = R.raw.alarma
            mediaPlayer = MediaPlayer.create(this, resourceId)
            mediaPlayer?.start()
        }
        fun llamar() {
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
            } }
        fun startCountdown() {
            val countDownTimer = object :
                CountDownTimer(10000, 1000) { // Cuenta atrás de 30 segundos (30000 milisegundos)
                override fun onTick(millisUntilFinished: Long) {
                    // Se ejecuta cada segundo mientras la cuenta atrás está en progreso
                }
                override fun onFinish() {
                    // La cuenta atrás ha finalizado
                    llamar()
                } }
            countDownTimer.start() // Iniciar la cuenta atrás
        }
        fun obtenercalendario(): List <Int> {
            val calendario = Calendar.getInstance()
            val hora = calendario.get(Calendar.HOUR_OF_DAY) // Obtener la hora en formato de 24 horas
            val minutos = calendario.get(Calendar.MINUTE)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val mes = calendario.get(Calendar.MONTH) + 1 // Los meses comienzan desde 0, por eso se suma 1
            val anio = calendario.get(Calendar.YEAR)
            return listOf(minutos,hora,dia,mes,anio)
        }
        //BOTÓN DE LLAMADA//
        val callButton:Button = findViewById(R.id.callButton)
        callButton.visibility=View.GONE
        callButton.setOnClickListener {
            callButton.visibility = View.GONE
            limpiarVentanaTiempo()
            llamar()
           }

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
                startCountdown()
               //suena_alarma()

                ////////OBTENER EL MOMENTO DE LA CAÍDA Y GUARDARLO
                val acc_x = valores.getMaximoX()
                val acc_y = valores.getMaximoY()
                val acc_z = valores.getMaximoZ()
                val (minutos,horas,dia,mes,ano) = obtenercalendario()
                val db = DataBase(applicationContext,"SOSFall",null,1)
                db.registra_caida(acc_x,acc_y,acc_z,minutos,horas,dia,mes,ano)

                /////////////////////////////////////////////////////
            } else {
                botonPopup.visibility = View.GONE
                val color = Color.GREEN
                square.setBackgroundColor(color)
                // Si la condición no se cumple, restablecer el tiempo de inicio de la condición
                tiempoInicioCondicion = 0L
            }}
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
        mediaPlayer?.release()
        mediaPlayer = null
    }
    private fun limpiarVentanaTiempo() {
        // Limpiar valores fuera de la ventana de tiempo
        valores.limpiarVentanaTiempo(System.currentTimeMillis() - ventanaTiempo)
        // Programar la próxima limpieza después de un segundo
        handler.postDelayed({ limpiarVentanaTiempo() }, ventanaTiempo)
    }
    fun obtenerDatosDeTabla(): ArrayList<String> {
        val listaDatos = ArrayList<String>() // Lista para almacenar los datos recuperados
        val db = DataBase(applicationContext, "DataBase", null, 1)
        // Consulta para obtener todos los datos de la tabla
        val query = "SELECT * FROM FALLS LIMIT 5" //
        val cursor = db.readableDatabase.rawQuery(query, null)
        // Iterar a través del cursor para obtener los datos
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Obtener los datos de cada columna (cambia los índices por los nombres de columnas reales)
                    val acc_x = cursor.getFloat(cursor.getColumnIndex("acc_x"))
                    val acc_y = cursor.getFloat(cursor.getColumnIndex("acc_y"))
                    val acc_z = cursor.getFloat(cursor.getColumnIndex("acc_z"))
                    val minuto = cursor.getInt(cursor.getColumnIndex("minuto"))
                    val hora = cursor.getInt(cursor.getColumnIndex("hora"))
                    val dia = cursor.getInt(cursor.getColumnIndex("dia"))
                    val mes = cursor.getInt(cursor.getColumnIndex("mes"))
                    val año = cursor.getInt(cursor.getColumnIndex("año"))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        // Devolver la lista de datos recuperados
        return listaDatos
    }}

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

/////BUGS A ARREGLAR
///CUANDO SE USA LA LLAMADA SE RALLA MUCHÍSIMO Y NO PARA DE LLAMAR

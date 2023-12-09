package com.codepalace.accelerometer

import android.Manifest
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
import android.os.Looper
import java.util.Calendar


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    private lateinit var warning: ImageButton
    private lateinit var valores: Valores
    private val ventanaTiempo = 30000L  // 30 segundos en milisegundos
    private val handler = Handler(Looper.getMainLooper())
    private var tiempoInicioCondicion: Long = 100
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        square = findViewById(R.id.tv_square)
        warning = findViewById(R.id.warning) // En lugar de val botonpopup: ImageButton = findViewById(R.id.warning)
        warning.visibility = View.GONE
        warning.setOnClickListener {
            // Ocultar el botón pop-up al hacer clic
            warning.visibility = View.GONE
            // Programar la tarea para limpiar la ventana
            limpiarVentanaTiempo()
        }
        setUpSensorStuff()

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
        fun suenaAlarma(){
            val resourceId = R.raw.alarma
            mediaPlayer = MediaPlayer.create(this, resourceId)
            mediaPlayer?.start()
        }

        fun llamar(){
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
            }
        }
        fun startCountdown(){
            // Creamos objeto de la cuenta atrás de 30 segundos (30000 milisegundos)
            val countDownTimer= object :
                CountDownTimer (10000,1000){
                override fun onTick(millisUntilFinished: Long) {
                    // Se ejecuta cada segundo mientras la cuenta atrás está en progreso
                }
                override fun onFinish() {
                    // La cuenta atrás ha finalizado
                    llamar()
                }
            }
            // Iniciar la cuenta atrás
            countDownTimer.start()
        }
        fun obtenerCalendario(): List<Int>{
            val calendario= Calendar.getInstance()
            // Obtener la hora en formato de 24 horas
            val hora = calendario.get(Calendar.HOUR_OF_DAY)
            // Obtener los minutos
            val minutos=calendario.get(Calendar.MINUTE)
            // Obtener el día
            val dia=calendario.get(Calendar.DAY_OF_MONTH)
            // Obtener el mes de año pero los meses comienzan en 0 así que le sumamos 1
            val mes=calendario.get(Calendar.MONTH)+1
            // Obtener el año
            val anio=calendario.get(Calendar.YEAR)
            return listOf(minutos,hora,dia,mes,anio)
        }

        //BOTÓN DE LLAMADA//
        val callButton:Button = findViewById(R.id.callButton)
        callButton.setOnClickListener {
            callButton.visibility = View.GONE
            limpiarVentanaTiempo()
            llamar()
        }

        //RECOGIDA VALORES SENSOR Y MUESTRA POR PANTALLA

        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Agregar valores a la lista
            valores.agregarValores(x, y, z)

            //Muestra por pantalla
            square.apply {
                translationZ = z
                translationX = x
                translationY = y
            }

            //CAMBIO DE COLOR EN BASE AL OUTPUT SENSOR

            if (valores.getMaximoX().toInt() > 25 || valores.getMaximoY().toInt() > 25 || valores.getMaximoZ().toInt() > 25) {
                // if (valores.getMaximoX().toFloat() == 0.toFloat()) {
                warning.visibility = View.VISIBLE
                callButton.visibility = View.VISIBLE
                val color = Color.RED
                square.setBackgroundColor(color)
                startCountdown()
                suenaAlarma()

                //OBTENER EL MOMENTO DE LA CAÍDA Y GUARDARLO
                val accX = valores.getMaximoX()
                val accy = valores.getMaximoY()
                val accz = valores.getMaximoZ()
                val (minutos,horas,dia,mes,ano) = obtenerCalendario()
                val db = DataBase(applicationContext,"SOSFall",null,1)
                db.registraCaida(accX,accy,accz,minutos,horas,dia,mes,ano)

            } else {
                warning.visibility = View.GONE
                //val color = Color.GREEN
                //square.setBackgroundColor(color)
                // Si la condición no se cumple, restablecer el tiempo de inicio de la condición
                tiempoInicioCondicion = 0L
            }

            square.text = getString(R.string.MaxText)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
        // Do not put T_ODO or it will throw a java exception

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
        handler.postDelayed({limpiarVentanaTiempo()} , ventanaTiempo)
    }
    fun obtenerDatosTabla(): ArrayList<String> {
        val listaDatos = ArrayList<String>() // Lista para almacenar los datos recuperados
        val db = DataBase(applicationContext, "DataBase", null, 1)
        // Consulta para obtener todos los datos de la tabla
        val query = "SELECT * FROM FALLS" //
        val cursor = db.readableDatabase.rawQuery(query, null)
        // Iterar a través del cursor para obtener los datos
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Obtener los datos de cada columna (cambia los índices por los nombres de columnas reales)
                    val accX = cursor.getFloat(cursor.getColumnIndex("accX"))
                    val accY = cursor.getFloat(cursor.getColumnIndex("accY"))
                    val accZ = cursor.getFloat(cursor.getColumnIndex("accZ"))
                    val minuto = cursor.getInt(cursor.getColumnIndex("minuto"))
                    val hora = cursor.getInt(cursor.getColumnIndex("hora"))
                    val dia = cursor.getInt(cursor.getColumnIndex("dia"))
                    val mes = cursor.getInt(cursor.getColumnIndex("mes"))
                    val anio = cursor.getInt(cursor.getColumnIndex("año"))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        // Devolver la lista de datos recuperados
        return listaDatos
    }}

private fun Float.format(digits: Int) = "%.${digits}f".format(this)


//CLASE  - DATOS DEL ACELERÓMETRO Y LIMPIEZA DE VENTANA
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
    //Elvis operator (?:)==> nullable reference but method/attribute supports only non-null.
    // It returns the expression from the left and if it is null the one form the right.
    fun getMaximoX(): Float = listaX.maxOrNull() ?: 0.0f
    fun getMaximoY(): Float = listaY.maxOrNull() ?: 0.0f
    fun getMaximoZ(): Float = listaZ.maxOrNull() ?: 0.0f

    fun limpiarVentanaTiempo(tiempoLimite: Long) {
        while (listaX.isNotEmpty() && (listaX.firstOrNull() ?: 0.0f) < tiempoLimite) {
            listaX.removeAt(0)
        }
        while (listaY.isNotEmpty() && (listaY.firstOrNull() ?: 0.0f) < tiempoLimite) {
            listaY.removeAt(0)
        }
        while (listaZ.isNotEmpty() && (listaZ.firstOrNull() ?: 0.0f) < tiempoLimite) {
            listaZ.removeAt(0)
        }
    }
}
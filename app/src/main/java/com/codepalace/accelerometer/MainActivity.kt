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
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import java.util.*
import com.codepalace.accelerometer.databinding.ActivityLoginBinding
import java.util.Calendar

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var db: dbCaidasHelper
    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    private lateinit var botonPopup: ImageButton
    private lateinit var valores: Valores
    private val ventanaTiempo = 300000000L  // 30 segundos en milisegundos
    private val handler = Handler()
    private var tiempoInicioCondicion: Long = 100
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        db = dbCaidasHelper(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        botonPopup = findViewById(R.id.warning)
        botonPopup.visibility = View.GONE

        square = findViewById(R.id.tv_square)
        valores = Valores()
        setUpSensorStuff()
    }

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )}}

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

        fun registrar_caida() {
            val calendario = Calendar.getInstance()
            val hora = calendario.get(Calendar.HOUR_OF_DAY) // Obtener la hora en formato de 24 horas
            val minutos = calendario.get(Calendar.MINUTE)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val mes = calendario.get(Calendar.MONTH) + 1 // Los meses comienzan desde 0, por eso se suma 1
            val anio = calendario.get(Calendar.YEAR)
            ////////OBTENER EL MOMENTO DE LA CAÍDA Y GUARDARLO///////
            val acc_x = valores.getMaximoX()
            val acc_y = valores.getMaximoY()
            val acc_z = valores.getMaximoZ()
            val caida=Caidas(0,acc_x,acc_y,acc_z,minutos,hora,dia,mes,anio) ////VER SI SE PUEDE PONER ID CADA VEZ MÁS
            db.registroCaidas(caida)
            Toast.makeText(this,"Caida Registrada",Toast.LENGTH_SHORT).show()
        }

        var countDownTimer2 = object :
            CountDownTimer(10000, 1000) { // Cuenta atrás de 10 segundos
            val contador: TextView= findViewById(R.id.contador)
            override fun onTick(millisUntilFinished: Long) {
                contador.text=millisUntilFinished.toString()
            }
            override fun onFinish() {
//                llamar()// POR AHORA LA QUITO, extraño que funciona lo de 1 vez con sonar la alarma y no con llamar
                //registrar_caida()
                suena_alarma()
            }}
        fun startCountdown() {
            countDownTimer2.start() // Iniciar la cuenta atrás
        }
        fun stopCountdown(){
            countDownTimer2.cancel()
        }

        //TABLA DEL REGISTRO DE CAIDAS
        val tablaCaidas:TableLayout =findViewById(R.id.tablaCaidas)
        fun displaycaidas() {
            var listaCaidas = db.get5registros()
        }

        //BOTÓN DE LLAMADA//
        val callButton:ImageButton = findViewById(R.id.callButton)
        callButton.visibility=View.GONE
        callButton.setOnClickListener {
            callButton.visibility = View.GONE
            registrar_caida()
            llamar()
            stopCountdown()
            limpiarVentanaTiempo()
        }
        //BOTÓN POPUP//
        botonPopup.setOnClickListener {
            botonPopup.visibility = View.GONE
            stopCountdown()
            limpiarVentanaTiempo()
        }

        //CAMBIO DE EVENTOS Y IMPRESIÓN DE MÁXIMOS//
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val X = event.values[0]
            val Y = event.values[1]
            val Z = event.values[2]
            square.apply {
                translationZ = Z
                translationX = X
                translationY = Y
            }
            square.text =
                "Máximo X: ${valores.getMaximoX().format(2)}\n" +
                        "Máximo Y: ${valores.getMaximoY().format(2)}\n" +
                        "Máximo Z: ${valores.getMaximoZ().format(2)}"

            //MODIFICACIÓN DE UMBRAL Y LÓGICA DE CAÍDAS//
            var contador_estado: Int =0
            if (valores.getMaximoX().toInt() > 25 || valores.getMaximoY().toInt() > 25 || valores.getMaximoZ().toInt() > 25) {
                //     if (valores.getMaximoX().toFloat() == 0.toFloat()) {
                botonPopup.visibility = View.VISIBLE
                callButton.visibility = View.VISIBLE
                var color = Color.RED
                square.setBackgroundColor(color)

                if  (contador_estado==1){
                    stopCountdown()
                }else{
                    //displaycaidas()
                    startCountdown()///corregir lo de llamar aun cuando se pulse
                }
                contador_estado=1
            } else {
                contador_estado= 0
                stopCountdown()
                valores.agregarValores(X, Y, Z)
                botonPopup.visibility = View.GONE
                var color = Color.GREEN
                square.setBackgroundColor(color)
                tiempoInicioCondicion = 0L
            }}}
    override fun onAccuracyChanged(p0: Sensor?, accuracy: Int) {
        // Implementar si la precisión del sensor cambia
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
        }}}

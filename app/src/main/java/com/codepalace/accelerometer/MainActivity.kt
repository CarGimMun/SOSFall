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
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import android.view.WindowManager
import android.os.CountDownTimer
import android.os.Looper
import android.widget.ListView
import android.widget.SimpleAdapter

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    private lateinit var warning: ImageButton
    private lateinit var valores: Valores
    private val ventanaTiempo = 300000000L  // 30 segundos en milisegundos
    private val handler =Handler(Looper.getMainLooper())
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
        valores=Valores()
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

        val it=intent
        val username=it.getStringExtra("username")
        val password=it.getStringExtra("password")
        val db = DataBase(applicationContext,"SOSFall",null,1)

        fun suenaAlarma(){
            val resourceId = R.raw.alarma
            mediaPlayer = MediaPlayer.create(this, resourceId)
            mediaPlayer?.start()
        }

        fun llamar(){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                val db = DataBase(applicationContext,"SOSFall",null,1)
                val telf = db.getContact(username = username, password = password)

                // PARA NÚMERO DE EMERGENCY, LA DOCUMENTACIÓN INDICA QUE NO SE DEBE USAR ACTION_CALL
                // SINO QUE HAY QUE EMPLEAR ACTION_DIAL
                if(telf=="112"){
                    intent = Intent(Intent.ACTION_DIAL)
                }else {
                    intent = Intent(Intent.ACTION_CALL)
                }
                intent.data = Uri.parse("tel:$telf")
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                Toast.makeText(this, "No se encontró una aplicación para realizar la llamada", Toast.LENGTH_SHORT).show()
            }
        }


        var countDownTimer= object: CountDownTimer(1000,100){
            val contador: TextView= findViewById(R.id.contador)

            override fun onTick(millisUntilFinished: Long) {
                // Se ejecuta cada segundo mientras la cuenta atrás está en progreso
                contador.text=millisUntilFinished.toString()
            }
            override fun onFinish() {
                // La cuenta atrás ha finalizado
                // llamar()
                //suenaAlarma()

            }
        }

        fun startCountdown(){
            // Iniciar la cuenta atrás
            countDownTimer.start()
            }

        fun stopCountdown() {
            countDownTimer.cancel()
        }

        //BOTÓN DE LLAMADA//-------

        // Creamos objeto de la cuenta atrás de 30 segundos (30000 milisegundos)

        val callButton:ImageButton = findViewById(R.id.callButton) //XML
        callButton.visibility=View.GONE



        callButton.setOnClickListener {
            callButton.visibility = View.GONE
            llamar()
            stopCountdown()
            limpiarVentanaTiempo()
        }
        warning.setOnClickListener {
            warning.visibility = View.GONE
        }

        //RECOGIDA VALORES SENSOR Y MUESTRA POR PANTALLA

        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION){
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        //Muestra por pantalla
        square.apply {
            translationZ = z
            translationX = x
            translationY = y
        }
        square.text = getString(R.string.MaxText)

        //LÓGICA DE CAÍDAS Y CAMBIO DE COLOR EN BASE AL OUTPUT SENSOR--------
        var contador_estado: Int = 0
        if (valores.getMaximoX().toInt() > 25 || valores.getMaximoY().toInt() > 25 || valores.getMaximoZ().toInt() > 25) {
        //if (valores.getMaximoX().toFloat() == 0.toFloat()) {

            db.registraCaida(username!!,valores.getMaximoX(), valores.getMaximoY(),valores.getMaximoZ())
            warning.visibility = View.VISIBLE
            callButton.visibility = View.VISIBLE
            var color = Color.RED
            square.setBackgroundColor(color)

            if (contador_estado == 1) {
                stopCountdown()
            } else {
                //displaycaidas()
                startCountdown()///corregir lo de llamar aun cuando se pulse
            }
            contador_estado = 1
        } else {
            stopCountdown()
            // Agregar valores a la lista
            valores.agregarValores(x, y, z)
            warning.visibility = View.GONE
            var color = Color.GREEN
            square.setBackgroundColor(color)
            tiempoInicioCondicion = 0L
            contador_estado = 0
        }

        }
        val fall=db.getFalls("*",username!!)
        val lst: ListView= findViewById(R.id.tablaMainCaidas)
        val list = ArrayList<HashMap<String, String>>()
        var item: HashMap<String, String>
        val sa: SimpleAdapter
        if(fall.size>=5) {
            for (i in 0 until 5) {
                item = HashMap<String, String>()
                item["col1"] = fall!![1]
                item["col2"] = fall!![2]
                item["col3"] = fall!![3]
                item["col4"] = fall!![5]
                item["col5"] = fall!![4]
                list.add(item)
            }
            sa= SimpleAdapter(
                this,
                list,R.layout.multi_line,
                arrayOf("col1", "col2", "col3", "col4", "col5"),
                intArrayOf(R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e)
            )
        }else{
            item = HashMap<String, String>()
            item["col1"] = fall!![0]
            list.add(item)
            sa = SimpleAdapter(
                this,
                list,R.layout.single_line,
                arrayOf("col1"),
                intArrayOf(R.id.line_a)
            )
        }
        lst.adapter= sa
        val headLst: ListView= findViewById(R.id.titleCaidas)
        val headList = ArrayList<HashMap<String, String>>()

        var header: HashMap<String, String> = HashMap<String, String>()
        header["col1"] = "AccX"
        header["col2"] = "AccY"
        header["col3"] = "AccZ"
        header["col4"] = "Fecha"
        header["col5"] = "Hora"
        headList.add(header)

        val Hsa: SimpleAdapter = SimpleAdapter(
            this,
            headList,R.layout.title_line,
            arrayOf("col1", "col2", "col3", "col4", "col5"),
            intArrayOf(R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e)
        )
        headLst.adapter=Hsa
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
        val postDelayed = handler.postDelayed({ limpiarVentanaTiempo() }, ventanaTiempo)
    }



private fun Float.format(digits: Int) = "%.${digits}f".format(this)


}
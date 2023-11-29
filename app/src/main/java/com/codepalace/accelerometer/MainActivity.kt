package com.codepalace.accelerometer

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    // private static final int REQUEST_CALL =1
    // private TextView callText
    //private Button callBtn


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        square = findViewById(R.id.Figure)

        setUpSensorStuff()
        // callText=findViewID(R.id.callTxt)
        // callBtn=findViewID(R.id.callButton)
        // callBtn.setOnClickListener(new View.OnClickListener(){
        //    @Override
        //   public void onClick(View v)(
        //       CallButton()
        //   )
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
            )

        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
            // Checks for the sensor we have registered
            if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")

                // Sides = Tilting phone left(10) and right(-10)
                val sides = event.values[0]

                // Up/Down = Tilting phone up(10), flat (0), upside-down(-10)
                val upDown = event.values[1]

                square.apply {
                    rotationX = upDown * 3f
                    rotationY = sides * 3f
                    rotation = -sides
                    translationX = sides * -10
                    translationY = upDown * 10



                    //            val   alpha = 0.8

                    //          val gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                    //          val gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                    //         val gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                    //       val linear_acceleration[0] = event.values[0] - gravity[0]
                    //      val linear_acceleration[1] = event.values[1] - gravity[1]
                    //       val linear_acceleration[2] = event.values[2] - gravity[2]

                }

                // Changes the colour of the square if it's completely flat
                val color = if (upDown.toInt() == 0 || sides.toInt() == 0) Color.GREEN else Color.RED
                square.setBackgroundColor(color)

                square.text = "up/down ${upDown.toInt()}\nleft/right ${sides.toInt()}"
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            return
        }

        override fun onDestroy() {
            sensorManager.unregisterListener(this)
            super.onDestroy()
        }
    // private void CallButton(){
    //  String number= callTxt
    // }
}



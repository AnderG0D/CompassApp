package com.example.brujula;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {
    // Interfaz obligado usar metodos

    // Objeto para guardar grados de inclinacion
    Float azimuth_angle;
    SensorManager compassSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    TextView textView_grados;
    ImageView imageView_brujula;
    float current_degree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el Gestor de sensores
        compassSensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);
        accelerometer =
                compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer =
                compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    // Metodo que permite reactivar sensores cuando la actividad
    // Se reanuda
    protected void onResume() {
        super.onResume();

        compassSensorManager.registerListener(MainActivity.this,
                accelerometer, SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(MainActivity.this,
                magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    // Ahora desactivar o apagar escuchador de evento para sensores
    protected void onPause() {
        super.onPause();
        compassSensorManager.unregisterListener(this);
    }

    // Variables globales
    // Para leer datos de los sensores
    float[] accel_read;
    float[] magnetic_read;

    @Override
    public void onSensorChanged(SensorEvent event) {
        textView_grados =
                findViewById(R.id.textView_grados);
        imageView_brujula =
                findViewById(R.id.imageView_brujula);

        // Leer datos de sensores
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accel_read = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetic_read = event.values;
        }

        // Revisar si los datos de sensores son disponibles
        // (diferentes a null)
        if (accel_read != null && magnetic_read != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean successful_read =
                    SensorManager.getRotationMatrix(R, I, accel_read,
                            magnetic_read);

            // Revisar si la operación anterior es exitosa
            if (successful_read) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth_angle = orientation[0];
                float degress = ((azimuth_angle * 100f) / 3.14f);
                int degressInt = Math.round(degress);

                // Mostrar angulo de inclinación
                textView_grados.setText(Integer.toString(degressInt) +
                        (char) 0x00B0 + " al norte absoluto");

                // Mostrar la imagen con un efecto de animación
                RotateAnimation rotateAnimation =
                        new RotateAnimation(current_degree, -degressInt,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);

                rotateAnimation.setDuration(100);
                rotateAnimation.setFillAfter(true);
                imageView_brujula.startAnimation(rotateAnimation);
                current_degree = -degress;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
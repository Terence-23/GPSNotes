package com.example.gpsnotes

import android.Manifest.permission
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


class MainActivity : AppCompatActivity() {

    lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        setContentView(R.layout.activity_main)
        val todoText: EditText = findViewById(R.id.NoteInput)
        val saveButton: Button = findViewById(R.id.SaveButton)

        saveButton.setOnClickListener{

                if (todoText.text.toString() != "") {
                    Log.d("APP", "OnClickListener start")
                    val message = todoText.text.toString()
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(permission.ACCESS_FINE_LOCATION), 2137
                        )
                        return@setOnClickListener
                    }
                    Log.d("APP", "get location start")
                    val token = CancellationTokenSource().token
                    val locationTask = locationClient.getCurrentLocation(/* p0 = */ 100, token)
//                    while (!locationTask.isComplete){Thread.sleep(10)}

                    locationTask.addOnSuccessListener {
                        val location = locationTask.result
                        Log.d("APP", "have location")

                        val latString: String = Location.convert(location.latitude, Location.FORMAT_DEGREES)
                        val lonString: String = Location.convert(location.longitude, Location.FORMAT_DEGREES)

                        val logPath = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"$latString, $lonString.txt"
                        )
                        Log.d("APP", logPath.absolutePath.toString())

                        writeToFile(logPath, message)
                        Log.d("APP", "OnClickListener end")
                        Toast.makeText(this@MainActivity, "Saved!", Toast.LENGTH_LONG).show()
                    }
                    locationTask.addOnFailureListener{
                        Toast.makeText(this@MainActivity, "Couldn't save", Toast.LENGTH_LONG).show()
                    }

                    Log.d("APP", "return control")

                }

        }
    }

    private fun writeToFile(path: File, message: String) {
        try {
            val outputStreamWriter = OutputStreamWriter(
                FileOutputStream(path, true)
            )
            outputStreamWriter.write(message)
            outputStreamWriter.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
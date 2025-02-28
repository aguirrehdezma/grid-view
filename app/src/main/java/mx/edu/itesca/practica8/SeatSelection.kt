package mx.edu.itesca.practica8

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SeatReservationActivity : AppCompatActivity() {
    companion object {
        const val MAX_SEATS = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seat_selection)

        val extras = intent.extras
        val movieTitle = extras?.getString("titulo") ?: ""
        val headerImage = extras?.getInt("header") ?: 0
        val movieSynopsis = extras?.getString("sinopsis") ?: ""

        val titleTextView: TextView = findViewById(R.id.titleSeats)
        titleTextView.text = movieTitle

        fun disableReservedSeats(radioGroup: RadioGroup) {
            for (i in 0 until radioGroup.childCount) {
                val radioButton = radioGroup.getChildAt(i)
                if (radioButton is RadioButton) {
                    val seatNumber = radioButton.text.toString().toIntOrNull()
                    if (seatNumber != null && ReservationManager.getReservations(movieTitle).contains(seatNumber)) {
                        radioButton.isEnabled = false
                        radioButton.setBackgroundResource(R.drawable.radio_disabled)
                    }
                }
            }
        }

        val row1: RadioGroup = findViewById(R.id.row1)
        val row2: RadioGroup = findViewById(R.id.row2)
        val row3: RadioGroup = findViewById(R.id.row3)
        val row4: RadioGroup = findViewById(R.id.row4)

        disableReservedSeats(row1)
        disableReservedSeats(row2)
        disableReservedSeats(row3)
        disableReservedSeats(row4)

        row1.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                row2.clearCheck()
                row3.clearCheck()
                row4.clearCheck()
            }
        }
        row2.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                row1.clearCheck()
                row3.clearCheck()
                row4.clearCheck()
            }
        }
        row3.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                row1.clearCheck()
                row2.clearCheck()
                row4.clearCheck()
            }
        }
        row4.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                row1.clearCheck()
                row2.clearCheck()
                row3.clearCheck()
            }
        }

        val confirm: Button = findViewById(R.id.confirm)
        confirm.setOnClickListener {
            val selectedSeatId = when {
                row1.checkedRadioButtonId != -1 -> row1.checkedRadioButtonId
                row2.checkedRadioButtonId != -1 -> row2.checkedRadioButtonId
                row3.checkedRadioButtonId != -1 -> row3.checkedRadioButtonId
                row4.checkedRadioButtonId != -1 -> row4.checkedRadioButtonId
                else -> -1
            }

            if (selectedSeatId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedSeatId)
                val seatNumber = selectedRadioButton.text.toString()
                if (!selectedRadioButton.isEnabled) {
                    Toast.makeText(this, "La silla ya está ocupada", Toast.LENGTH_LONG).show()
                } else {
                    selectedRadioButton.isEnabled = false
                    selectedRadioButton.setBackgroundResource(R.drawable.radio_disabled)

                    ReservationManager.reserveSeat(movieTitle, seatNumber.toInt())
                    Toast.makeText(this, "Silla $seatNumber ocupada", Toast.LENGTH_LONG).show()

                    val remainingSeats = MAX_SEATS - ReservationManager.getReservations(movieTitle).size

                    val intent = Intent(this, detalle_pelicula::class.java)
                    intent.putExtra("titulo", movieTitle)
                    intent.putExtra("header", headerImage)
                    intent.putExtra("sinopsis", movieSynopsis)
                    intent.putExtra("numberSeats", remainingSeats)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Por favor, selecciona una silla", Toast.LENGTH_LONG).show()
            }
        }
    }
}

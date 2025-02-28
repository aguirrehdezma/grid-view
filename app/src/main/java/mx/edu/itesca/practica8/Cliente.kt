package mx.edu.itesca.practica8
import java.io.Serializable

data class Cliente(val nombre: String, val estado: String, val fila: Int, val asiento: Int) : Serializable

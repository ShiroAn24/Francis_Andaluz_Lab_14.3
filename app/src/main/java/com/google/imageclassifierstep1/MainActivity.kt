import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {
    private lateinit var tflite: Interpreter
    private val tamaño_del_input = 224  // Ajusta el tamaño según el modelo
    private val tamaño_del_output = 4   // Ajusta el tamaño según el modelo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cargar el modelo TFLite
        try {
            tflite = Interpreter(loadModelFile())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Crear input y output según los tamaños definidos
        val input = Array(1) { FloatArray(tamaño_del_input) }
        val output = Array(1) { FloatArray(tamaño_del_output) }

        // Ejecutar el modelo con input y output
        tflite.run(input, output)

        // Muestra el resultado en la pantalla
        val resultView: TextView = findViewById(R.id.result_text)
        resultView.text = "Resultado: ${output[0][0]}"  // Ajusta según la salida esperada
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        assets.openFd("model.tflite").use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { fileInputStream ->
                val fileChannel = fileInputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }
}

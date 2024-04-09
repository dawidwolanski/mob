package lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import lab03.Lab03Activity
import pl.wsei.pam.lab01.Lab01Activity
import pl.wsei.pam.lab01.R

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab02)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favorites_grid)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button6x6 = findViewById<Button>(R.id.main_6_6_board)
        val button4x4 = findViewById<Button>(R.id.main_4_4_board)
        val button4x3 = findViewById<Button>(R.id.main_4_3_board)
        val button3x2 = findViewById<Button>(R.id.main_3_2_board)

        val buttonClickListener = View.OnClickListener { view ->
            if (view is Button) {
                val tag: String? = view.tag as String?
                val tokens: List<String>? = tag?.split(" ")
                val rows = tokens?.get(0)?.toInt()
                val columns = tokens?.get(1)?.toInt()
                if (rows != null && columns != null) {
                    //Toast.makeText(this, "rows: ${rows}, columns: ${columns}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Lab03Activity::class.java)
                    intent.putExtra("rows", rows)
                    intent.putExtra("columns", columns)
                    startActivity(intent)
                }
            }
        }

        button6x6.setOnClickListener(buttonClickListener)
        button4x4.setOnClickListener(buttonClickListener)
        button4x3.setOnClickListener(buttonClickListener)
        button3x2.setOnClickListener(buttonClickListener)

    }
}
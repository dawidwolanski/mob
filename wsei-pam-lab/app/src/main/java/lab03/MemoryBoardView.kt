package lab03

import android.graphics.Color
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import java.util.Stack

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private var icons: List<Int> = listOf(
        R.drawable.baseline_rocket_launch_24,
        R.drawable.baseline_beach_access_24,
        R.drawable.baseline_local_florist_24,
        R.drawable.baseline_brightness_2_24,
        R.drawable.baseline_drive_eta_24,
        R.drawable.baseline_paid_24,
        R.drawable.baseline_notifications_24,
        R.drawable.baseline_water_drop_24,
        R.drawable.baseline_train_24,
        R.drawable.baseline_temple_hindu_24,
        R.drawable.baseline_sports_basketball_24,
        R.drawable.baseline_savings_24,
        R.drawable.baseline_remove_red_eye_24,
        R.drawable.baseline_sports_esports_24,
        R.drawable.baseline_sentiment_satisfied_alt_24,
        R.drawable.baseline_science_24,
        R.drawable.baseline_radio_24,
        R.drawable.baseline_palette_24

    )

    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
            it.shuffle()
        }

        // tu umieść kod pętli tworzący wszystkie karty, który jest obecnie
        // w aktywności Lab03Activity

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val btn = ImageButton(gridLayout.context).apply {
                    tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    layoutParams.setMargins(10, 10, 10, 10)
                    this.layoutParams = layoutParams
                    setBackgroundColor(Color.parseColor("#9e7fc9"))
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setPadding(20, 20, 20, 20)
                }

                val resourceImage = shuffledIcons.removeAt(0)
                addTile(btn, resourceImage)
                gridLayout.addView(btn)
            }
        }
    }

    private val deckResource: Int = R.drawable.deck
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { (e) -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)


    private fun onClickTile(v: View) {
        val tile = tiles[v.tag]
        matchedPair.push(tile)
        val matchResult = logic.process {
            tile?.tileResource ?: -1
        }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))
        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, R.drawable.deck)
        tiles[button.tag.toString()] = tile
    }

    fun getState(): IntArray {
        return tiles.values.map { tile -> if (tile.revealed) tile.tileResource else -1 }.toIntArray()
    }

    fun setState(state: IntArray) {
        tiles.values.forEachIndexed { index, tile ->
            val resource = state[index]
            if (resource != -1) {
                tile.revealed = true
                tile.button.setImageResource(resource)
            } else {
                tile.revealed = false
                tile.button.setImageResource(tile.deckResource)
            }
        }
    }


}
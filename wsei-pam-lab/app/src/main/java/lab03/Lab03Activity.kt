package lab03

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import java.util.Random
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView

    private lateinit var completionPlayer: MediaPlayer
    private lateinit var negativePLayer: MediaPlayer

    private var isSound: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)

        //Add toolbar to the top of the screen
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memory_game)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val columns = intent.getIntExtra("columns", 4)
        val rows = intent.getIntExtra("rows", 4)

        mBoard = findViewById(R.id.memory_game)
        mBoard.columnCount = columns
        mBoard.rowCount = rows

        mBoardModel = MemoryBoardView(mBoard, columns, rows)

        if (savedInstanceState != null) {
            val gameState = savedInstanceState.getIntArray("gameState")
            if (gameState != null) {
                mBoardModel.setState(gameState)
            }
        }

        mBoardModel.setOnGameChangeListener { e ->
            runOnUiThread {
                when (e.state) {
                    GameStates.Matching -> {
                        e.tiles.forEach { tile ->
                            tile.revealed = true

                        }
                    }
                    GameStates.Match -> {
                        if (isSound) {
                            completionPlayer.start()
                        }

                        e.tiles.forEach { tile ->
                            tile.revealed = true
                            animatePairedButton(tile.button) {}
                        }
                    }
                    GameStates.NoMatch -> {
                        if (isSound) {
                            negativePLayer.start()
                        }

                        e.tiles.forEach { tile ->
                            tile.revealed = true
                            animateNotMatchingButton(tile.button) {
                                resetTileImage(e)
                            }

                        }

                    }
                    GameStates.Finished -> {
                        Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun resetTileImage(e: MemoryGameEvent) {
        Timer().schedule(700) {
            runOnUiThread {
                e.tiles.forEach { tile ->
                    tile.revealed = false
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("gameState", mBoardModel.getState())
    }

    private fun animatePairedButton(button: ImageButton, action: Runnable ) {
        val set = AnimatorSet()
        val random = Random()
        button.pivotX = random.nextFloat() * 200f
        button.pivotY = random.nextFloat() * 200f

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scalingX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scalingY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)
        set.startDelay = 500
        set.duration = 2000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scalingX, scalingY, fade)
        set.addListener(object: Animator.AnimatorListener {

            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0.0f
                action.run()
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        set.start()
    }

    private fun animateNotMatchingButton(button: ImageButton, action: Runnable ) {
        val set = AnimatorSet()
        val random = Random()
        button.pivotX = random.nextFloat() * 200f
        button.pivotY = random.nextFloat() * 200f

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 0f,-10f, 10f,-10f, 10f, 0f)

        set.startDelay = 500
        set.duration = 500
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation)
        set.addListener(object: Animator.AnimatorListener {

            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                action.run()
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        set.start()
    }

    override fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePLayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }

    override fun onPause() {
        super.onPause()
        completionPlayer.release()
        negativePLayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean  {
        val inflater: MenuInflater  = menuInflater
        inflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.board_activity_sound -> {
                if (isSound) {
                    Toast.makeText(this, "Sound turned off", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_off_24)
                    isSound = false
                } else {
                    Toast.makeText(this, "Sound turned on", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_up_24)
                    isSound = true
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
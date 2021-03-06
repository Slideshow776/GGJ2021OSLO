package no.sandramoen.ggj2021oslo.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import no.sandramoen.ggj2021oslo.actors.*
import no.sandramoen.ggj2021oslo.utils.BaseActor
import no.sandramoen.ggj2021oslo.utils.BaseGame
import no.sandramoen.ggj2021oslo.utils.BaseScreen
import no.sandramoen.ggj2021oslo.utils.GameUtils

class LevelScreen : BaseScreen() {

    private lateinit var player: Player
    private lateinit var bed: Bed
    private lateinit var computer: Computer
    private lateinit var cat: Cat

    private lateinit var ground1: Ground
    private lateinit var ground2: Ground
    private lateinit var groundBackup: Ground

    private lateinit var pile1: Pile
    private lateinit var pile2: Pile

    private var gameStarted = false
    private var gameOver = false
    private var gameOverTime = 0f
    private var gameTime = 0f
    private var itemToCreate = "sock"

    private lateinit var scoreLabel: Label
    private lateinit var highScoreLabel: Label
    private lateinit var gameOverLabel: Label
    private lateinit var restartLabel: Label

    private lateinit var gameOverTable: Table

    override fun initialize() {
        // println("Level id: ${MathUtils.random(0, 10_000)}")

        // audio
        BaseGame.alarm1Sound!!.play(BaseGame.soundVolume)

        // actors
        ground1 = Ground(0f, 0f, mainStage)
        ground2 = Ground(100f, 0f, mainStage)
        groundBackup = Ground(50f, 0f, mainStage)

        pile1 = Pile(100f, 18f, mainStage)
        pile2 = Pile(170f, 18f, mainStage)
        cat = Cat(150f, 95f, mainStage)

        bed = Bed(0f, 18f, mainStage)
        computer = Computer(18f, 17f, mainStage)
        player = Player(15f, 30f, mainStage)

        // labels
        scoreLabel = Label("00000", BaseGame.labelStyle)
        scoreLabel.setFontScale(1.5f)
        highScoreLabel = Label("HI ${BaseGame.highScore}", BaseGame.labelStyle)
        highScoreLabel.setFontScale(1.5f)
        var scoreTable = Table()
        scoreTable.add(highScoreLabel).padRight(Gdx.graphics.width * .04f)
        scoreTable.add(scoreLabel)

        gameOverLabel = Label("GAME OVER!", BaseGame.labelStyle)
        gameOverLabel.setFontScale(1.5f)
        restartLabel = Label("jump to restart", BaseGame.labelStyle)
        gameOverTable = Table()
        gameOverTable.add(gameOverLabel).row()
        gameOverTable.add(restartLabel)
        gameOverTable.color.a = 0f

        uiTable.add(scoreTable).expand().top().right().pad(10f).row()
        uiTable.add(gameOverTable).expand().top()
        // uiTable.debug = true
    }

    override fun update(dt: Float) {
        if (gameOver && gameOverTime < 5f) gameOverTime += dt

        if (gameStarted && !gameOver) gameTime += dt
        scoreLabel.setText("${score()}")

        // actors
        for (pile: BaseActor in BaseActor.getList(mainStage, Pile::class.java.canonicalName)) {
            if (player.overlaps(pile)) gameOver()
        }

        cat.act(dt, player)
        if (player.overlaps(cat) && !gameOver) {
            when(MathUtils.random(1, 2)) {
                1-> BaseGame.catAngry1Sound!!.play(BaseGame.soundVolume)
                2-> BaseGame.catAngry2Sound!!.play(BaseGame.soundVolume)
            }
            gameOver()
        }

        for (item: BaseActor in BaseActor.getList(mainStage, PickUpItem::class.java.canonicalName)) {
            if (player.overlaps(item)) {
                var temp = item as PickUpItem
                player.addItem(temp.itemName)
                temp.remove()
                if (player.clothing == "sock") itemToCreate = "shirt"
                else if (player.clothing == "shirt") itemToCreate = "tie"
                else if (player.clothing == "tie") itemToCreate = "coffee"
            }
        }

        // scaling difficulty
        when {
            gameTime > 80f -> if (!gameOver) setActorSpeed(90f)
            gameTime > 70f -> if (!gameOver) setActorSpeed(80f)
            gameTime > 60f -> if (!gameOver) setActorSpeed(70f)
            gameTime > 50f -> if (!gameOver) setActorSpeed(60f)
            gameTime > 45f -> if (!gameOver) setActorSpeed(55f)
            gameTime > 30f -> if (!gameOver) setActorSpeed(50f)
            gameTime > 20f -> if (!gameOver) setActorSpeed(45f)
            gameTime > 10f -> if (!gameOver) setActorSpeed(40f)
            gameTime > 5f -> if (!gameOver) setActorSpeed(35f)
            else -> if (!gameOver && gameStarted) setActorSpeed(-30f)
        }

        createClothes()
    }

    override fun keyDown(keyCode: Int): Boolean {
        if (keyCode == Keys.SPACE) {
            if (!gameStarted) {
                startGame()
                return true
            } else if (gameOver && gameOverTime > .5f) { // added a small delay
                println("loading new level")
                BaseGame.setActiveScreen(LevelScreen())
            }

            if (!gameOver) player.jump()
        } else if (keyCode == Keys.BACK || keyCode == Keys.ESCAPE)
            Gdx.app.exit()
        return true
    }

    // for android
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!gameStarted) {
            startGame()
            return true
        } else if (gameOver && gameOverTime > .5f) { // added a small delay
            println("loading new level")
            BaseGame.setActiveScreen(LevelScreen())
        }

        if (!gameOver) player.jump()
        return super.touchDown(screenX, screenY, pointer, button)
    }

    private fun score() = gameTime.toInt() * 100

    private fun gameOver() {
        gameOver = true
        player.moving = false
        ground1.moving = false
        ground2.moving = false
        groundBackup.moving = false
        pile1.moving = false
        pile2.moving = false
        cat.moving = false
        BaseGame.socksFast1Music!!.stop()
        BaseGame.feetFast1Music!!.stop()
        BaseGame.tiptapFeetMusic!!.stop()
        gameOverTable.color.a = 1f
        if (score() > BaseGame.highScore)
            BaseGame.highScore = score()
        GameUtils.saveGameState()
        setActorSpeed(0f)
        player.moving = false
        player.gameOver()
        computer.fadeInAndSounds()
    }

    private fun setActorSpeed(newSpeed: Float) {
        BaseGame.speed = newSpeed
        for (ground: BaseActor in BaseActor.getList(mainStage, Ground::class.java.canonicalName))
            ground.setSpeed(BaseGame.speed)

        for (pile: BaseActor in BaseActor.getList(mainStage, Pile::class.java.canonicalName))
            pile.setSpeed(BaseGame.speed)

        if (gameTime > 30f) {
            cat.setSpeed(-BaseGame.speed)
            cat.active()
        }

        for (item: BaseActor in BaseActor.getList(mainStage, PickUpItem::class.java.canonicalName))
            item.setSpeed(BaseGame.speed)

        bed.setSpeed(BaseGame.speed)
    }

    private fun startGame() {
        println("starting the game!")
        gameOver = false
        gameStarted = true
        setActorSpeed(-BaseGame.speed)
        player.jump(125f)
    }

    private fun createClothes() {
        var yPos = 30f
        if (itemToCreate == "coffee" && gameTime > 30f && player.clothing == "tie") {
            if (BaseActor.count(mainStage, PickUpItem::class.java.canonicalName) == 0) {
                PickUpItem(110f, yPos, mainStage, itemToCreate)
                println("created a $itemToCreate!")
            }
        } else if (itemToCreate == "tie" && gameTime > 30f && player.clothing == "shirt") {
            if (BaseActor.count(mainStage, PickUpItem::class.java.canonicalName) == 0) {
                PickUpItem(110f, yPos, mainStage, itemToCreate)
                println("created a $itemToCreate!")
            }
        } else if (itemToCreate == "shirt" && gameTime > 20f && player.clothing == "sock") {
            if (BaseActor.count(mainStage, PickUpItem::class.java.canonicalName) == 0) {
                PickUpItem(110f, yPos, mainStage, itemToCreate)
                println("created a $itemToCreate!")
            }
        } else if (itemToCreate == "sock" && gameTime > 10f && player.clothing == "none") {
            if (BaseActor.count(mainStage, PickUpItem::class.java.canonicalName) == 0) {
                PickUpItem(110f, yPos, mainStage, itemToCreate)
                println("created a $itemToCreate!")
            }
        }
    }
}
package no.sandramoen.ggj2021oslo.screens

import com.badlogic.gdx.Input
import no.sandramoen.ggj2021oslo.actors.Ground
import no.sandramoen.ggj2021oslo.actors.Pile
import no.sandramoen.ggj2021oslo.actors.Player
import no.sandramoen.ggj2021oslo.utils.BaseActor
import no.sandramoen.ggj2021oslo.utils.BaseGame
import no.sandramoen.ggj2021oslo.utils.BaseScreen

class LevelScreen : BaseScreen() {

    private lateinit var player: Player
    override fun initialize() {
        Ground(0f, 0f, mainStage)
        Ground(100f, 0f, mainStage)

        Pile(100f, 20f, mainStage)

        player = Player(15f, 30f, mainStage)
    }

    override fun update(dt: Float) {
        for (pile: BaseActor in BaseActor.getList(mainStage, Pile::class.java.canonicalName)) {
            if (player.overlaps(pile)) {
                println("! u lost !")
                BaseGame.setActiveScreen(LevelScreen())
            }
        }
    }


    override fun keyDown(keyCode: Int): Boolean {
        if (keyCode == Input.Keys.SPACE) player.jump()
        return true
    }
}
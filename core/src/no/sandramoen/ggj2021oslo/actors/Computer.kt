package no.sandramoen.ggj2021oslo.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.ggj2021oslo.utils.BaseActor
import no.sandramoen.ggj2021oslo.utils.BaseGame

class Computer(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    var activate = false

    init {
        loadImage("computer")
        setSize(25f, 25f)
        color.a = 0f
    }

    fun fadeInAndSounds() {
        if (!activate) {
            activate = true

            addAction(Actions.fadeIn(1f))

            // sounds
            BaseGame.type1Sound!!.play(BaseGame.soundVolume)
            BaseGame.mouseClickSound!!.play(BaseGame.soundVolume)
            addAction(Actions.sequence(
                Actions.delay(1.5f),
                Actions.run {
                    BaseGame.mouseClickSound!!.play(BaseGame.soundVolume)
                    BaseGame.type2Sound!!.play(BaseGame.soundVolume)
                },
                Actions.delay(1.5f),
                Actions.run {
                    BaseGame.mouseClickSound!!.play(BaseGame.soundVolume)
                    BaseGame.type3Sound!!.play(BaseGame.soundVolume)
                }
            ))
        }
    }
}

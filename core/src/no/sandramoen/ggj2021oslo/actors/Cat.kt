package no.sandramoen.ggj2021oslo.actors

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.ggj2021oslo.utils.BaseActor
import no.sandramoen.ggj2021oslo.utils.BaseGame
import kotlin.math.abs

class Cat(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var isOut = false
    private var active = false

    init {
        // animation
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("cat$i"))
        setAnimation(Animation(.25f, animationImages, Animation.PlayMode.LOOP_PINGPONG))
        setSize(10f, 15f)

        // physics
        setSpeed(0f)
        setMotionAngle(180f)
        setSize(7f, 25f)
        setBoundaryRectangle()
    }

    fun act(dt: Float, player: Player) {
        super.act(dt)
        applyPhysics(dt)

        // if moved completely past left edge of screen:
        //   shift right, past other instance.
        if (x + width < 0) {
            moveBy(100f + width + MathUtils.random(0f, 800f), 0f)
        }

        if (abs(player.x - x) <= 50f) moveIn()
        else moveOut()

        // println("x: $x, y: $y, $isOut: $isOut, speed: ${getSpeed()}") // for debugging...
    }

    fun active() {
        if (!active) {
            println("cat activated!")
            active = true
            BaseGame.catPurrSound!!.play(BaseGame.soundVolume * .9f)
        }
    }

    private fun moveIn() {
        if (actions.size == 0 && !isOut) {
            addAction(Actions.moveBy(0f, -6f, 1f))
            isOut = true
        }
    }

    private fun moveOut() {
        if (actions.size == 0 && isOut) {
            addAction(Actions.moveBy(0f, 6f, 1f))
            isOut = false
            when(MathUtils.random(1, 2)) {
                1-> BaseGame.catHappy1Sound!!.play(BaseGame.soundVolume)
                2-> BaseGame.catHappy2Sound!!.play(BaseGame.soundVolume)
            }
        }
    }
}

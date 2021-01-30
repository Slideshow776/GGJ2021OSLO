package no.sandramoen.ggj2021oslo.actors

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.ggj2021oslo.utils.BaseActor

class Pile(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        when (MathUtils.random(1, 2)) {
            1 -> loadImage("pile")
            2 -> loadImage("newspile")
        }
        setSpeed(0f)
        setMotionAngle(180f)
        setSize(8f, 5f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)

        // if moved completely past left edge of screen:
        //   shift right, past other instance.
        if (x + width < 0) {
            moveBy(100f + width + MathUtils.random(-5f, 5f), 0f)
            y = MathUtils.random(15f, 19f)
            setSize(8f, MathUtils.random(7f, 18f))
        }
    }
}
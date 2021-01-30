package no.sandramoen.ggj2021oslo.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.ggj2021oslo.utils.BaseActor

class Bed(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        loadImage("bed")
        setSpeed(0f)
        setMotionAngle(180f)
        setSize(30f, 30f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)

        // if moved completely past left edge of screen:
        //   shift right, past other instance.
        if (x + width < 0) {
            remove()
        }
    }
}

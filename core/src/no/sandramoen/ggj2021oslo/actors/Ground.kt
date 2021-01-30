package no.sandramoen.ggj2021oslo.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.ggj2021oslo.utils.BaseActor
import no.sandramoen.ggj2021oslo.utils.BaseGame

class Ground(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        loadImage("ground")
        setSpeed(BaseGame.speed)
        setMotionAngle(180f)
        setSize(100f, 20f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)

        // if moved completely past left edge of screen:
        //   shift right, past other instance.
        if (x + width < 0) {
            moveBy(2 * width, 0f)
        }
    }
}
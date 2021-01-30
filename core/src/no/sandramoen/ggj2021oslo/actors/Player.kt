package no.sandramoen.ggj2021oslo.actors

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.ggj2021oslo.utils.BaseActor
import no.sandramoen.ggj2021oslo.utils.BaseGame

class Player(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        // animation
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("player$i"))
        setAnimation(Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG))

        setSize(10f, 22f)

        setMaxSpeed(300f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)

        // simulate force of gravity
        setAcceleration(600f)
        accelerateAtAngle(270f)
        applyPhysics(dt)

        // stop player from passing through the ground
        for (g in getList(stage, Ground::class.java.canonicalName)) {
            if (overlaps(g!!)) {
                setSpeed(0f)
                preventOverlap(g!!)
            }
        }
    }

    fun jump() {
        setSpeed(250f)
        setMotionAngle(90f)
    }
}
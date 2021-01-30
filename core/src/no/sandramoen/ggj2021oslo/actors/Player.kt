package no.sandramoen.ggj2021oslo.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.ggj2021oslo.utils.BaseActor
import no.sandramoen.ggj2021oslo.utils.BaseGame
import no.sandramoen.ggj2021oslo.utils.GameUtils

class Player(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {

    private var isJumping = false
    private var itemCount = 0f
    var clothing = "none"

    init {
        // animation
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("player$i"))
        setAnimation(Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG))
        setSize(10f, 22f)

        setMaxSpeed(300f)
        setBoundaryRectangle()
        setOrigin(Align.center)
    }

    override fun act(dt: Float) {
        super.act(dt)

        // simulate force of gravity
        setAcceleration(600f)
        accelerateAtAngle(270f)
        applyPhysics(dt)

        if (y < 18f) {
            isJumping = false
            y = 18f

            if (!BaseGame.feetFast1Music!!.isPlaying && moving)
                GameUtils.setMusicLoopingAndPlay(BaseGame.feetFast1Music) // , volume = BaseGame.soundVolume * .5f)
        }
    }

    fun jump(jumAmount: Float = 250f) {
        if (!isJumping) {
            startSweating()
            BaseGame.feetFast1Music!!.stop()
            BaseGame.tiptapFeetMusic!!.stop()
            BaseGame.socksFast1Music!!.stop()
            isJumping = true
            setSpeed(jumAmount)
            setMotionAngle(90f)
            when(MathUtils.random(1, 3)) {
                1-> BaseGame.jump1Sound!!.play(BaseGame.soundVolume)
                2-> BaseGame.jump2Sound!!.play(BaseGame.soundVolume)
                3-> BaseGame.jump3Sound!!.play(BaseGame.soundVolume)
            }
        }
    }

    fun addItem(item: String) {
        println("player picked up some: $item!")

        if (item == "sock") {
            var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
            for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("player_sock$i"))
            setAnimation(Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG))
            setSize(10f, 22f)
            BaseGame.gettingReadyRush1Sound!!.play(1f) // TODO: low volume audio
            clothing = "sock"
        } else if (item == "shirt") {
            var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
            for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("player_shirt$i"))
            setAnimation(Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG))
            setSize(10f, 22f)
            BaseGame.gettingReadyRush2Sound!!.play(1f) // TODO: low volume audio
            clothing = "shirt"
        } else if (item == "tie") {
            var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
            for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("player_tie$i"))
            setAnimation(Animation(.1f, animationImages, Animation.PlayMode.LOOP_PINGPONG))
            setSize(10f, 22f)
            BaseGame.gettingReadyRush2Sound!!.play(1f) // TODO: low volume audio
            clothing = "tie"
        }
    }

    private fun startSweating() {
        // effects
        val effect = SweatEffect()
        effect.setPosition(width / 2, height * .85f) // by trial and error...
        effect.setScale(Gdx.graphics.height * .00005f)
        this.addActor(effect)
        effect.start()
    }
}
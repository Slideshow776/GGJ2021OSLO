package no.sandramoen.ggj2021oslo.actors

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.ggj2021oslo.utils.BaseActor
import no.sandramoen.ggj2021oslo.utils.BaseGame
import com.badlogic.gdx.math.Vector2

import com.badlogic.gdx.graphics.g2d.Batch

class PickUpItem(x: Float, y: Float, s: Stage, item: String) : BaseActor(x, y, s) {
    var itemName = item

    private var vertexShader: String? = null
    private var fragmentShader: String? = null
    private var shaderProgram: ShaderProgram? = null
    private var time = 0f

    init {
        loadImage(item)
        setSize(11f, 11f)

        // physics
        setSpeed(BaseGame.speed)
        setMotionAngle(180f)
        setBoundaryRectangle()

        // shaders
        vertexShader = BaseGame.defaultShader.toString()
        fragmentShader = BaseGame.glowShader.toString()
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)

        // to detect errors in GPU compilation
        if (!shaderProgram!!.isCompiled) println("Couldn't compile shader: " + shaderProgram!!.log)

        time = 0f
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt;

        applyPhysics(dt)

        // if moved completely past left edge of screen: d
        //   shift right, past other instance.
        if (x + width < 0) {
            moveBy(100f + width + MathUtils.random(-5f, 5f), 0f)
            setSize(8f, MathUtils.random(5f, 15f))
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        try {
            batch.shader = shaderProgram
            shaderProgram!!.setUniformf("u_time", time)
            shaderProgram!!.setUniformf("u_imageSize", Vector2(width, height))
            shaderProgram!!.setUniformi("u_glowRadius", 7)
            super.draw(batch, parentAlpha)
            batch.shader = null
        } catch (error: Error) {
            super.draw(batch, parentAlpha)
        }
    }
}
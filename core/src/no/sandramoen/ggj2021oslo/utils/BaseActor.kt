package no.sandramoen.ggj2021oslo.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.scenes.scene2d.Group
import kotlin.math.abs
import com.badlogic.gdx.math.Intersector

import com.badlogic.gdx.math.Intersector.MinimumTranslationVector

import com.badlogic.gdx.math.Vector2


open class BaseActor(x: Float, y: Float, s: Stage) : Group() {
    private val token = "BaseActor.kt"
    private var animation: Animation<TextureAtlas.AtlasRegion>?
    private var animationTime: Float = 0F
    private var animationPaused: Boolean = false

    private var velocityVec: Vector2 = Vector2(0f, 0f)
    private var accelerationVec: Vector2 = Vector2(0f, 0f)
    private var acceleration: Float = 0f
    private var maxSpeed: Float = 1000f
    private var deceleration: Float = 0f
    private var boundaryPolygon: Polygon? = null
    var animationWidth = width
    private var animationHeight = height

    var isFacingRight = true
    var pause = false
    var moving = false


    init {
        this.x = x
        this.y = y
        s.addActor(this)
        animation = null
        debug = false
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        setAnimationSize(width, height)
    }

    override fun act(dt: Float) {
        // if (!moving) return

        if (!pause)
            super.act(dt)

        if (!animationPaused)
            animationTime += dt
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        //  apply color tint effect
        val c: Color = color
        batch.setColor(c.r, c.g, c.b, c.a)

        if (animation != null && isVisible) {
            if (isFacingRight)
                batch.draw(
                    animation!!.getKeyFrame(animationTime),
                    x - abs(width - animationWidth) / 2,
                    y,
                    originX,
                    originY,
                    animationWidth,
                    animationHeight,
                    scaleX,
                    scaleY,
                    rotation
                )
            else
                batch.draw(
                    animation!!.getKeyFrame(animationTime),
                    x + width + abs(width - animationWidth) / 2,
                    y,
                    originX,
                    originY,
                    -animationWidth,
                    animationHeight,
                    scaleX,
                    scaleY,
                    rotation
                )
        }
        super.draw(batch, parentAlpha)
    }

    // Graphics ---------------------------------------------------------------------------------------------------
    fun setAnimation(anim: Animation<TextureAtlas.AtlasRegion>) {
        animation = anim
        animationTime = 0f
        val tr: TextureRegion = animation!!.getKeyFrame(0.toFloat())
        val w: Float = tr.regionWidth.toFloat()
        val h: Float = tr.regionHeight.toFloat()
        setSize(w, h)
        setAnimationSize(w, h)
        setOrigin(w / 2, h / 2)

        if (boundaryPolygon == null)
            setBoundaryRectangle()
    }

    fun setAnimationSize(width: Float, height: Float) {
        animationWidth = width
        animationHeight = height
    }

    fun flip() {
        isFacingRight = !isFacingRight
    }

    fun setAnimationPaused(pause: Boolean) {
        animationPaused = pause
    }

    fun loadTexture(fileName: String): Animation<TextureRegion> {
        val fileNames: Array<String> = Array(1)
        fileNames.add(fileName)
        return loadAnimationFromFiles(fileNames, 1f, true)
    }

    private fun loadAnimationFromFiles(
        fileNames: Array<String>, frameDuration: Float, loop: Boolean,
        textureFilter: TextureFilter = TextureFilter.Linear
    ): Animation<TextureRegion> {  // TODO: might not need this
        val textureArray: Array<TextureRegion> = Array()

        for (i in 0 until fileNames.size) {
            val texture = Texture(Gdx.files.internal(fileNames[i]))
            texture.setFilter(textureFilter, textureFilter)
            textureArray.add(TextureRegion(texture))
        }

        val anim: Animation<TextureRegion> = Animation(frameDuration, textureArray)

        if (loop)
            anim.playMode = Animation.PlayMode.LOOP
        else
            anim.playMode = Animation.PlayMode.NORMAL

        if (animation == null)
            setAnimation(anim as Animation<TextureAtlas.AtlasRegion>)

        return anim
    }

    fun loadImage(name: String) {
        val region = BaseGame.textureAtlas!!.findRegion(name)
        setAnimation(Animation(1f, region))
    }

    /*fun loadAnimation(region: Array<TextureAtlas.AtlasRegion>, frameDuration: Float, loop: Boolean) {
        setAnimation(Animation(frameDuration, region), loop)
    }*/

    fun isAnimationFinished(): Boolean {
        return animation!!.isAnimationFinished(animationTime)
    }

    // Physics ---------------------------------------------------------------------------------------------------
    fun setSpeed(speed: Float) {
        // If length is zero, then assume motion angle is zero degrees
        if (velocityVec.len() == 0f)
            velocityVec.set(speed, 0f)
        else
            velocityVec.setLength(speed)
    }

    fun getSpeed() = velocityVec.len()
    fun setMotionAngle(angle: Float) {
        velocityVec.setAngle(angle)
    }

    fun getMotionAngle() = velocityVec.angle()
    fun isMoving() = getSpeed() > 0
    fun setAcceleration(acc: Float) {
        acceleration = acc
    }

    fun accelerateAtAngle(angle: Float) = accelerationVec.add(Vector2(acceleration, 0f).setAngle(angle))
    fun accelerateForward() = accelerateAtAngle(rotation)
    fun setMaxSpeed(ms: Float) {
        maxSpeed = ms
    }

    fun setDeceleration(dec: Float) {
        deceleration = dec
    }

    fun applyPhysics(dt: Float) {
        // apply acceleration
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt)

        var speed = getSpeed()

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0f)
            speed -= deceleration * dt

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0f, maxSpeed)

        // update velocity
        setSpeed(speed)

        // apply velocity
        moveBy(velocityVec.x * dt, velocityVec.y * dt)

        // reset acceleration
        accelerationVec.set(0f, 0f)
    }

    // camera ---------------------------------------------------------------------------------------------------
    fun alignCamera(target: Vector2 = Vector2(x, y), lerp: Float = 1f) {
        if (this.stage != null) {
            val camera = this.stage.camera

            // center camera on actor
            val position = camera.position
            position.x = camera.position.x + (target.x + width / 2 - camera.position.x) * lerp
            // position.y = camera.position.y + (target.y + height / 2 - camera.position.y) * lerp
            camera.position.set(position)

            camera.update()
        }
    }

    // Collision detection --------------------------------------------------------------------------------------
    fun setBoundaryRectangle() {
        val w: Float = width
        val h: Float = height
        val vertices: FloatArray = floatArrayOf(0f, 0f, w, 0f, w, h, 0f, h)
        boundaryPolygon = Polygon(vertices)
    }

    open fun preventOverlap(other: BaseActor): Vector2? {
        val poly1 = getBoundaryPolygon()
        val poly2 = other.getBoundaryPolygon()

        // initial test to improve performance
        if (!poly1.boundingRectangle.overlaps(poly2.boundingRectangle)) return null
        val mtv = MinimumTranslationVector()
        val polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv)
        if (!polygonOverlap) return null
        moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth)
        return mtv.normal
    }

    fun setBoundaryPolygon(numSides: Int) {
        val w: Float = width
        val h: Float = height

        val vertices = FloatArray(2 * numSides)
        for (i in 0 until numSides) {
            val angle: Float = i * MathUtils.PI2 / numSides
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2    // x-coordinates
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2  // y-coordinates
        }
        boundaryPolygon = Polygon(vertices)
    }

    fun getBoundaryPolygon(): Polygon {
        boundaryPolygon!!.setPosition(x, y)
        boundaryPolygon!!.setOrigin(originX, originY)
        boundaryPolygon!!.rotation = rotation
        boundaryPolygon!!.setScale(scaleX, scaleY)
        return boundaryPolygon as Polygon
    }

    fun overlaps(other: BaseActor): Boolean {
        val poly1: Polygon = this.getBoundaryPolygon()
        val poly2: Polygon = other.getBoundaryPolygon()

        // initial test to improve performance
        if (!poly1.boundingRectangle.overlaps(poly2.boundingRectangle))
            return false
        return Intersector.overlapConvexPolygons(poly1, poly2)
    }

    // world ---------------------------------------------------------------------------------------------------

    // stores size of game world for all actors
    private var worldBounds: Rectangle? = null


    open fun setWorldBounds(width: Float, height: Float) {
        worldBounds = Rectangle(0f, 0f, width, height)
    }

    fun boundToWorld() {
        if (x < 0) x = 0f
        if (x + getWidth() > worldBounds!!.width) setX(worldBounds!!.width - getWidth())
        if (getY() < 0) setY(0f)
        if (getY() + getHeight() > worldBounds!!.height) setY(worldBounds!!.height - getHeight())
    }

    open fun setWorldBounds(ba: BaseActor) {
        setWorldBounds(ba.width, ba.height)
    }

    open fun getWorldBounds(): Rectangle? {
        return worldBounds
    }

    // miscellaneous -------------------------------------------------------------------------------------------
    fun centerAtPosition(x: Float, y: Float) = setPosition(x - width / 2, y - height / 2)
    fun centerAtActor(other: BaseActor) = centerAtPosition(other.x + other.width / 2, other.y + other.height / 2)
    fun setOpacity(opacity: Float) {
        this.color.a = opacity
    }

    companion object {
        fun getList(stage: Stage, className: String): ArrayList<BaseActor> {
            var list: ArrayList<BaseActor> = ArrayList()

            var theClass: Class<*>? = null
            try {
                theClass = Class.forName(className)
            } catch (error: Exception) {
                error.printStackTrace()
            }

            for (actor in stage.actors) {
                if (theClass!!.isInstance(actor)) {
                    list.add(actor as BaseActor)
                }
            }
            return list
        }

        fun count(stage: Stage, className: String): Int {
            return getList(stage, className).size
        }
    }
}
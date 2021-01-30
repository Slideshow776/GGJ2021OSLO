package no.sandramoen.ggj2021oslo.utils

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

abstract class BaseGame() : Game(), AssetErrorListener {
    private val token = "BaseGame.kt"

    init {
        game = this
    }

    companion object {
        private var game: BaseGame? = null

        lateinit var assetManager: AssetManager
        lateinit var fontGenerator: FreeTypeFontGenerator
        const val WORLD_WIDTH = 100f
        const val WORLD_HEIGHT = 100f
        const val scale = 1.5f

        // game assets
        var labelStyle: LabelStyle? = null
        var textButtonStyle: TextButtonStyle? = null
        var textureAtlas: TextureAtlas? = null
        var skin: Skin? = null
        var alarm1Sound: Sound? = null
        var jump1Sound: Sound? = null
        var jump2Sound: Sound? = null
        var jump3Sound: Sound? = null
        var gettingReadyRush1Sound: Sound? = null
        var gettingReadyRush2Sound: Sound? = null
        var gettingReadyRush3Sound: Sound? = null
        var yawnSound: Sound? = null
        var mouseClickSound: Sound? = null
        var type1Sound: Sound? = null
        var type2Sound: Sound? = null
        var type3Sound: Sound? = null
        var feetFast1Music: Music? = null
        var socksFast1Music: Music? = null
        var tiptapFeetMusic: Music? = null
        var levelMusic1: Music? = null

        // game state
        var prefs: Preferences? = null
        var highScore: Int = 0
        var speed = 30f
        var soundVolume = .5f
        var musicVolume = 1f

        fun setActiveScreen(s: BaseScreen) {
            game?.setScreen(s)
        }
    }

    override fun create() {
        Gdx.input.inputProcessor = InputMultiplexer() // discrete input

        // global variables
        GameUtils.loadGameState()

        // asset manager
        assetManager = AssetManager()
        assetManager.setErrorListener(this)
        assetManager.load("images/included/packed/ggj2021oslo.pack.atlas", TextureAtlas::class.java)
        assetManager.load("audio/sound/Alarm1.mp3", Sound::class.java)
        assetManager.load("audio/sound/Jump_Grunt1.mp3", Sound::class.java)
        assetManager.load("audio/sound/Jump_Grunt2.mp3", Sound::class.java)
        assetManager.load("audio/sound/Jump_Grunt3.wav", Sound::class.java)
        assetManager.load("audio/sound/Alarm1.mp3", Sound::class.java)
        assetManager.load("audio/sound/Getting_Ready_Rush1.mp3", Sound::class.java)
        assetManager.load("audio/sound/Getting_Ready_Rush2.mp3", Sound::class.java)
        assetManager.load("audio/sound/Getting_Ready_Rush3.mp3", Sound::class.java)
        assetManager.load("audio/sound/Yawn_1.wav", Sound::class.java)
        assetManager.load("audio/sound/Mouse-Click.wav", Sound::class.java)
        assetManager.load("audio/sound/Type_1.wav", Sound::class.java)
        assetManager.load("audio/sound/Type_2.wav", Sound::class.java)
        assetManager.load("audio/sound/Type_3.wav", Sound::class.java)
        assetManager.load("audio/sound/tiptap_feet_1.wav", Music::class.java)
        assetManager.load("audio/music/FeetFast1.wav", Music::class.java)
        assetManager.load("audio/music/SocksFast1.wav", Music::class.java)
        assetManager.load("audio/music/Wheres_my_video_call_Music_test1.mp3", Music::class.java)

        assetManager.load("skins/default/uiskin.json", Skin::class.java)

        val resolver = InternalFileHandleResolver()
        assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
        assetManager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))
/*
        assetManager.load(AssetDescriptor("shaders/default.vs", Text::class.java, TextLoader.TextParameter()))
        assetManager.load(AssetDescriptor("shaders/shockwave.fs", Text::class.java, TextLoader.TextParameter()))*/
        assetManager.finishLoading()

        textureAtlas =
            assetManager.get("images/included/packed/ggj2021oslo.pack.atlas") // all images are found in this global static variable

        // audio
        alarm1Sound = assetManager.get("audio/sound/Alarm1.mp3", Sound::class.java)
        jump1Sound = assetManager.get("audio/sound/Jump_Grunt1.mp3", Sound::class.java)
        jump2Sound = assetManager.get("audio/sound/Jump_Grunt2.mp3", Sound::class.java)
        jump3Sound = assetManager.get("audio/sound/Jump_Grunt3.wav", Sound::class.java)
        gettingReadyRush1Sound = assetManager.get("audio/sound/Getting_Ready_Rush1.mp3", Sound::class.java)
        gettingReadyRush2Sound = assetManager.get("audio/sound/Getting_Ready_Rush2.mp3", Sound::class.java)
        gettingReadyRush3Sound = assetManager.get("audio/sound/Getting_Ready_Rush3.mp3", Sound::class.java)
        yawnSound = assetManager.get("audio/sound/Yawn_1.wav", Sound::class.java)
        mouseClickSound = assetManager.get("audio/sound/Mouse-Click.wav", Sound::class.java)
        type1Sound = assetManager.get("audio/sound/Type_1.wav", Sound::class.java)
        type2Sound = assetManager.get("audio/sound/Type_2.wav", Sound::class.java)
        type3Sound = assetManager.get("audio/sound/Type_3.wav", Sound::class.java)
        tiptapFeetMusic = assetManager.get("audio/sound/tiptap_feet_1.wav", Music::class.java)
        feetFast1Music = assetManager.get("audio/music/FeetFast1.wav", Music::class.java)
        socksFast1Music = assetManager.get("audio/music/SocksFast1.wav", Music::class.java)
        levelMusic1 = assetManager.get("audio/music/Wheres_my_video_call_Music_test1.mp3", Music::class.java)

        // text files
        /*defaultShader = assetManager.get("shaders/default.vs", Text::class.java).getString()
        shockwaveShader = assetManager.get("shaders/shockwave.fs", Text::class.java).getString()*/

        // skins
        skin = Skin(Gdx.files.internal("skins/default/uiskin.json"))

        // fonts
        FreeTypeFontGenerator.setMaxTextureSize(2048) // solves font bug that won't show some characters like "." and "," in android
        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/arcade.ttf"))
        val fontParameters = FreeTypeFontParameter()
        fontParameters.size = (.038f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
        fontParameters.color = Color.WHITE
        fontParameters.borderWidth = 2f
        fontParameters.borderColor = Color.BLACK
        fontParameters.borderStraight = true
        fontParameters.minFilter = TextureFilter.Linear
        fontParameters.magFilter = TextureFilter.Linear
        val customFont = fontGenerator.generateFont(fontParameters)

        val buttonFontParameters = FreeTypeFontParameter()
        buttonFontParameters.size =
            (.04f * Gdx.graphics.height).toInt() // If the resolutions height is 1440 then the font size becomes 86
        buttonFontParameters.color = Color.WHITE
        buttonFontParameters.borderWidth = 2f
        buttonFontParameters.borderColor = Color.BLACK
        buttonFontParameters.borderStraight = true
        buttonFontParameters.minFilter = TextureFilter.Linear
        buttonFontParameters.magFilter = TextureFilter.Linear
        val buttonCustomFont = fontGenerator.generateFont(buttonFontParameters)

        labelStyle = LabelStyle()
        labelStyle!!.font = customFont

        textButtonStyle = TextButtonStyle()
        val buttonTexUp = textureAtlas!!.findRegion("button")
        val buttonTexDown = textureAtlas!!.findRegion("button-pressed")
        val buttonPatchUp = NinePatch(buttonTexUp, 24, 24, 24, 24)
        val buttonPatchDown = NinePatch(buttonTexDown, 24, 24, 24, 24)
        textButtonStyle!!.up = NinePatchDrawable(buttonPatchUp)
        textButtonStyle!!.down = NinePatchDrawable(buttonPatchDown)
        textButtonStyle!!.font = buttonCustomFont
        textButtonStyle!!.fontColor = Color.WHITE

        // other
        GameUtils.setMusicLoopingAndPlay(levelMusic1)
    }

    override fun dispose() {
        super.dispose()

        assetManager.dispose()
        fontGenerator.dispose()
        /*try { // TODO: uncomment this when development is done
            assetManager.dispose()
            fontGenerator.dispose()
        } catch (error: UninitializedPropertyAccessException) {
            Gdx.app.error("BaseGame", "Error $error")
        }*/
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        Gdx.app.error("BaseGame.kt", "Could not load asset: " + asset.fileName, throwable)
    }
}

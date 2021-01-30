package no.sandramoen.ggj2021oslo

import no.sandramoen.ggj2021oslo.screens.LevelScreen
import no.sandramoen.ggj2021oslo.utils.BaseGame

class MyGdxGame() : BaseGame() {

    override fun create() {
        super.create()
        setActiveScreen(LevelScreen())
    }
}

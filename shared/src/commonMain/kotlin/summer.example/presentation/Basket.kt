package summer.example.presentation

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kodein.di.erased.instance
import summer.example.domain.basket.BasketController
import summer.example.entity.Basket
import summer.example.presentation.base.BasePresenter

interface BasketView {
    var items: List<Basket.Item>
}

class BasketPresenter : BasePresenter<BasketView>() {
    private val basketController: BasketController by instance()

    override val viewProxy = object : BasketView {
        override var items by state({ it::items }, initial = emptyList())
    }

    init {
        basketController.flow.onEach { basket ->
            viewProxy.items = basket.items
        }.launchIn(this)
    }
}
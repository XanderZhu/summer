package summer.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import summer.SummerPresenter

abstract class SummerActivity<
        TViewState : Any,
        TViewMethods : Any,
        TRouter : Any,
        TPresenter : SummerPresenter<TViewState, TViewMethods, TRouter>> : AppCompatActivity() {

    abstract val router: TRouter
    abstract val viewState: TViewState
    abstract val viewMethods: TViewMethods

    abstract fun createPresenter(): TPresenter

    lateinit var presenter: TPresenter

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
        presenter.beforeCreateView(owner = this)
        initView()
        presenter.onCreateView(viewState, viewMethods, router, owner = this)
    }

    abstract fun initView()

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroyView()
        presenter.onDestroyOwner()
    }

    override fun onResume() {
        super.onResume()
        presenter.onAppear()
    }

    override fun onPause() {
        super.onPause()
        presenter.onDisappear()
    }
}
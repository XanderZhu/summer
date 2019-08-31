package summer.android

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import summer.SummerPresenter
import java.util.Collections.emptyList

abstract class SummerFragment<
        TViewState : Any,
        TViewMethods : Any,
        TRouter : Any,
        TPresenter : SummerPresenter<TViewState, TViewMethods, TRouter>> : androidx.fragment.app.Fragment() {

    protected abstract val router: TRouter
    protected abstract val viewMethods: TViewMethods

    protected abstract fun createViewState(): TViewState

    private var _presenter: TPresenter? = null
    protected val presenter: TPresenter get() = _presenter!!

    protected abstract fun createPresenter(): TPresenter

    protected abstract fun initView()

    protected var viewState: TViewState? = null

    protected open fun createComponents(): List<SummerComponent<*, *, *, *>> = emptyList()
    private var lifecycleComponents: List<SummerComponent<*, *, *, *>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _presenter = createPresenter()
        lifecycleComponents = createComponents()
        _presenter!!.onCreate()
        lifecycleComponents.forEach { it.onCreate() }
    }

    private var isFirstViewCreation = true

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewState = createViewState()
        _presenter!!.onCreateView(viewState!!, viewMethods, router)
        if (isFirstViewCreation) {
            _presenter!!.afterCreate()
        }
        isFirstViewCreation = false

        lifecycleComponents.forEach { component ->
            component.onViewCreated(
                parentView = view as? ViewGroup,
                context = context!!,
                isFirstViewCreation = isFirstViewCreation
            )
        }
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleComponents.forEach { it.onDestroyView() }
        lifecycleComponents = emptyList()
        _presenter!!.onDestroyView()
        viewState = null
//        _presenter = null // если раскомментировать, то не получится получить доступ к onDestroyOwner() в onDestroy
    }

    @CallSuper
    override fun onDestroy() {
        notifyPresenterIfRemoving()
        super.onDestroy()
        lifecycleComponents.forEach { it.onDestroy() }
        presenter.onDestroy()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        presenter.onAppear()
        lifecycleComponents.forEach { it.onResume() }
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        lifecycleComponents.forEach { it.onPause() }
        presenter.onDisappear()
    }

    private fun notifyPresenterIfRemoving() {
        var anyParentIsRemoving = false

        var parent = parentFragment
        while (!anyParentIsRemoving && parent != null) {
            anyParentIsRemoving = parent.isRemoving
            parent = parent.parentFragment
        }

        if (isRemoving || anyParentIsRemoving) presenter.onExit()
    }
}

abstract class ArgsSummerFragment<
        TViewState : Any,
        TViewMethods : Any,
        TRouter : Any,
        TPresenter : SummerPresenter<TViewState, TViewMethods, TRouter>,
        TArgs> : SummerFragment<
        TViewState,
        TViewMethods,
        TRouter,
        TPresenter>(), ArgsFragmentFeature<TArgs> {

    @Suppress("LeakingThis")
    override val fragment = this

    override var argsBackingField: TArgs? = null
}
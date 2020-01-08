/*package view

import dev.icerock.moko.fields.FormField
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.widgets.*
import dev.icerock.moko.widgets.core.Image
import dev.icerock.moko.widgets.core.Theme
import dev.icerock.moko.widgets.factory.*
import dev.icerock.moko.widgets.screen.*
import dev.icerock.moko.widgets.style.background.*
import dev.icerock.moko.widgets.style.view.*
import dev.icerock.moko.widgets.utils.asLiveData
import dev.icerock.moko.widgets.utils.platformSpecific
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class App : BaseApplication() {
    override fun setup() {
        val theme = Theme()

        registerScreenFactory(LoginScreen::class) { LoginScreen(theme) {TimerViewModel()}}
    }

    override fun getRootScreen(): KClass<out Screen<Args.Empty>> {
        return LoginScreen::class
    }
}

@UseExperimental(InternalCoroutinesApi::class)
class TimerViewModel : ViewModel() {
    private val iteration = MutableLiveData(0)
    val emailField = FormField<String,StringDesc>("")  {it.value.desc().asLiveData()}
    val passwordField = FormField<String,StringDesc>("")  {it.value.desc().asLiveData()}

}

class LoginScreen(
    private val theme: Theme,
    private val loginViewModelFactory: () -> TimerViewModel
) : WidgetScreen<Args.Empty>() {

    override fun createContentWidget() = with(theme) {
        val viewModel = getViewModel(loginViewModelFactory)

        constraint(size = WidgetSize.AsParent) {
            val emailInput = +input(
                size = WidgetSize.WidthAsParentHeightWrapContent,
                id = Id.EmailInputId,
                label = const("Email".desc() as StringDesc),
                field = viewModel.emailField
            )
            val passwordInput = +input(
                size = WidgetSize.WidthAsParentHeightWrapContent,
                id = Id.PasswordInputId,
                label = const("Password".desc() as StringDesc),
                field = viewModel.passwordField
            )
            /*val loginButton = +button(
                size = WidgetSize.Const(SizeSpec.AsParent, SizeSpec.Exact(50f)),
                text = const("Login".desc() as StringDesc),
                onTap = viewModel::onLoginPressed
            )

            val registerButton = +button(
                id = Id.RegistrationButtonId,
                size = WidgetSize.Const(SizeSpec.WrapContent, SizeSpec.Exact(40f)),
                text = const("Registration".desc() as StringDesc),
                onTap = viewModel::onRegistrationPressed
            )*/

            constraints {
                passwordInput centerYToCenterY root
                passwordInput leftRightToLeftRight root

                emailInput bottomToTop passwordInput
                emailInput leftRightToLeftRight root

                //loginButton topToBottom passwordInput
                //loginButton leftRightToLeftRight root

                //registerButton topToBottom loginButton
                //registerButton rightToRight root

                // logo image height must be automatic ?
                //logoImage centerXToCenterX root
                /*logoImage.verticalCenterBetween(
                    top = root.top,
                    bottom = emailInput.top
                )*/
            }
        }
    }

    object Id {
        object EmailInputId : InputWidget.Id
        object PasswordInputId : InputWidget.Id
        object RegistrationButtonId : ButtonWidget.Id
    }
}



val loginScreen = Theme {
    constraintFactory = DefaultConstraintWidgetViewFactory(
        DefaultConstraintWidgetViewFactoryBase.Style(
            padding = PaddingValues(16f),
            background = Background(
                fill = Fill.Solid(Colors.white)
            )
        )
    )

    imageFactory = DefaultImageWidgetViewFactory(
        DefaultImageWidgetViewFactoryBase.Style(
            scaleType = DefaultImageWidgetViewFactoryBase.ScaleType.FIT
        )
    )

    inputFactory = DefaultInputWidgetViewFactory(
        DefaultInputWidgetViewFactoryBase.Style(
            margins = MarginValues(bottom = 8f),
            underLineColor = Color(0xe5e6eeFF),
            labelTextStyle = TextStyle(
                color = Color(0x777889FF)
            )
        )
    )

    val corners = platformSpecific(android = 8f, ios = 25f)

    buttonFactory = DefaultButtonWidgetViewFactory(
        DefaultButtonWidgetViewFactoryBase.Style(
            margins = MarginValues(top = 32f),
            background = {
                val bg: (Color) -> Background = {
                    Background(
                        fill = Fill.Solid(it),
                        shape = Shape.Rectangle(
                            cornerRadius = corners
                        )
                    )
                }
                StateBackground(
                    normal = bg(Color(0x6770e0FF)),
                    pressed = bg(Color(0x6770e0EE)),
                    disabled = bg(Color(0x6770e0BB))
                )
            }.invoke(),
            textStyle = TextStyle(
                color = Colors.white
            )
        )
    )

    setButtonFactory(
        DefaultButtonWidgetViewFactory(
            DefaultButtonWidgetViewFactoryBase.Style(
                margins = MarginValues(top = 16f),
                padding = platformSpecific(
                    ios = PaddingValues(start = 16f, end = 16f),
                    android = null
                ),
                background = {
                    val bg: (Color) -> Background = {
                        Background(
                            fill = Fill.Solid(it),
                            border = Border(
                                color = Color(0xF2F2F8FF),
                                width = 2f
                            ),
                            shape = Shape.Rectangle(cornerRadius = corners)
                        )
                    }
                    StateBackground(
                        normal = bg(Colors.white),
                        pressed = bg(Color(0xEEEEEEFF)),
                        disabled = bg(Color(0xBBBBBBFF))
                    )
                }.invoke(),
                textStyle = TextStyle(
                    color = Color(0x777889FF)
                )
            )
        ),
        LoginScreen.Id.RegistrationButtonId
    )
}*/
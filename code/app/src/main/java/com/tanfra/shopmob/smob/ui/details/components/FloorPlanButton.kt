package com.tanfra.shopmob.smob.ui.details.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.tanfra.shopmob.R
import timber.log.Timber
import kotlin.properties.Delegates

// idea and general handling adapted from Kotlin/Android courses
class FloorPlanButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // custom attributes - set in init
    private var btnDefaultColor = 0
    private var btnDefaultTitle = ""
    private var btnAlternativeColor = 0
    private var btnAlternativeTitle = ""
    private var btnProgressCircleColor = 0

    // button width / height
    private var widthSize = 0
    private var heightSize = 0

    // FloorPlan animation done via second rectangle
    private var percFloorPlanBar: Float = 0.0f
    private var btnTitle: String = ""

    // activate / block the button animation from outside
    private var btnActive = false

    // setter function for button height
    fun setHeight(height: Int) {
        heightSize = height
    }

    // setter function for "active state of the button" (to activate programmatically)
    fun setActive(state: Boolean) {
        btnActive = state
    }

    // setter function for the floor plan button state (Animated or Stopped)
    fun setState(state: ButtonState) {
        buttonState = state
    }

    // initialize some basic properties to avoid having to do so in onDraw
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        // typeface = Typeface.create("", Typeface.BOLD)
    }

    // simple animations can be done using the ValueAnimator class
    // see: https://developer.android.com/guide/topics/graphics/prop-animation#value-animator
    private val valueAnimator = ValueAnimator.ofFloat(1f, 0f).apply {

        // force 'emulated' progress to start over, in case we've reached 100 and the download
        // hasn't finished (see answer to https://knowledge.udacity.com/questions/489831)
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART

        // nominal time to cycle through the entire range (1.0 --> 0.0)
        duration = 1500
    }

    // ... 'by Delegates.observer()'
    // --> provided lambda is called everytime buttonState is changed
    // --> see: https://kotlinlang.org/docs/delegated-properties.html#delegating-to-another-property
    private var buttonState: ButtonState by Delegates
        .observable(ButtonState.Active) { _, old, new ->

            Timber.i("buttonState observer: state changed from $old to $new")

            // animation state machine
            when (new) {

                is ButtonState.Active -> {
                    // register update listener
                    valueAnimator.addUpdateListener {
                        percFloorPlanBar= it.animatedValue as Float
                        invalidate()
                    }

                    // start valueAnimator
                    valueAnimator.start()

                    // and immediately transition to next state
                    buttonState = buttonState.next()

                }  // ButtonState.Active

                is ButtonState.Running -> {
                    // switch button title
                    btnTitle = context.getString(R.string.smob_floorplan)

                    // update view
                    invalidate()

                }  // ButtonState.Running

                is ButtonState.Stopped -> {
                    // stop valueAnimator
                    valueAnimator.removeAllListeners()
                    valueAnimator.cancel()

                }  // ButtonState.Stopped

            }  // when (new)

        }  // observer for ButtonState


    // dynamic initializations
    init {

//        .height(20.dp)
//            .padding(horizontal = 16.dp)
//            .background(MaterialTheme.colorScheme.secondary),
        // get custom attributes
        context.withStyledAttributes(attrs, R.styleable.FloorPlanButton) {
            btnDefaultColor = getColor(R.styleable.FloorPlanButton_defaultColor, Color.BLUE)
            btnDefaultTitle = getString(R.styleable.FloorPlanButton_defaultTitle) ?: "ShopMob"
            btnAlternativeColor = getColor(R.styleable.FloorPlanButton_alternativeColor, Color.LTGRAY)
            btnAlternativeTitle = getString(R.styleable.FloorPlanButton_alternativeTitle) ?: "Smobbing it..."
            btnProgressCircleColor = getColor(R.styleable.FloorPlanButton_progressCircleColor, Color.YELLOW)
        }

        // starting with btnTitle set to btnDefaultTitle
        btnTitle = btnDefaultTitle

        // activate performClick ... plus the onClickListeners
        isClickable = true

    }  // init { ... }


    // generally, user interactions for CustomViews are controlled by performClick
    // ... leaving "onClickListener" free for "use case dependent" actions (--> here: download() )
    override fun performClick(): Boolean {
        //if (super.performClick()) return true
        super.performClick()

        // only allow the floor plan animation to run when it has been activated
        // (= pre-conditions have been met - this is controlled from the outside through a setter)
        if(btnActive && buttonState == ButtonState.Stopped) {
            // adjust state when button is clicked and in state 'Completed' (no re-triggering)
            buttonState = buttonState.next()
        }

        return true
    }


    // screen rotated, etc. --> sizes may change
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // adjust size values of the custom view element
        widthSize = w
        heightSize = h

    }

    // draw da custom button
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // compute these only once
        val heightButton = heightSize.toFloat()
        val widthButton = widthSize.toFloat()
        val widthFloorPlanBar = percFloorPlanBar * widthButton
        val widthFloorPlanBarInv = (1.0f - percFloorPlanBar) * widthButton

        // draw 'FloorPlan' rectangle
        paint.color = btnAlternativeColor
        canvas.drawRect(0.0f, 0.0f, widthFloorPlanBarInv, heightButton, paint)
        canvas.drawRect(0.0f, 0.0f, widthFloorPlanBar, heightButton, paint)

        // draw rectangle with full width of the button and primary color
        paint.color = btnDefaultColor
        canvas.drawRect(widthFloorPlanBar, 0.0f, widthFloorPlanBarInv, heightButton, paint)
        canvas.drawRect(widthFloorPlanBar, 0.0f, widthFloorPlanBar, heightButton, paint)

        // display title (as per layout xml - custom attribute 'defaultTitle')
        paint.color = Color.WHITE
        canvas.drawText(btnTitle, (widthSize/2).toFloat(), (heightSize/2 + 20).toFloat(), paint)

    }

    // get actual size values (depends on rotation, device, etc.)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}
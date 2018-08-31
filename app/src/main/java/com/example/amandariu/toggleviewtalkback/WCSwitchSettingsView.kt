package com.example.amandariu.toggleviewtalkback

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_switch_setting.view.*

class SwitchSettingView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null)
    : LinearLayout(ctx, attrs), Checkable {
    init {
        orientation = LinearLayout.VERTICAL
        View.inflate(context, R.layout.view_switch_setting, this)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SwitchSettingView)
            try {
                val title = a.getString(R.styleable.SwitchSettingView_switchTitle).orEmpty()
                switchSetting_title.text = title
                switchSetting_switch.text =
                        a.getString(R.styleable.SwitchSettingView_switchSummary).orEmpty()
                switchSetting_switch.isChecked =
                        a.getBoolean(R.styleable.SwitchSettingView_switchChecked, false)

                switchSetting_switch.textOff = resources.getText(R.string.switch_disabled)
                switchSetting_switch.textOn = resources.getText(R.string.switch_enabled)
                setOnClickListener { toggle() }
            } finally {
                a.recycle()
            }
        }
    }

    private val checkable: CompoundButton by lazy {
        switchSetting_switch
    }

    private var listener: CompoundButton.OnCheckedChangeListener? = null

    override fun isChecked() = checkable.isChecked

    override fun toggle() {
        checkable.toggle()
        onCheckChanged()
    }

    override fun setChecked(checked: Boolean) {
        if (checkable.isChecked != checked) {
            checkable.isChecked = checked
            listener?.onCheckedChanged(checkable, isChecked)
        }
    }

    private fun onCheckChanged() {
        listener?.onCheckedChanged(checkable, isChecked)
//        sendAccessibilityEvent(AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED)
    }

    /**
     * Allows for sending in lambdas in place of a listener object.
     */
    fun setOnCheckedChangeListener(onCheckedChangeListener: ((CompoundButton, Boolean) -> Unit)?) {
        listener = onCheckedChangeListener?.let {
            OnCheckedChangeListener { buttonView, isChecked ->
                onCheckedChangeListener(buttonView!!, isChecked)
            }
        }
    }


    //region Customized Accessibility events
    override fun getAccessibilityClassName() = this::javaClass.name

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent?) {
        println("AMANDA-TEST > onInitializeAccessibilityEvent > $event")

        event?.isChecked = isChecked
        super.onInitializeAccessibilityEvent(event)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo?) {
        println("AMANDA-TEST > onInitializeAccessibilityNodeInfo > $info")

        info?.isCheckable = true
        info?.isChecked = isChecked
        super.onInitializeAccessibilityNodeInfo(info)
    }
    //endregion


    //region Other Accessibility events
    override fun sendAccessibilityEvent(eventType: Int) {
        println("AMANDA-TEST > sendAccessibilityEvent" +
                " ${AccessibilityEvent.eventTypeToString(eventType)}")
        super.sendAccessibilityEvent(eventType)
    }


    override fun onRequestSendAccessibilityEvent(
            child: View?, event: AccessibilityEvent?): Boolean {
        println("AMANDA-TEST > $event")
        return super.onRequestSendAccessibilityEvent(child, event)
    }
    //endregion
}


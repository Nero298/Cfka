package com.zodiactap.mapper.base.actions

import android.view.KeyEvent
import com.zodiactap.mapper.base.constraints.ConfigConstraintsUseCase
import com.zodiactap.mapper.base.constraints.ConstraintData
import com.zodiactap.mapper.base.keymaps.ClickType
import com.zodiactap.mapper.base.keymaps.ConfigKeyMapStateImpl
import com.zodiactap.mapper.base.keymaps.KeyMap
import com.zodiactap.mapper.base.trigger.KeyEventTriggerDevice
import com.zodiactap.mapper.base.trigger.KeyEventTriggerKey
import com.zodiactap.mapper.base.utils.singleKeyTrigger
import com.zodiactap.mapper.base.utils.triggerKey
import com.zodiactap.mapper.common.utils.dataOrNull
import com.zodiactap.mapper.system.inputevents.KeyEventUtils
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ConfigActionsUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var useCase: ConfigActionsUseCaseImpl
    private lateinit var configKeyMapState: ConfigKeyMapStateImpl
    private lateinit var mockConfigConstraintsUseCase: ConfigConstraintsUseCase

    @Before
    fun before() {
        configKeyMapState = ConfigKeyMapStateImpl(
            testScope,
            keyMapRepository = mock(),
            floatingButtonRepository = mock(),
        )

        mockConfigConstraintsUseCase = mock()

        useCase = ConfigActionsUseCaseImpl(
            state = configKeyMapState,
            preferenceRepository = mock(),
            configConstraints = mockConfigConstraintsUseCase,
            defaultKeyMapOptionsUseCase = mock(),
        )
    }

    @Test
    fun `Enable hold down option for key event actions when the trigger is a DPAD button`() =
        runTest(testDispatcher) {
            configKeyMapState.setKeyMap(
                KeyMap(
                    trigger = singleKeyTrigger(
                        KeyEventTriggerKey(
                            keyCode = KeyEvent.KEYCODE_DPAD_LEFT,
                            device = KeyEventTriggerDevice.Internal,
                            clickType = ClickType.SHORT_PRESS,
                            requiresIme = true,
                        ),
                    ),
                ),
            )

            useCase.addAction(ActionData.InputKeyEvent(keyCode = KeyEvent.KEYCODE_W))

            val actionList = useCase.keyMap.value.dataOrNull()!!.actionList
            assertThat(actionList[0].holdDown, `is`(true))
            assertThat(actionList[0].repeat, `is`(false))
        }

    /**
     * Issue #852. Add a phone ringing constraint when you add an action
     * to answer a phone call.
     */
    @Test
    fun `when add answer phone call action, then add phone ringing constraint`() =
        runTest(testDispatcher) {
            // GIVEN
            configKeyMapState.setKeyMap(KeyMap())
            val action = ActionData.AnswerCall

            // WHEN
            useCase.addAction(action)

            // THEN
            verify(mockConfigConstraintsUseCase).addConstraint(any<ConstraintData.PhoneRinging>())
        }

    /**
     * Issue #852. Add a in phone call constraint when you add an action
     * to end a phone call.
     */
    @Test
    fun `when add end phone call action, then add in phone call constraint`() =
        runTest(testDispatcher) {
            // GIVEN
            configKeyMapState.setKeyMap(KeyMap())
            val action = ActionData.EndCall

            // WHEN
            useCase.addAction(action)

            // THEN
            verify(mockConfigConstraintsUseCase).addConstraint(any<ConstraintData.InPhoneCall>())
        }

    /**
     * issue #593
     */
    @Test
    fun `key map with hold down action, load key map, hold down flag shouldn't disappear`() =
        runTest(testDispatcher) {
            // given
            val action = Action(
                data = ActionData.TapScreen(100, 100, null),
                holdDown = true,
            )

            val keyMap = KeyMap(
                0,
                trigger = singleKeyTrigger(triggerKey(KeyEvent.KEYCODE_0)),
                actionList = listOf(action),
            )

            // when
            configKeyMapState.setKeyMap(keyMap)

            // then
            assertThat(useCase.keyMap.value.dataOrNull()!!.actionList, `is`(listOf(action)))
        }

    @Test
    fun `add modifier key event action, enable hold down option and disable repeat option`() =
        runTest(testDispatcher) {
            KeyEventUtils.MODIFIER_KEYCODES.forEach { keyCode ->
                configKeyMapState.setKeyMap(KeyMap())

                useCase.addAction(ActionData.InputKeyEvent(keyCode))

                useCase.keyMap.value.dataOrNull()!!.actionList
                    .single()
                    .let {
                        assertThat(it.holdDown, `is`(true))
                        assertThat(it.repeat, `is`(false))
                    }
            }
        }
}

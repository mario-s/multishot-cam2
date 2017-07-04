package de.mario.camera


import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith


import org.mockito.Mockito.mock



/**
 */
@RunWith(JUnitPlatform::class)
object ErrorDialogTest : Spek({
    val ID = "foo"

    describe("the error dialog") {

        it("should have a factory method to create the dialog") {
            val instance = ErrorDialog.newInstance(ID)
            assertThat(instance, notNullValue())
        }

    }
})

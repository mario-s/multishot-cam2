package de.mario.camera


import org.hamcrest.CoreMatchers.notNullValue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.junit.Assert.assertThat

/**
 */
@RunWith(JUnitPlatform::class)
object CameraFragmentTest : Spek({

    val ID = "foo"

    describe("the camera fragment") {

        it("should have a factory method to create the fragment") {
            val instance = CameraFragment.newInstance()
            assertThat(instance, notNullValue())
        }

    }
})
package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Direction
import com.example.model.FruitType
import com.example.model.GridPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Pavi Endra Paambu", appName)
  }

  @Test
  fun `test grid point movement and direction`() {
    val start = GridPoint(5, 5)
    val next = start.offset(Direction.UP.dx, Direction.UP.dy)
    assertEquals(GridPoint(5, 4), next)
    assertTrue(Direction.UP.isOpposite(Direction.DOWN))
  }

  @Test
  fun `test fruit types exist with points`() {
    assertTrue(FruitType.APPLE.points > 0)
    assertTrue(FruitType.WATERMELON.points > FruitType.APPLE.points)
    assertEquals("Sevvaappil", FruitType.APPLE.tamilName)
  }
}

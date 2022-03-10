package screen

import LINE_DIVIDER
import extensions.getNotEmptyString

class ShoppingHome: Screen() {

    fun startApp() {
        showGreetingMessage()
        showCategories()
    }

    fun showGreetingMessage() {
        ScreenStack.push(this)
        println("hello! write your name please!")

        val name = readLine().getNotEmptyString()
        println(
            """
        welcome! $name! 
        원하는 카테고리를 입력해주세요! :)
       $LINE_DIVIDER
        """.trimIndent()
        )
    }

    private fun showCategories() {
        val shoppingCategory = ShoppingCategory()
        shoppingCategory.showCategories()
    }

}
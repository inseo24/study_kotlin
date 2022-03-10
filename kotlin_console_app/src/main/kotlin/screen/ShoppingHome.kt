package screen

import extensions.getNotEmptyString

class ShoppingHome {

    fun startApp() {
        showGreetingMessage()
        showCategories()
    }

    fun showGreetingMessage() {
        println("hello! write your name please!")

        val name = readLine().getNotEmptyString()
        println(
            """
        welcome! $name! 
        원하는 카테고리를 입력해주세요! :)
        ====================
        """.trimIndent()
        )
    }

    private fun showCategories() {
        val shoppingCategory = ShoppingCategory()
        shoppingCategory.showCategories()
    }

}
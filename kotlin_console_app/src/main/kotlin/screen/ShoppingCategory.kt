package screen

import extensions.getNotEmptyString
import java.nio.file.attribute.UserDefinedFileAttributeView

class ShoppingCategory: Screen() {

    fun showCategories() {
        ScreenStack.push(this)
        val categories = arrayOf("패션", "전자기기", "반려동물용품")
        for (category in categories) {
            println(category)
        }

        println("=> 장바구니로 이동하려면 #을 입력해주세요.")

        val userSelectedCategory = readLine().getNotEmptyString()

        if (userSelectedCategory == "#") {
            val shoppingCart = ShoppingCart()
            shoppingCart.showCartItems()
        } else {
            if (categories.contains(userSelectedCategory)) {
                val shoppingProductList = ShoppingProductList(userSelectedCategory)
                shoppingProductList.showProducts()
            } else {
                showErrorMessage(userSelectedCategory)
            }
        }
    }

    private fun showErrorMessage(userSelectedCategory: String) {
        println("[$userSelectedCategory] : 존재하지 않는 카테고리입니다. 다시 입력해주세요.")
        showCategories()
    }
}
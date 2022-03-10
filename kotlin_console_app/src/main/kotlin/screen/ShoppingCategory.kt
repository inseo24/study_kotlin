package screen

import extensions.getNotEmptyInt
import extensions.getNotEmptyString

class ShoppingCategory {

    fun showCategories() {
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
                val shoppingProductList = ShoppingProductList()
                shoppingProductList.showProducts(userSelectedCategory)
            } else {

            }
        }
    }
}